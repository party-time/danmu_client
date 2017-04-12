package cn.partytime.service;


import cn.partytime.config.ConfigUtils;
import cn.partytime.config.ScriptConfigUtils;
import cn.partytime.json.RestResult;
import cn.partytime.model.*;
import cn.partytime.util.FileUtils;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.ListUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by yang on 2017/2/17.
 */
@Service
@Slf4j
public class ClientUpdateService {

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private Properties properties;


    @Autowired
    private ScriptConfigUtils scriptConfigUtils;


    public void createUpdatePlanHandler(){
        log.info("execute update plan");
        UpdatePlanConfig versionConfig = findVersionConfig();
        if(versionConfig!=null){
            List<VersionInfo> versionInfoList = versionConfig.getData();
            if(ListUtils.checkListIsNotNull(versionInfoList)){
                for(VersionInfo versionInfo:versionInfoList){
                    createSchtasks(versionInfo);
                }
            }
        }
    }




    public boolean setRequestResult(VersionInfo versionInfo, String machineNum,String status, int code){
        try {
            ClientVersion clientVersion = new ClientVersion();
            clientVersion.setId(versionInfo.getId());
            clientVersion.setVersion(versionInfo.getVersion());
            clientVersion.setMachineNum(machineNum);
            clientVersion.setUpdateDate(versionInfo.getUpdateDate());
            clientVersion.setStatus(status);
            clientVersion.setCode(code);
            clientVersion.setDomainName(configUtils.getDomain());
            String str = JSON.toJSONString(clientVersion);

            int type = versionInfo.getType();
            String filePath = "";
            if(type==0){
                filePath = configUtils.findVersionJavaPath()+ File.separator+scriptConfigUtils.UPDATE_PLAN;
            }else{
                filePath = configUtils.findVersionFlashPath()+ File.separator+scriptConfigUtils.UPDATE_PLAN;
            }
            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
                FileUtils.outputFile(filePath,str);
                return true;
            }else{
                FileUtils.outputFile(filePath,str);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }




    public static List<String> findFileList(File f){
        List<String> fileList = new ArrayList<>();
        if(f!=null){
            if(f.isDirectory()){
                File[] fileArray=f.listFiles();
                if(fileArray!=null){
                    for (int i = 0; i < fileArray.length; i++) {
                        fileList.add(fileArray[i].getName());
                    }
                }
            }
        }
        return fileList;
    }


    public void repeatRequest(){
        String javaPath = configUtils.findVersionJavaPath();
        String flashPath = configUtils.findVersionFlashPath();
        sendReqeust(javaPath,0);
        sendReqeust(flashPath,1);
    }

    private void sendReqeust(String path,int type){
        List<String> fileList = findFileList(new File(path));
        Map<String,ClientVersion> stringClientVersionMap = new HashMap<String,ClientVersion>();

        if(ListUtils.checkListIsNotNull(fileList)){
            for(String str:fileList){
                String versionStr = FileUtils.txt2String(path+File.separator+ str);
                if(!StringUtils.isEmpty(versionStr)){
                    ClientVersion clientVersion = JSON.parseObject(versionStr, ClientVersion.class);
                    if(!"none".equals(clientVersion.getStatus()) && clientVersion.getCode()==0){
                        String result = HttpUtils.httpRequestStr(configUtils.getUpdateVersionResultCommitNetUrl()+"?id="+clientVersion.getId()+"&result="+clientVersion.getStatus()+"&machineNum="+clientVersion.getMachineNum()+"&type="+type, "GET", null);
                        if(!StringUtils.isEmpty(result)){
                            RestResult restResult = JSON.parseObject(result, RestResult.class);
                            if(restResult.getResult()==200){
                                VersionInfo versionInfo = new VersionInfo();
                                versionInfo.setId(clientVersion.getId());
                                versionInfo.setType(type);
                                versionInfo.setUpdateDate(clientVersion.getUpdateDate());
                                versionInfo.setVersion(clientVersion.getVersion());

                                setRequestResult(versionInfo,clientVersion.getMachineNum(),clientVersion.getStatus(),1);
                            }
                        }
                    }
                }
            }
        }
    }


    public UpdatePlanConfig findVersionConfig(){
        int count = 0;
        while (count<3){
            String versionStr = findVersionByHttp();
            try {
                if(!StringUtils.isEmpty(versionStr)){
                    return strToVersionConfig(versionStr);
                }
            }catch (Exception e){
                System.out.print("获取数据异常");
            }
            count++;
            System.out.print("请求失败，等待"+count+"秒，再次发起请求");
            try {
                Thread.sleep(count*2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String findVersionByHttp(){
        return HttpUtils.httpRequestStr(configUtils.getUpdateVersionUrl()+"?addressId="+properties.getAddressId(), "GET", null);
    }


    public static UpdatePlanConfig strToVersionConfig(String jsonStr){
        UpdatePlanConfig updatePlanConfig = JSON.parseObject(jsonStr, UpdatePlanConfig.class);
        return updatePlanConfig;
    }

    public void createSchtasks(VersionInfo versionInfo){
        //判断是否获取新的定时任务；如果本地有定时任务未执行就不拉新的定时任务=
        String  machineNum = configUtils.getMachineNum();
        List<UpdatePlanMachine> updatePlanMachineList =  versionInfo.getUpdatePlanMachineList();
        if(ListUtils.checkListIsNull(updatePlanMachineList)){
            if(0==versionInfo.getType()){
                setRequestResult(versionInfo,machineNum,"none",0);
            }else{
                setRequestResult(versionInfo,machineNum,"none",0);
            }
        }
    }


}
