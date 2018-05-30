package cn.partytime.service;

import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.model.Properties;
import cn.partytime.util.CommandConst;
import cn.partytime.util.PrintScreenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/1 0001.
 */

@Service
@Slf4j
public class CommandExecuteService {

    @Autowired
    private Properties properties;

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private WindowShellService windowShellService;

    @Autowired
    private ScriptFileService scriptFileService;

    @Autowired
    private ScriptConfigUtils scriptConfigUtils;

    @Autowired
    private RsyncFileService rsyncFileService;

    @Autowired
    private ClientUpdateService clientUpdateService;

    @Autowired
    private ProjectorService projectorService;

    @Autowired
    private MessageSendToCollectorServer messageSendToCollectorServer;

    @Autowired
    private ConfigUtils configUtils;

    public void executeProjectorStartCallBack() {
        //projectorService.projectorHandler(0);
        logLogicService.logUploadHandler("投影仪开启");
        projectorService.projectSendCommand(CommandConst.PROJECTOR_START,0);
    }

    public void executeProjectorCloseCallBack() {
        //projectorService.projectorHandler(1);
        logLogicService.logUploadHandler("投影仪关闭");
        projectorService.projectSendCommand(CommandConst.PROJECTOR_CLOSE,1);
    }

    public void executeProjectorNewStartCallBack() {
        logLogicService.logUploadHandler("投影仪开启");
        projectorService.projectNewStartCommand(CommandConst.PROJECTOR_START,0);
    }

    public void executeProjectChangeCallBack() {
        projectorService.projectorHandler(2);
    }

    public void executeAppRestartCallBack() {
        executeAppCloseCallBack();
        executeAppStartCallBack();
    }

    public void executeAppStartCallBack() {
        if(!"3".equals(properties.getMachineNum())) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            windowShellService.execExe(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.STARTFLASH_BAT));
            log.info("execute printScreen logic");
            PrintScreenUtils.moveWindow();
            /*Map<String,Object> map = new HashMap<>();
            map.put("data",true);
            map.put("type","startStageAndFull");
            map.put("clientType","2");
            map.put("code",configUtils.getRegisterCode());*/
            //messageSendToCollectorServer.sendMessageToCollectorServer(map);

        }
    }

    public void executeAppCloseCallBack() {
        windowShellService.execExe(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.KILLFLASH_BAT));
    }

    public void executeFlashUpdateCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.findScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.FLASHUPDATE_VBS));
    }

    public void executeFlashRollBackCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.findScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.FLASHROLLBACK_VBS));
    }

    public void executeJavaUpdateCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.findScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.JAVAUPDATE_VBS));
    }

    public void executeJavaRollBackCallBack() {
        windowShellService.execExe("cscript "+scriptConfigUtils.findScriptPath(scriptConfigUtils.VBS_TYPE, scriptConfigUtils.JAVAROLLBACK_VBS));
    }

    public void executeVideoDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.SPECIALVIDEOS_BAT));
    }

    public void executeExpressionDownCallBack() {
        String shellPath = scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.EXPRESSIONS_BAT);
        windowShellService.execShell(shellPath);
    }

    public void executeSpecialImgDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.SPECIALIMAGES_BAT));
    }

    public void executeTimerDmDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.TIMERDANMU_BAT));
    }

    public void executeAdDmDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.ADTIMERDANMU_BAT));
    }

    public void executeResourceAllDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.RESOURCE_BAT));
    }

    public void executeUpdateClientDownCallBack() {
        windowShellService.execShell(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.CLIENTDOWNLOAD_BAT));
    }

    public void executeUpdatePlanCreateCallBack() {
        clientUpdateService.createUpdatePlanHandler();
    }

    public void executeConfigCreateCallBack() {
        rsyncFileService.createFlashConfig();
    }

    public void executeTeamViewStartCallBack() {
        windowShellService.execExe(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.STARTTEAMVIEWER_BAT));
    }

    public void executeScreenPicCallBack() {
        windowShellService.printScreenPic();
    }

    public void executeTeamViewCloseCallBack() {
        windowShellService.execExe(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.KILLTEAMVIEWER_BAT));
    }

    public void executeScriptCreateCallBack() {
        scriptFileService.createShell();
    }

    public void executeDataDownCallBack(){
        //下载数据文件
        windowShellService.execShell(scriptConfigUtils.findScriptPath(scriptConfigUtils.BAT_TYPE, scriptConfigUtils.DATA_BAT));
    }
}
