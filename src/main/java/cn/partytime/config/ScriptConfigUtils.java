package cn.partytime.config;

import cn.partytime.config.ConfigUtils;
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
    private final String UPDATE_FREEMARKER_PATH="updateScript";

    public final String SEPARATOR="/";
    private final String COMMA=".";

    public final  String FTL_TYPE="ftl";
    public final  String BAT_TYPE="bat";
    public final  String SH_TYPE="sh";
    public final  String VBS_TYPE="vbs";

    public final String JAVA_PROPERTIESE_NAME="application.properties";
    public final String JAVA_JAR_NAME="danmu_java_client.jar";
    public final String FLASH_START_NAME="dmMovie.exe";
    public String findJavaJarPath(){
        return configUtils.findJavaProgramPath()+SEPARATOR+JAVA_JAR_NAME;
    }
    public String findJavaPropertiesPath(){
        return configUtils.findJavaProgramPath()+SEPARATOR+JAVA_PROPERTIESE_NAME;
    }
    public String findFlashStartExe(){return configUtils.findFlashProgramPath()+SEPARATOR+FLASH_START_NAME;}










    public String findEecuteScriptDirectory() {
        return configUtils.shellPath();
    }

    public String findFreemarkerPath(String name,String type){
        return UPDATE_FREEMARKER_PATH+SEPARATOR+type+SEPARATOR+name+convertFirstWordUpperCase(type)+COMMA+FTL_TYPE;
    }
    public String fineScriptPath(String type,String name){
        return findEecuteScriptDirectory()+SEPARATOR+name+COMMA+type;
    }



    public String convertFirstWordUpperCase(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public String findJavaUpdatePlan() {
        return configUtils.findVersionJavaPath() +SEPARATOR+UPDATE_PLAN;
    }
    public String findFlashUpdatePlan() { return configUtils.findVersionFlashPath()  +SEPARATOR+UPDATE_PLAN;}
    public String findJavaVersionPath() {return configUtils.findJavaProgramPath() + SEPARATOR+VERSION_NAME;}
    public String findFlashVersionPath() {return configUtils.findFlashProgramPath() + SEPARATOR+VERSION_NAME;}
    public String findBakJavaVersionPath() {return configUtils.findBakJavaProgramPath() + SEPARATOR+VERSION_NAME;}
    public String findBakFlashVersionPath() {return configUtils.findBakFlashProgramPath()+ SEPARATOR+VERSION_NAME;  }



}
