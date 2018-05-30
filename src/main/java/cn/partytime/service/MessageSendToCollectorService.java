package cn.partytime.service;

import cn.partytime.config.ClientCache;
import cn.partytime.model.client.ClientModel;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2018/5/29.
 */

@Service
@Slf4j
public class MessageSendToCollectorService {

    @Autowired
    private ClientCache clientCache;

    /**
     * 客户端发送消息给服务器
     * @param map
     */
    public void sendMessageToCollectorServer(Map<String,String> map){
        ConcurrentHashMap<Channel,ClientModel> concurrentHashMap = clientCache.findServerClientChannelConcurrentHashMap();
        if (concurrentHashMap != null && concurrentHashMap.size() > 0) {
            for (ConcurrentHashMap.Entry<Channel, ClientModel> entry : concurrentHashMap.entrySet()) {
                Channel channel = entry.getKey();
                String message = JSON.toJSONString(map);
                log.info("发送给服务器的消息:{}",message);
                channel.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
    }
}
