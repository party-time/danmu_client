package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 投影仪开启和关闭
     *
     * @param type 0开启，1：关闭； 2：切白
     */
    public void projectorHandler(int type) {
        ConcurrentHashMap concurrentHashMap = clientCache.findConcurrentHashMap();
        List<DeviceInfo> deviceInfoList = findDeviceInfo(concurrentHashMap);
        String url = "";
        for (DeviceInfo deviceInfo : deviceInfoList) {
            if (type == 0) {
                url = configUtils.getProjectorOpenUrl(deviceInfo.getIp());
                logLogicService.logUploadHandler("开启投影仪的url:" + url);
            } else if (type == 1) {
                url = configUtils.getProjectorCloseUrl(deviceInfo.getIp());
                logLogicService.logUploadHandler("关闭投影仪的url:" + url);
            } else if (type == 2) {}
            /*if(openFlg){
                url = configUtils.getProjectorOpenUrl(deviceInfo.getIp());
                logLogicService.logUploadHandler("开启投影仪的url:"+url);
            }else{
                url = configUtils.getProjectorCloseUrl(deviceInfo.getIp());
                logLogicService.logUploadHandler("关闭投影仪的url:"+url);
            }*/
            executeProjector(url);
        }
    }


    private void executeProjector(final String url) {
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HttpUtils.httpRequestStr(url, "GET", null);
            }
        });
    }

    private List<DeviceInfo> findDeviceInfo(ConcurrentHashMap concurrentHashMap) {
        Iterator iterator = concurrentHashMap.keySet().iterator();
        List<DeviceInfo> deviceInfoList = new ArrayList<DeviceInfo>();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            DeviceInfo deviceInfo = (DeviceInfo) concurrentHashMap.get(key);
            if (deviceInfo.getType() == 0) {
                deviceInfoList.add(deviceInfo);
            }
        }
        return deviceInfoList;
    }
}
