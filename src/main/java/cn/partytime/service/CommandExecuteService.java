package cn.partytime.service;

import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/4/1 0001.
 */

@Service
@Slf4j
public class CommandExecuteService {


    @Autowired
    private WindowShellService windowShellService;

    @Autowired
    private ScriptFileService scriptFileService;

    @Autowired
    private ScriptConfigUtils scriptConfigUtils;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ProjectorService projectorService;

    public void startProjector(){
        projectorService.projectorHandler(0);
    }
    public void stopProjector(){
        projectorService.projectorHandler(1);
    }

    public void blankProjector(){
        projectorService.projectorHandler(2);
    }

    public void startApp(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE,scriptConfigUtils.STARTFLASH_BAT));
    }
    public void stopApp(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE,scriptConfigUtils.KILLFLASH_BAT));
    }

    public void restartApp(){
        stopApp();
        startApp();
    }

    public void upgradeFlash(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE,scriptConfigUtils.FLASHUPDATE_VBS));
    }
    public void rollbackFlash(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE,scriptConfigUtils.FLASHROLLBACK_VBS));
    }
    public void upgradeJava(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE,scriptConfigUtils.JAVAUPDATE_VBS));
    }
    public void rollbackJava(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE,scriptConfigUtils.JAVAROLLBACK_VBS));
    }
    public void createScriptFile(){
        scriptFileService.createShell();
    }

    public void downloadVideoResource(){

    }
    public void downloadExpressionResource(){}
    public void downloadSpecialPictureResource(){}
    public void downloadTimerDanmuResource(){}
    public void downloadAdTimeDanmuResource(){}


    public void createConfig(){

    }


    /**
     * 开启teamviewer
     * */
    public void startTeamviewr(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE,scriptConfigUtils.STARTTEAMVIEWER_BAT));
    }

    /**
     * 关闭teamviewer
     */
    public void stopTeamviewr(){
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE,scriptConfigUtils.KILLTEAMVIEWER_BAT));
    }


}
