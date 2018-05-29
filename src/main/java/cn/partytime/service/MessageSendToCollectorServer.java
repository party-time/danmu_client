package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.model.client.ClientModel;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2018/5/29.
 */

@Service
public class MessageSendToCollectorServer {

    @Autowired
    private ClientCache clientCache;

    /**
     * 客户端发送消息给服务器
     * @param map
     */
    public void sendMessageToCollectorServer(Map<String,Object> map){
        ConcurrentHashMap<Channel,ClientModel> concurrentHashMap = clientCache.findServerClientChannelConcurrentHashMap();
        if (concurrentHashMap != null && concurrentHashMap.size() > 0) {
            for (ConcurrentHashMap.Entry<Channel, ClientModel> entry : concurrentHashMap.entrySet()) {
                Channel channel = entry.getKey();
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(map)));
            }
        }
    }
}
