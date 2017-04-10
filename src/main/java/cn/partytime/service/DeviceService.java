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
    private ConfigUtils configUtils;

    @Autowired
    private ClientCache clientCache;

    /**
     * 获取设备基本信息
     */
    public void findDeviceInfo(){
        String url =configUtils.findDeviceInfoUrl();
        String deviceStr = HttpUtils.httpRequestStr(url,"GET",null);
        DeviceConfig deviceConfig = JSON.parseObject(deviceStr,DeviceConfig.class);
        List<DeviceInfo> deviceInfoList = deviceConfig.getData();
        String filePath = configUtils.findJavaConfigPath();
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
                if(localIp.equals(deviceInfo.getIp())){
                    return deviceInfo;
                }
            }
        }
        return null;
    }
}
