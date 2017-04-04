package cn.partytime.config;

import cn.partytime.model.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by administrator on 2017/2/15.
 */
@Component
public class ConfigUtils {

    @Autowired
    private Properties properties;

    public String appName="dmMovie";
    public String testRsyncIp="101.201.80.206";
    public String productionRsyncIp="59.110.148.54";
    private String testRsyncName="testdownload";
    private String productionRsyncName="appbackup";
    private String testDownloadClient = "testclientdownload";
    private String clientdownload ="clientdownload";
    private String executeScript="executeScript";
    private String testExecuteScript="testExecuteScript";
    public String saveFilePath = "resource";
    private String baseTestUrl = "http://test.party-time.cn";
    private String baseUrl = "http://www.party-time.cn";
    private String logTestUrl="http://testlog.party-time.cn";
    private String logUrl="http://log.party-time.cn";

    private String logUrlPath="/log/java";

    public String cmdRsyncFilePath = "/enterX/flash/"+saveFilePath;
    private String tempInitUrl="/v1/api/javaClient/latelyParty";
    private String tempAdTimerDanmuNetUrl="/v1/api/javaClient/findAdTimerDanmu";
    private String tempTimerDanmuNetUrl="/v1/api/javaClient/findTimerDanmuFile";
    private String tempUpdateVersionNetUrl="/v1/api/javaClient/findUpdatePlan";
    private String tempUpdateVersionResultCommitNetUrl="/v1/api/javaClient/updateUpdatePlan";
    private String deviceInfoUrlUrl="/v1/api/admin/device/find";

    private String projectorOpenPath="/tgi/console.tgi?powerOn131658";
    private String projectorClosePath="/tgi/console.tgi?powerOff131047";

    private String updatePlanPath="/v1/api/javaClient/updateUpdatePlan";

    private String paramUrl="/v1/api/javaClient/findFlashConfig";

    private String saveScreenPicUrl="/v1/api/javaClient/saveScreen";

    private String webSocketPath="/ws";

    private String filePath(){
        return properties.getBasePath()+"/enterX";
    }

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

    public String rsyncClientName(){
        if(0==properties.getEnv()){
            return testDownloadClient;
        }else{
            return clientdownload;
        }
    }

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

    public String getScreenSaveFile(){
        return properties.getRegistCode()+".jpg";
    }


    public String findFlashProgramPath() {return filePath() + "/flash";}
    public String findJavaProgramPath() {return filePath() + "/java";}
    public String findBakFlashProgramPath() {return filePath() + "/bak/flash";}
    public String findBakJavaProgramPath() {return filePath() + "/bak/java";}
    public String findVersionJavaPath(){return filePath() + "/version/java"; }
    public String findVersionFlashPath(){return filePath() + "/version/flash"; }
    public String findJavaNewClientPath(){return filePath() + "/newClient/java";}
    public String findFlashNewClientPath(){return filePath() + "/newClient/flash";}

    public String programPath(){return "/enterX/newClient";}

    public String findJavaConfigPath(){return findJavaProgramPath()+"/"+"config";}

    public String realSaveAdtimerFilePath(){
        return rsyncSaveFilePath()+"/adTimerDanmu";
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

    public String getLogUrl(){
        if(0==properties.getEnv()){
            return logTestUrl+logUrlPath;
        }else{
            return logUrl+logUrlPath;
        }
    }

    public String getDeviceInfoUrlUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+deviceInfoUrlUrl+"?addressId="+properties.getAddressId();
        }else{
            return baseUrl+deviceInfoUrlUrl+"?addressId="+properties.getAddressId();
        }
    }

    public String getProjectorOpenUrl(String ip){
        return "http://"+ip+projectorOpenPath;
    }

    public String getProjectorCloseUrl(String ip){
        return "http://"+ip+projectorClosePath;
    }

    public String getAddressId(){
        return  properties.getAddressId();
    }


    public String getParamUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+paramUrl;
        }else{
            return baseUrl+paramUrl;
        }
    }

    public String findUpdatePlanUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+updatePlanPath;
        }else{
            return baseUrl+updatePlanPath;
        }
    }

    public String getSaveScreenPicUrl(){
        if(0==properties.getEnv()){
            return baseTestUrl+saveScreenPicUrl;
        }else{
            return baseUrl+saveScreenPicUrl;
        }
    }

    public String getWebSocketUrl(int port){
        //return getRsyncIp()+webSocketPath+"?code="+properties.getRegistCode()+"&clientType=3";
        return "http://localhost:"+port+"?code="+properties.getRegistCode()+"&clientType=3";
    }

}
