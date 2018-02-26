package cn.partytime.scheduler;


import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.model.UpdatePlanConfig;
import cn.partytime.model.VersionConfig;
import cn.partytime.model.VersionInfo;

import cn.partytime.service.*;
import cn.partytime.util.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    private WindowShellService windowShellService;

    @Autowired
    private RsyncFileService rsyncFileService;

    @Autowired
    private ClientUpdateService clientUpdateService;

    @Autowired
    private ScriptConfigUtils scriptConfigUtils;

    //场地
    @Value("${addressId}")
    private String addressId;

    //场地
    @Value("${projectorStatus:0}")
    private String projectorStatus;

    @Autowired
    private LogLogicService logLogicService;

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


    @Scheduled(cron = "0 */5 * * * ?")
    public void repeatFailedRequest(){
        log.info("http request fail remedy");
        clientUpdateService.repeatRequest();
    }

    @Scheduled(cron = "0 0 8 * * ?")
    private void projectorStart(){
        logLogicService.logUploadHandler("8点开始执行开启投影的定时任务"+" projectorStatus状态是:"+projectorStatus);
        if(!"584a1a9a0cf2fdb8406efdce".equals(addressId) && "1".equals(projectorStatus)){
            try {
                //除了鑫源厅意外，其他的厅都走这个定时任务
                //发送socket链接
                projectorService.executePJLINKCommand(1);
                //启动本地启动脚本
                projectorService.newPjLinkStartOperate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            logLogicService.logUploadHandler("projectorStatus状态是:"+projectorStatus);
        }
    }

}
