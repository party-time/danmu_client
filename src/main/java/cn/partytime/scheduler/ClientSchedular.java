package cn.partytime.scheduler;


import cn.partytime.config.ClientCache;
import cn.partytime.config.ClientPartyCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.model.*;

import cn.partytime.model.common.RestResultModel;
import cn.partytime.service.*;
import cn.partytime.util.DateUtils;
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

import java.util.Date;
import java.util.List;

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



    @Value("${machineNum}")
    private String machineNum;

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private TmsCommandService tmsCommandService;

    @Scheduled(cron = "0 5 3 * * ?")
    private void cronRsyncFile(){
        //flash资源下载
        rsyncFileService.rsyncFile();
        //flash配置表生成
        rsyncFileService.createFlashConfig();
        //客户端版本下载
        rsyncFileService.downloadClient();
        //下载更新计划
        clientUpdateService.createUpdatePlanHandler();
    }

    @Scheduled(cron = "0/1 * * * * ?")
    public void autoMovieStart() {
        synchronized (ClientSchedular.class){
            if(!"1".equals(machineNum) || autoMovieStart!=1){
                log.info("不是1号机器，不执行此定时任务");
                return;
            }
            Long danmuStartDate = clientPartyCache.getDanmuStartDate();
            Integer time = clientPartyCache.getAdTime();

            if(time==null){
                System.out.println("广告时间为0，定时任务终止");
                return;
            }
            if(danmuStartDate==null){
                System.out.println("开始时间为0，定时任务终止");
                return;
            }
            Date currentDate = DateUtils.getCurrentDate();
            long subTime = (currentDate.getTime() - danmuStartDate)/1000;
            if (subTime > time && subTime - time < 10 && !clientPartyCache.isBooleanMovieStart()) {
                String resultStr =  tmsCommandService.movieHandler("movie-start", DateUtils.getCurrentDate());
                if(!StringUtils.isEmpty(resultStr)){
                    RestResultModel restResultModel = JSON.parseObject(resultStr,RestResultModel.class);
                    if(restResultModel.getResult()==200){
                        clientPartyCache.setBooleanMovieStart(true);
                    }
                }
            }else{
                clientPartyCache.setBooleanMovieStart(false);
            }
        }
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
        if("1".equals(projectorStatus)){
            try {
                //除了鑫源厅意外，其他的厅都走这个定时任务
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

}
