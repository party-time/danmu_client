package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.device.DeviceConfig;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.util.FileUtils;
import cn.partytime.util.HttpUtils;
import cn.partytime.util.ListUtils;
import com.alibaba.fastjson.JSON;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

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
        String url =configUtils.getDeviceInfoUrlUrl();
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
}
