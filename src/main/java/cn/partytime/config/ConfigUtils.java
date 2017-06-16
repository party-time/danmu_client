package cn.partytime.config;

import cn.partytime.model.Properties;
import cn.partytime.util.CommonConst;
import cn.partytime.util.CommonUtil;
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



    public String saveFilePath = "resource";

    private String baseTestUrl = "http://test.party-time.cn";
    private String baseUrl = "http://www.party-time.cn";
    private String localUrl="http://localhost";
    private String logTestUrl="http://testlog.party-time.cn";
    private String logUrl="http://log.party-time.cn";
    private String logUrlPath="/log/java";


    public String cmdRsyncFilePath = "/enterX/flash/"+saveFilePath;
    private String baseJavaClientUrl="/v1/api/javaClient";
    private String tempInitUrl=baseJavaClientUrl+"/latelyParty";
    private String tempAdTimerDanmuNetUrl=baseJavaClientUrl+"/findAdTimerDanmu";
    private String tempTimerDanmuNetUrl=baseJavaClientUrl+"/findTimerDanmuFile";
    private String tempUpdateVersionNetUrl=baseJavaClientUrl+"/findUpdatePlan";
    private String tempUpdateVersionResultCommitNetUrl=baseJavaClientUrl+"/updateUpdatePlan";
    private String updatePlanPath=baseJavaClientUrl+"/updateUpdatePlan";
    private String paramUrl=baseJavaClientUrl+"/findFlashConfig";
    private String saveScreenPicUrl=baseJavaClientUrl+"/saveScreen";


    private String webSocketPath="/ws";

    private String filePath(){
        return properties.getBasePath()+"/enterX";
    }


    public Integer getEnv(){
        return  properties.getEnv();
    }
    public String findMachineNumber(){
        return properties.getMachineNum();
    }

    public String getRegisterCode(){
        return properties.getRegistCode();
    }
    public String getRsyncIp(){
        if(0==properties.getEnv()){
            return testRsyncIp;
        }else{
            return productionRsyncIp;
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
        return properties.getAddressId()+"_"+properties.getMachineNum()+".jpg";
    }


    public String findFlashProgramPath() {return filePath() + "/flash";}
    public String findJavaProgramPath() {return filePath() + "/java";}
    public String findBakFlashProgramPath() {return filePath() + "/bak/flash";}
    public String findBakJavaProgramPath() {return filePath() + "/bak/java";}
    public String findVersionJavaPath(){return filePath() + "/version/java"; }
    public String findVersionFlashPath(){return filePath() + "/version/flash"; }
    public String findJavaNewClientPath(){return filePath() + "/newClient/java";}
    public String findFlashNewClientPath(){return filePath() + "/newClient/flash";}

    public String findTeamViewerPath(){return properties.getBasePath()+"/TeamViewer";}

    public String programPath(){return "/enterX/newClient";}

    public String findJavaConfigPath(){return findJavaProgramPath()+"/"+"config";}

    public String realSaveAdtimerFilePath(){
        return rsyncSaveFilePath()+"/adTimerDanmu";
    }

    public String  getDomain(){
        if(0==properties.getEnv()){
            return baseTestUrl;
        }else if(1==properties.getEnv()){
            return baseUrl;
        }else {
            return localUrl;
        }
    }

    public String getLogDomain(){
        if(0==properties.getEnv()){
            return logTestUrl;
        }else if(1==properties.getEnv()){
            return logUrl;
        }else {
            return localUrl;
        }
    }

    public String getUpdateVersionResultCommitNetUrl(){
        return getDomain()+tempUpdateVersionResultCommitNetUrl;
    }

    public String getUpdateVersionUrl(){
        return getDomain()+tempUpdateVersionNetUrl;
    }


    public String getInitUrl(){
        return getDomain()+tempInitUrl;
    }

    public String getAdTimerDanmuNetUrl(){
        return getDomain()+tempAdTimerDanmuNetUrl;
    }

    public String getTimerDanmuNetUrl(){
        return getDomain()+tempTimerDanmuNetUrl;
    }

    public String findLogUrl(){
        return getLogDomain()+logUrlPath;
    }

    public String findSpecialVideosPath() {
        return cmdRsyncFilePath +"/upload"+"/specialVideos";
    }
    public  String  findExpressionsPath() {
        return cmdRsyncFilePath +"/upload"+"/expressions";
    }
    public  String  findSpecialImagesPath(){
        return cmdRsyncFilePath +"/upload"+"/specialImages";
    }
    public  String  findTimerDanmuPath() {
        return cmdRsyncFilePath +"/timerDanmu";
    }
    public  String   findAdTimerDanmuPath(){
        return cmdRsyncFilePath +"/adTimerDanmu";
    }
    public String findUpdateClientDownPath(){
        return  "/enterX/newClient";
    }


    public String getAddressId(){
        return  properties.getAddressId();
    }


    public String getParamUrl(){
        return getDomain()+paramUrl;
    }

    public String findUpdatePlanUrl(){
        return getDomain()+updatePlanPath;
    }

    public String getSaveScreenPicUrl(){
        return getDomain()+saveScreenPicUrl;
    }

    public String getWebSocketUrl(String ip,int port){
        //return getRsyncIp()+webSocketPath+"?code="+properties.getRegistCode()+"&clientType=3";
        return "http://"+ip+":"+port+"?code="+properties.getRegistCode()+"&clientType=2";
    }

    public String getDistributeServerUrl(){
        return getDomain()+"/distribute/client/login/"+properties.getRegistCode();
    }

    public String getProjectorRequestUrl(String command,String status){
        return getDomain()+baseJavaClientUrl+CommonConst.SEPARATOR+command+CommonConst.SEPARATOR+properties.getRegistCode()+CommonConst.SEPARATOR+status;
    }

    public String getPartyRequestUrl(String type,String command){
        return getDomain()+baseJavaClientUrl+ CommonConst.SEPARATOR+type+CommonConst.SEPARATOR+properties.getRegistCode()+CommonConst.SEPARATOR+command;
    }

    public String getPromotionalFilmUrl(String command,String status){
        return getDomain()+baseJavaClientUrl+CommonConst.SEPARATOR+command+CommonConst.SEPARATOR+properties.getRegistCode()+CommonConst.SEPARATOR+status;
    }

    public String findDeviceInfoUrl(){
        return getDomain()+baseJavaClientUrl+CommonConst.SEPARATOR+"findDeviceInfo"+CommonConst.SEPARATOR+properties.getAddressId();
    }

}
