package cn.partytime.config;

import cn.partytime.model.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by administrator on 2017/2/15.
 */
@Service
public class ConfigUtils {

    @Autowired
    private Properties properties;


    public String appName="dmMovie";

    private String filePath(){
        return properties.getBasePath()+"/enterX";
    }

    public String testRsyncIp="101.201.80.206";
    public String productionRsyncIp="59.110.148.54";

    private String testRsyncName="testdownload";

    private String productionRsyncName="appbackup";

    public String getRsyncIp(){
        if(0==properties.getEnv()){
            return testRsyncIp;
        }else{
            return productionRsyncIp;
        }
    }

    public String rsyncName(){
        if(0==properties.getEnv()){
            return testRsyncName;
        }else{
            return productionRsyncName;
        }
    }

    private String testDownloadClient = "testclientdownload";

    private String clientdownload ="clientdownload";


    public String rsyncClientName(){
        if(0==properties.getEnv()){
            return testDownloadClient;
        }else{
            return clientdownload;
        }
    }

    private String executeScript="executeScript";

    private String testExecuteScript="testExecuteScript";

    public String getExecuteScriptName(){
        if(0==properties.getEnv()){
            return testExecuteScript;
        }else{
            return executeScript;
        }
    }


    public String getMachineNum(){
        return properties.getMachineNum();
    }

    public String rsyncPasswordFile(){
        return filePath()+"/rsync/rsync.secrets";
    }

    public String saveFilePath = "resource";

    public String cmdRsyncFilePath = "/enterX/flash/"+saveFilePath;

    public String rsyncSaveFilePath(){
        return filePath()+"/flash/" + saveFilePath;
    }

    public String realSaveTimerFilePath(){
        return rsyncSaveFilePath()+"/timerDanmu";
    }

    public String shellPath(){
        return filePath()+"/bin";
    }

    public String screenSavePath(){
        return filePath()+"/screenPic";
    }

    public String programJavaPath(){
        return filePath() +"/java";
    }

    public String programFlashPath(){
        return filePath() + "/flash";
    }

    public String programPath(){

        return "/enterX/newClient";
    }

    public String getEecuteScriptPath(){
        return "/enterX/bin";
    }

    public String programNewJavaPath (){
        return filePath() + "/newClient/java";
    }

    public String programNewFlashPath(){
        return filePath() + "/newClient/flash";
    }

    public String realSaveAdtimerFilePath(){
        return rsyncSaveFilePath()+"/adTimerDanmu";
    }

    private String baseTestUrl = "http://127.0.0.1";

    private String baseUrl = "http://www.party-time.cn";

    private String tempInitUrl="/v1/api/javaClient/latelyParty";

    private String tempAdTimerDanmuNetUrl="/v1/api/javaClient/findAdTimerDanmu";

    private String tempTimerDanmuNetUrl="/v1/api/javaClient/findTimerDanmuFile";

    private String tempUpdateVersionNetUrl="/v1/api/javaClient/findUpdatePlan";

    private String tempUpdateVersionResultCommitNetUrl="/v1/api/javaClient/updateUpdatePlan";

    private String paramUrl = "/v1/api/javaClient/findFlashConfig";

    private String javaVersionDirectory= "/version/java";

    private String flashVersionDirectory= "/version/flash";

    private String javaUpateVbsName = "/javaUpdate.vbs";

    private String flashUpateVbsName = "/flashUpdate.vbs";

    private String timerJavaUpateVbsName = "/timerjavaUpdate.vbs";

    private String timerFlashUpateVbsName = "/timerflashUpdate.vbs";

    private String javaUpdateShellName = "/javaUpdate.sh";
    private String flashUpdateShellName = "/flashUpdate.sh";
    private String javaRollbackShellName = "/javaRollback.sh";
    private String flashRollbackShellName = "/flashRollback.sh";


    private String javaRollbackVbsName = "/javaRollback.vbs";
    private String flashRollbackVbsName = "/flashRollback.vbs";




    public String getJavaRollbackShellPath(){
        return shellPath()+javaRollbackShellName;
    }

    public String getFlashRollbackShellPath(){
        return shellPath()+flashRollbackShellName;
    }

    public String getJaveUpdateVbsPath(){
        return shellPath()+javaUpateVbsName;
    }
    public String getFlashUpdateVbsPath(){  return shellPath()+flashUpateVbsName; }

    public String getTimerJaveUpdateVbsPath(){
        return shellPath()+timerJavaUpateVbsName;
    }
    public String getTimerFlashUpdateVbsPath(){  return shellPath()+timerFlashUpateVbsName; }

    public String getJavaRollbackVbsPath(){ return shellPath()+javaRollbackVbsName;}

    public String getFlashRollbackVbsPath(){ return shellPath()+flashRollbackVbsName;}

    public String getJavaVersionDirectory() {
        return filePath()+javaVersionDirectory;
    }

    public String getFlashVersionDirectory(){
        return filePath()+flashVersionDirectory;
    }

    public String  getDomain(){
        if(0==properties.getEnv()){
            return baseTestUrl;
        }else{
            return baseUrl;
        }
    }

    public String getUpdateVersionResultCommitNetUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+tempUpdateVersionResultCommitNetUrl;
        }else{
            return baseUrl+tempUpdateVersionResultCommitNetUrl;
        }
    }

    public String getUpdateVersionUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+tempUpdateVersionNetUrl;
        }else{
            return baseUrl+tempUpdateVersionNetUrl;
        }
    }


    public String getInitUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+tempInitUrl;
        }else{
            return baseUrl+tempInitUrl;
        }
    }

    public String getAdTimerDanmuNetUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+tempAdTimerDanmuNetUrl;
        }else{
            return baseUrl+tempAdTimerDanmuNetUrl;
        }
    }

    public String getTimerDanmuNetUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+tempTimerDanmuNetUrl;
        }else{
            return baseUrl+tempTimerDanmuNetUrl;
        }
    }

    public String getParamUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+paramUrl;
        }else{
            return baseUrl+paramUrl;
        }
    }

}
