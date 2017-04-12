package cn.partytime.scheduler;


import cn.partytime.model.UpdatePlanConfig;
import cn.partytime.model.VersionConfig;
import cn.partytime.model.VersionInfo;

import cn.partytime.service.RsyncFileService;
import cn.partytime.service.ClientUpdateService;
import cn.partytime.util.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RsyncFileService rsyncFileService;

    @Autowired
    private ClientUpdateService clientUpdateService;

    @Scheduled(cron = "0 5 3 * * ?")
    private void cronRsyncFile(){
        //flash资源下载
        rsyncFileService.rsyncFile();
        //flash配置表生成
        rsyncFileService.createFlashConfig();
        //客户端版本下载
        rsyncFileService.downloadClient();
        //下载执行脚本
        //rsyncFileService.downloadExecuteShell();
    }



    @Scheduled(cron = "0 */10 * * * ?")
    public void planSchtasks(){
        log.info("execute update plan");
        clientUpdateService.createUpdatePlanHandler();
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void repeatFailedRequest(){
        log.info("http request fail remedy");
        clientUpdateService.repeatRequest();
    }

}
