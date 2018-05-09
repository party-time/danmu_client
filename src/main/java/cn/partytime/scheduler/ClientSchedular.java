package cn.partytime.scheduler;


import cn.partytime.config.ClientCache;
import cn.partytime.config.ClientPartyCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.model.*;

import cn.partytime.model.common.RestResultModel;
import cn.partytime.service.*;
import cn.partytime.util.DateUtils;
import cn.partytime.util.FileUtils;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.ListUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/3 0003.
 */

@Service
@Slf4j
@EnableScheduling
public class ClientSchedular {


    @Autowired
    private ProjectorService projectorService;

    @Autowired
    private RsyncFileService rsyncFileService;

    @Autowired
    private ClientUpdateService clientUpdateService;

    @Autowired
    private ClientPartyCache clientPartyCache;

    //场地
    @Value("${addressId}")
    private String addressId;

    //场地
    @Value("${autoMovieStart:0}")
    private Integer autoMovieStart;

    //场地
    @Value("${projectorStatus:0}")
    private String projectorStatus;


    @Value("${basePath}")
    private String basePath;

    @Value("${machineNum}")
    private String machineNum;

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private TmsCommandService tmsCommandService;

    @Autowired
    private CommandExecuteService commandExecuteService;

    @Autowired
    private WindowShellService windowShellService;

    @Autowired
    private ScriptConfigUtils scriptConfigUtils;

    @Scheduled(cron = "0 5 3 * * ?")
    private void cronRsyncFile(){

        //flash资源下载
        logLogicService.logUploadHandler("下载资源");
        rsyncFileService.rsyncFile();
        //flash配置表生成
        logLogicService.logUploadHandler("生成配置表");
        rsyncFileService.createFlashConfig();
        //客户端版本下载
        logLogicService.logUploadHandler("下载客户端");
        rsyncFileService.downloadClient();
        //下载更新计划
        logLogicService.logUploadHandler("生成更新计划");
        clientUpdateService.createUpdatePlanHandler();

        logLogicService.logUploadHandler("下载数据文件");
        rsyncFileService.downloadData();

    }

    @Scheduled(cron = "0 30 4 * * ?")
    private void executeUpdateJava(){
        logLogicService.logUploadHandler("4:30执行java客户端更新");
        commandExecuteService.executeJavaUpdateCallBack();
    }

    @Scheduled(cron = "0 0 5 * * ?")
    private void executeUpdateFlash(){
        logLogicService.logUploadHandler("5:00执行flash客户端更新");
        commandExecuteService.executeFlashUpdateCallBack();
    }



    @Scheduled(fixedRate  = 500)
    public void autoMovieStart() {
        //synchronized (ClientSchedular.class){
            if(!"1".equals(machineNum) || autoMovieStart!=1){
                log.info("不是1号机器，不执行此定时任务");
                return;
            }
            Long danmuStartDate = clientPartyCache.getDanmuStartDate();
            Integer time = clientPartyCache.getAdTime();

            if(time==null){
                logLogicService.logUploadHandler("广告时间为0，定时任务终止");
                return;
            }
            if(danmuStartDate==null){
                logLogicService.logUploadHandler("开始时间为0，定时任务终止");
                return;
            }
            Date currentDate = DateUtils.getCurrentDate();
            long subTime = (currentDate.getTime() - danmuStartDate);
            if (subTime > time * 1000 && subTime - time*1000 < 10 * 1000 && !clientPartyCache.isBooleanMovieStart()) {
                log.info("发送电影开始");

                String resultStr =  tmsCommandService.movieHandler("movie-start", currentDate);
                if(!StringUtils.isEmpty(resultStr)){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RestResultModel restResultModel = JSON.parseObject(resultStr,RestResultModel.class);
                            if(restResultModel.getResult()==200){
                                logLogicService.logUploadHandler("电影开始时间："+DateUtils.dateToString(currentDate,"yyyy-MM-dd hh:mm:ss"));
                                //log.info("电影开始时间：{}",DateUtils.dateToString(currentDate,"yyyy-MM-dd hh:mm:ss"));
                                clientPartyCache.setBooleanMovieStart(true);
                            }
                        }
                    }).start();

                }
            }else{
                clientPartyCache.setBooleanMovieStart(false);
            }
       // }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void repeatFailedRequest(){
        log.info("http request fail remedy");
        clientUpdateService.repeatRequest();
    }

    @Scheduled(cron = "0 0 8 * * ?")
    private void projectorStart(){
        logLogicService.logUploadHandler("8点开始执行开启投影的定时任务"+" projectorStatus状态是:"+projectorStatus);
        //if(!"584a1a9a0cf2fdb8406efdce".equals(addressId) && "1".equals(projectorStatus)){
        if("1".equals(projectorStatus) && !"584a1a9a0cf2fdb8406efdce".equals(addressId)){
            try {
                //除了鑫源厅以外，其他的厅都走这个定时任务
                //发送socket链接
                projectorService.executePJLINKCommand(1);
                //(杭州厅)启动本地启动脚本
                if("5a54368e91289c7e9c525d29".equals(addressId)){
                    projectorService.newPjLinkStartOperate();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            logLogicService.logUploadHandler("projectorStatus状态是:"+projectorStatus);
        }
    }


    @Scheduled(cron = "0 0 2 * * ?")
    private void deleteJavaLog() throws ParseException {
        logLogicService.logUploadHandler("删除本地日志，只保留当天和前一天的");
        String logPath = basePath + File.separator + "log"+File.separator+"danmu_client";
        File file = new File(logPath);
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            return;
        }

        Map<String,String> map = new HashMap<>();
        String currentDateStr = DateUtils.dateToString(new Date(),"yyyy-MM-dd");
        log.info(currentDateStr);
        map.put(currentDateStr,currentDateStr);

        Date beforeDate = DateUtils.DateMinusSomeDay(new Date(),1);
        String beforeDateStr = DateUtils.dateToString(beforeDate,"yyyy-MM-dd");
        log.info(beforeDateStr);
        map.put(beforeDateStr,beforeDateStr);

        for (File f : flist) {
            if (f.isDirectory()) {
                //这里将列出所有的文件夹
                log.info("Dir==>" + f.getName());
                String name = f.getName();
                if(!map.containsKey(name)){
                    //log.info("Dir==>" + f.getName());
                    FileUtils.deleteDir(f);
                }
            }
        }

    }

}
