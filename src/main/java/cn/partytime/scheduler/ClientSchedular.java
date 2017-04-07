package cn.partytime.scheduler;


import cn.partytime.service.ClientUpdateService;
import cn.partytime.service.RsyncFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        rsyncFileService.downloadExecuteShell();
    }

    //@Scheduled(cron = "0 */10 * * * ?")
    /*public void planSchtasks(){
        log.info("execute update plan");
        VersionConfig versionConfig = clientUpdateService.findVersionConfig();
        if(versionConfig!=null){
            List<VersionInfo> versionInfoList = versionConfig.getData();
            if(ListUtils.checkListIsNotNull(versionInfoList)){
                for(VersionInfo versionInfo:versionInfoList){
                    clientUpdateService.createSchtasks(versionInfo);
                }
            }
        }
    }*/

    @Scheduled(cron = "0 */5 * * * ?")
    public void repeatFailedRequest(){
        log.info("http request fail remedy");
        clientUpdateService.repeatRequest();
    }

}
