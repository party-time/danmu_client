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
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.KILLFLASH_BAT));
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.STARTFLASH_BAT));
    }

    public void executeAppStartCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.STARTFLASH_BAT));
        PrintScreenUtils.open2Screen();
    }

    public void executeAppCloseCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.KILLFLASH_BAT));

    }

    public void executeFlashUpdateCallBack() {
        windowShellService.execShell("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.FLASHUPDATE_VBS));
    }

    public void executeFlashRollBackCallBack() {
        windowShellService.execShell("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.FLASHROLLBACK_VBS));
    }

    public void executeJavaUpdateCallBack() {
        windowShellService.execShell("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.JAVAUPDATE_VBS));
    }

    public void executeJavaRollBackCallBack() {
        windowShellService.execShell("cscript "+scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.JAVAROLLBACK_VBS));
    }

    public void executeVideoDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.SPECIALVIDEOS_BAT));
    }

    public void executeExpressionDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.EXPRESSIONS_BAT));
    }

    public void executeSpecialImgDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.SPECIALIMAGES_BAT));
    }

    public void executeTimerDmDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.TIMERDANMU_BAT));
    }

    public void executeAdDmDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.ADTIMERDANMU_BAT));
    }

    public void executeConfigCreateCallBack() {
        rsyncFileService.createFlashConfig();
    }

    public void executeTeamViewStartCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.STARTTEAMVIEWER_BAT));
    }

    public void executeScreenPicCallBack() {
        windowShellService.printScreenPic();
    }

    public void executeTeamViewCloseCallBack() {
        windowShellService.execShell(scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.KILLTEAMVIEWER_BAT));
    }

    public void executeScriptCreateCallBack() {
        scriptFileService.createShell();
    }




}
