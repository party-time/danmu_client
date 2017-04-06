package cn.partytime.config;

import cn.partytime.model.device.DeviceInfo;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Component
public class ClientCache {


    public static String clientStatus = "";

    public static String getClientStatus() {
        return clientStatus;
    }

    public static void setClientStatus(String clientStatus) {
        ClientCache.clientStatus = clientStatus;
    }

    private ConcurrentHashMap<String, DeviceInfo> deviceInfoConcurrentHashMap = new ConcurrentHashMap<String, DeviceInfo>();

    public void setDeviceInfoConcurrentHashMap(String id,DeviceInfo deviceInfo){
        deviceInfoConcurrentHashMap.put(id,deviceInfo);
    }

    public ConcurrentHashMap findConcurrentHashMap(){
        return deviceInfoConcurrentHashMap;
    }

}
