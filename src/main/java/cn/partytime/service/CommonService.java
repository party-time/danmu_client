package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.server.ServerConfig;
import cn.partytime.model.server.ServerInfo;
import cn.partytime.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

@Service
public class CommonService {




    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ClientCache clientCache;

    public ServerInfo getServerInfo(){

        String url  = configUtils.getDistributeServerUrl();
        String serverStr = HttpUtils.repeatRequest(url,"GET",null);
        if(!StringUtils.isEmpty(serverStr)){
            ServerConfig serverConfig = JSON.parseObject(serverStr,ServerConfig.class);
            ServerInfo serverInfo = serverConfig.getServerInfo();
            clientCache.setServerInfo(serverInfo);
            return serverInfo;
        }
        return null;
    }
}
