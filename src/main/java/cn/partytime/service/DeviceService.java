package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.device.DeviceConfig;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.util.CommonUtil;
import cn.partytime.util.FileUtils;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.ListUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Service
public class DeviceService {

    @Autowired
    private LogLogicService logLogicService;


    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ClientCache clientCache;

    /**
     * 获取设备基本信息
     */
    public void findDeviceInfo(){
        String filePath = configUtils.findJavaConfigPath();
        File file = new File(filePath);
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        DeviceConfig deviceConfig=null;

        if(file.exists()){
            String deviceStr = FileUtils.txt2String(filePath);
            System.out.println("get local server Info:"+deviceStr);
            if(!StringUtils.isEmpty(deviceStr)){
                deviceInfoList = JSON.parseArray(deviceStr,DeviceInfo.class);
            }else {
                String url = configUtils.findDeviceInfoUrl();
                deviceStr = HttpUtils.httpRequestStr(url, "GET", null);
                deviceConfig = JSON.parseObject(deviceStr, DeviceConfig.class);
                if(deviceConfig!=null){
                    deviceInfoList = deviceConfig.getData();
                }
            }
        }else{
            String url =configUtils.findDeviceInfoUrl();
            String deviceStr = HttpUtils.httpRequestStr(url,"GET",null);
            deviceConfig = JSON.parseObject(deviceStr,DeviceConfig.class);
            if(deviceConfig!=null){
                deviceInfoList = deviceConfig.getData();
            }
        }

        if(ListUtils.checkListIsNotNull(deviceInfoList)){
            FileUtils.writeContentToFile(filePath,JSON.toJSONString(deviceInfoList));
            for(DeviceInfo deviceInfo:deviceInfoList){
                clientCache.setDeviceInfoConcurrentHashMap(deviceInfo.getId(),deviceInfo);
            }
        }

    }

    public List<DeviceInfo> findDeviceInfoList(int type) {
        ConcurrentHashMap concurrentHashMap = clientCache.findConcurrentHashMap();
        Iterator iterator = concurrentHashMap.keySet().iterator();
        List<DeviceInfo> deviceInfoList = new ArrayList<DeviceInfo>();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            DeviceInfo deviceInfo = (DeviceInfo) concurrentHashMap.get(key);
            if (deviceInfo.getType() == type) {
                deviceInfoList.add(deviceInfo);
            }
        }
        return deviceInfoList;
    }

    public DeviceInfo findServiceDevice(){
        String localIp = CommonUtil.getIpAddress();
        List<DeviceInfo> deviceInfoList = findDeviceInfoList(1);
        if(ListUtils.checkListIsNotNull(deviceInfoList)){
            for(DeviceInfo deviceInfo:deviceInfoList){
                if(!localIp.equals(deviceInfo.getIp())){
                    return deviceInfo;
                }
            }
        }
        return null;
    }
}
