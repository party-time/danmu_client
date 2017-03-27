package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Service
public class ProjectorService {

    @Autowired
    private ClientCache clientCache;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private LogLogicService logLogicService;

    /**
     * 投影仪开启和关闭
     * @param openFlg(true:开启;false:关闭)
     */
    public void projectorHandler(boolean openFlg){
        ConcurrentHashMap concurrentHashMap = clientCache.findConcurrentHashMap();
        List<DeviceInfo> deviceInfoList = findDeviceInfo(concurrentHashMap);
        String url= "";
        for(DeviceInfo deviceInfo:deviceInfoList){
            if(openFlg){
                url = configUtils.getProjectorOpenUrl(deviceInfo.getIp());
                logLogicService.logUploadHandler("开启投影仪的url:"+url);
            }else{
                url = configUtils.getProjectorCloseUrl(deviceInfo.getIp());
                logLogicService.logUploadHandler("关闭投影仪的url:"+url);
            }

            HttpUtils.httpRequestStr(url,"GET",null);
        }
    }

    private List<DeviceInfo> findDeviceInfo(ConcurrentHashMap concurrentHashMap){
        Iterator iterator = concurrentHashMap.keySet().iterator();
        List<DeviceInfo> deviceInfoList = new ArrayList<DeviceInfo>();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            DeviceInfo deviceInfo = (DeviceInfo)concurrentHashMap.get(key);
            if(deviceInfo.getType()==0){
                deviceInfoList.add(deviceInfo);
            }
        }
        return deviceInfoList;
    }
}
