package cn.partytime.config;

import cn.partytime.util.CommonConst;
import cn.partytime.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

@Component
public class ScriptConfigUtils {


    @Autowired
    private ConfigUtils configUtils;

    public final String COMMON_VBS="common";
    public final String START_VBS="start";
    public final String JAVAUPDATE_VBS="javaUpdate";
    public final String JAVAUPDATECOMMON_VBS="javaUpdateCommon";
    public final String JAVAROLLBACK_VBS="javaRollback";
    public final String FLASHROLLBACK_VBS="flashRollback";
    public final String FLASHUPDATE_VBS="flashUpdate";
    public final String FLASHUPDATECOMMON_VBS="flashUpdateCommon";
    public final String TIMERFLASHUPDATE_VBS="timerflashUpdate";
    public final String TIMERJAVAUPDATE_VBS="timerjavaUpdate";
    public final String JAVASTART_BAT="javaStart";
    public final String KILLFLASH_BAT="killFlash";
    public final String STARTFLASH_BAT="startFlash";
    public final String KILLTEAMVIEWER_BAT="killTeamViewer";
    public final String STARTTEAMVIEWER_BAT="startTeamViewer";
    public final String FLASHROLLBACK_SH="flashRollback";
    public final String FLASHUPDATE_SH="flashUpdate";
    public final String JAVAROLLBACK_SH="javaRollback";
    public final String JAVAUPDATE_SH="javaUpdate";
    private final String VERSION_NAME="version";
    public final String UPDATE_PLAN="updatePlan";

    public final String SPECIALVIDEOS_BAT="specialVideos";
    public final String EXPRESSIONS_BAT="expressions";
    public final String SPECIALIMAGES_BAT="specialImages";
    public final String TIMERDANMU_BAT="timerDanmu";
    public final String ADTIMERDANMU_BAT="adTimerDanmu";
    public final String CLIENTDOWNLOAD_BAT="clientdownload";
    public final String RESOURCE_BAT="resource";

    public final String RSYNCRESOURCEDOWN_BAT="rsyncResourceDown";
    private final String UPDATE_FREEMARKER_PATH="updateScript";


    public final  String FTL_TYPE="ftl";
    public final  String BAT_TYPE="bat";
    public final  String SH_TYPE="sh";
    public final  String VBS_TYPE="vbs";

    public final String JAVA_PROPERTIESE_NAME="application.properties";
    public final String JAVA_JAR_NAME="danmu_java_client.jar";
    public final String FLASH_START_NAME="dmMovie.exe";
    public final String TEAMVIEWER_START_NAME="TeamViewer.exe";
    public String findJavaJarPath(){
        return configUtils.findJavaProgramPath()+CommonConst.SEPARATOR+JAVA_JAR_NAME;
    }

    public String findJavaPropertiesPath(){
        return configUtils.findJavaProgramPath()+CommonConst.SEPARATOR+JAVA_PROPERTIESE_NAME;
    }
    public String findFlashStartExe(){return configUtils.findFlashProgramPath()+CommonConst.SEPARATOR+FLASH_START_NAME;}

    public String findTeamViewerExe(){return configUtils.findTeamViewerPath()+CommonConst.SEPARATOR+TEAMVIEWER_START_NAME;}

    public String findEecuteScriptDirectory() {
        return configUtils.shellPath();
    }

    public String findFreemarkerPath(String name,String type){
        return UPDATE_FREEMARKER_PATH+CommonConst.SEPARATOR+type+ CommonConst.SEPARATOR+name+ CommonUtil.convertFirstWordUpperCase(type)+CommonConst.COMMA+FTL_TYPE;
    }
    public String fineScriptPath(String type,String name){
        return findEecuteScriptDirectory()+CommonConst.SEPARATOR+name+CommonConst.COMMA+type;
    }


    public String findJavaUpdatePlan() {
        return configUtils.findVersionJavaPath() +CommonConst.SEPARATOR+UPDATE_PLAN;
    }
    public String findFlashUpdatePlan() { return configUtils.findVersionFlashPath()  +CommonConst.SEPARATOR+UPDATE_PLAN;}
    public String findJavaVersionPath() {return configUtils.findJavaProgramPath() + CommonConst.SEPARATOR+VERSION_NAME;}
    public String findFlashVersionPath() {return configUtils.findFlashProgramPath() + CommonConst.SEPARATOR+VERSION_NAME;}
    public String findBakJavaVersionPath() {return configUtils.findBakJavaProgramPath() + CommonConst.SEPARATOR+VERSION_NAME;}
    public String findBakFlashVersionPath() {return configUtils.findBakFlashProgramPath()+ CommonConst.SEPARATOR+VERSION_NAME;  }



}
