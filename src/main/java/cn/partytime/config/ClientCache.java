package cn.partytime.config;

import cn.partytime.model.Party;
import cn.partytime.model.client.ClientModel;
import cn.partytime.model.client.PartyInfo;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.model.server.ServerInfo;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

@Component
public class ClientCache {


    public  String clientStatus = "";

    public PartyInfo partyInfo;

    private ServerInfo serverInfo;

    private ConcurrentHashMap<String, DeviceInfo> deviceInfoConcurrentHashMap = new ConcurrentHashMap<String, DeviceInfo>();

    public  String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    public void setDeviceInfoConcurrentHashMap(String id, DeviceInfo deviceInfo){
        deviceInfoConcurrentHashMap.put(id,deviceInfo);
    }
    public ConcurrentHashMap findConcurrentHashMap(){
        return deviceInfoConcurrentHashMap;
    }


    public PartyInfo getPartyInfo() {
        return partyInfo;
    }

    public void setPartyInfo(PartyInfo partyInfo) {
        this.partyInfo = partyInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ConcurrentHashMap<String, DeviceInfo> getDeviceInfoConcurrentHashMap() {
        return deviceInfoConcurrentHashMap;
    }

    public void setDeviceInfoConcurrentHashMap(ConcurrentHashMap<String, DeviceInfo> deviceInfoConcurrentHashMap) {
        this.deviceInfoConcurrentHashMap = deviceInfoConcurrentHashMap;
    }
}
