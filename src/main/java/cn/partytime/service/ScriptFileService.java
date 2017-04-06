package cn.partytime.service;


import cn.partytime.config.ConfigUtils;
import cn.partytime.model.Properties;
import cn.partytime.config.ScriptConfigUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2017/3/30 0030.
 */

@Service
@Slf4j
public class ScriptFileService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private ScriptConfigUtils scriptConfigUtils;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private Properties properties;


    public void createShell(){
        Map<String ,Object> map = new HashMap<>();

        //common
        map.put("rootPath",properties.getBasePath());
        map.put("javaJarPath",scriptConfigUtils.findJavaJarPath());
        map.put("javaPropertiesPath",scriptConfigUtils.findJavaPropertiesPath());
        map.put("flashStartExe",scriptConfigUtils.findFlashStartExe());
        map.put("javaPath",configUtils.findJavaProgramPath());
        map.put("flashPath",configUtils.findFlashProgramPath());
        map.put("javaNewClietPath",configUtils.findJavaNewClientPath());
        map.put("flashNewClietPath",configUtils.findFlashNewClientPath());
        map.put("javaBakClietPath",configUtils.findBakJavaProgramPath());
        map.put("flashBakClietPath",configUtils.findBakFlashProgramPath());

        map.put("javaUpdatePlan",scriptConfigUtils.findJavaUpdatePlan());
        map.put("flashUpdatePlan",scriptConfigUtils.findFlashUpdatePlan());

        map.put("flashCurrentVersionPath",scriptConfigUtils.findFlashVersionPath());
        map.put("javaCurrentVersionPath",scriptConfigUtils.findJavaVersionPath());

        map.put("flashBakVersionPath",scriptConfigUtils.findBakJavaVersionPath());
        map.put("javaBakVersionPath",scriptConfigUtils.findBakFlashVersionPath());

        map.put("updatePlanCommitUrl",configUtils.findUpdatePlanUrl());

        //bat param
        map.put("rsyncPasswordPath",configUtils.rsyncPasswordFile());
        map.put("serverIp",configUtils.getRsyncIp());

        map.put("javaStartBatPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.BAT_TYPE,scriptConfigUtils.JAVASTART_BAT));
        map.put("commvbsPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE,scriptConfigUtils.COMMON_VBS));
        map.put("flashRollBakShellPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.SH_TYPE,scriptConfigUtils.FLASHROLLBACK_SH));
        map.put("flashUpdateShellPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.SH_TYPE,scriptConfigUtils.FLASHUPDATE_SH));
        map.put("flashCommonUpdateVbsPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE,scriptConfigUtils.FLASHUPDATECOMMON_VBS));

        map.put("javaRollBakShellPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.SH_TYPE,scriptConfigUtils.JAVAROLLBACK_SH));
        map.put("javaUpdateShellPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.SH_TYPE,scriptConfigUtils.JAVAUPDATE_SH));
        map.put("javaCommonUpdateVbsPath",scriptConfigUtils.fineScriptPath(scriptConfigUtils.VBS_TYPE,scriptConfigUtils.JAVAUPDATECOMMON_VBS));


        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.COMMON_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.START_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.JAVAUPDATE_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.JAVAUPDATECOMMON_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.JAVAROLLBACK_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.FLASHROLLBACK_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.FLASHUPDATE_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.FLASHUPDATECOMMON_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.TIMERFLASHUPDATE_VBS);
        createShellFile(map,scriptConfigUtils.VBS_TYPE,scriptConfigUtils.TIMERJAVAUPDATE_VBS);
        createShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.JAVASTART_BAT);
        createShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.KILLFLASH_BAT);
        createShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.STARTFLASH_BAT);
        createShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.KILLTEAMVIEWER_BAT);
        createShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.STARTTEAMVIEWER_BAT);
        createShellFile(map,scriptConfigUtils.SH_TYPE,scriptConfigUtils.FLASHROLLBACK_SH);
        createShellFile(map,scriptConfigUtils.SH_TYPE,scriptConfigUtils.FLASHUPDATE_SH);
        createShellFile(map,scriptConfigUtils.SH_TYPE,scriptConfigUtils.JAVAROLLBACK_SH);
        createShellFile(map,scriptConfigUtils.SH_TYPE,scriptConfigUtils.JAVAUPDATE_SH);

        map.put("rsyncScriptPath",configUtils.findSpecialVideosPath());
        map.put("resourceType",scriptConfigUtils.SPECIALVIDEOS_BAT);
        createRsyncDownloadShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.RSYNCRESOURCEDOWN_BAT,scriptConfigUtils.SPECIALVIDEOS_BAT);

        map.put("rsyncScriptPath",configUtils.findExpressionsPath());
        map.put("resourceType",scriptConfigUtils.EXPRESSIONS_BAT);
        createRsyncDownloadShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.RSYNCRESOURCEDOWN_BAT,scriptConfigUtils.EXPRESSIONS_BAT);

        map.put("rsyncScriptPath",configUtils.findSpecialImagesPath());
        map.put("resourceType",scriptConfigUtils.SPECIALIMAGES_BAT);
        createRsyncDownloadShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.RSYNCRESOURCEDOWN_BAT,scriptConfigUtils.SPECIALIMAGES_BAT);

        map.put("rsyncScriptPath",configUtils.findTimerDanmuPath());
        map.put("resourceType",scriptConfigUtils.TIMERDANMU_BAT);
        createRsyncDownloadShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.RSYNCRESOURCEDOWN_BAT,scriptConfigUtils.TIMERDANMU_BAT);

        map.put("rsyncScriptPath",configUtils.findAdTimerDanmuPath());
        map.put("resourceType",scriptConfigUtils.ADTIMERDANMU_BAT);
        createRsyncDownloadShellFile(map,scriptConfigUtils.BAT_TYPE,scriptConfigUtils.RSYNCRESOURCEDOWN_BAT,scriptConfigUtils.ADTIMERDANMU_BAT);
    }

    public void createRsyncDownloadShellFile(Map<String, Object> model,String type,String freemakerName,String fileName){
        String freemarkerPath = scriptConfigUtils.findFreemarkerPath(freemakerName,type);
        String filePath = scriptConfigUtils.fineScriptPath(type,fileName);
        createFile(model,freemarkerPath,filePath);
    }

    public void createShellFile(Map<String, Object> model,String type,String fileName){
        String freemarkerPath = scriptConfigUtils.findFreemarkerPath(fileName,type);
        String filePath = scriptConfigUtils.fineScriptPath(type,fileName);
        createFile(model,freemarkerPath,filePath);
    }


    public void createFile(Map<String, Object> model,String freemarkerPath,String filePath){
        Template template = null; // freeMarker template
        String content = null;
        FileOutputStream fos = null;
        try {
            template = configuration.getTemplate(freemarkerPath);
            content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        File file = new File(filePath);

        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } catch (FileNotFoundException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }
}
