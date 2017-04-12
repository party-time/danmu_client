package cn.partytime.service;

import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.util.PrintScreenUtils;
import lombok.extern.slf4j.Slf4j;
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
    private RsyncFileService rsyncFileService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ProjectorService projectorService;

    public void executeProjectStartCallBack() {
        projectorService.projectorHandler(0);
    }

    public void executeProjectCloseCallBack() {
        projectorService.projectorHandler(1);
    }

    public void executeProjectChangeCallBack() {
        projectorService.projectorHandler(2);
    }

    public void executeAppRestartCallBack() {
        executeAppCloseCallBack();
        executeAppStartCallBack();
    }

    public void executeAppStartCallBack() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.STARTFLASH_BAT));
        log.info("execute printScreen logic");
        PrintScreenUtils.moveWindow();
    }

    public void executeAppCloseCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.KILLFLASH_BAT));

    }

    public void executeFlashUpdateCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.FLASHUPDATE_VBS));
    }

    public void executeFlashRollBackCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.FLASHROLLBACK_VBS));
    }

    public void executeJavaUpdateCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.JAVAUPDATE_VBS));
    }

    public void executeJavaRollBackCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.JAVAROLLBACK_VBS));
    }

    public void executeVideoDownCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.SPECIALVIDEOS_BAT));
    }

    public void executeExpressionDownCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.EXPRESSIONS_BAT));
    }

    public void executeSpecialImgDownCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.SPECIALIMAGES_BAT));
    }

    public void executeTimerDmDownCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.TIMERDANMU_BAT));
    }

    public void executeAdDmDownCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.ADTIMERDANMU_BAT));
    }

    public void executeUpdateClientDownCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.CLIENTDOWNLOAD_BAT));
    }

    public void executeConfigCreateCallBack() {
        rsyncFileService.createFlashConfig();
    }

    public void executeTeamViewStartCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.STARTTEAMVIEWER_BAT));
    }

    public void executeScreenPicCallBack() {
        windowShellService.printScreenPic();
    }

    public void executeTeamViewCloseCallBack() {
        windowShellService.execExe(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.KILLTEAMVIEWER_BAT));
    }

    public void executeScriptCreateCallBack() {
        scriptFileService.createShell();
    }




}
