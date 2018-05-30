/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
//The MIT License
//
//Copyright (c) 2009 Carl Bystršm
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package cn.partytime.netty.client.handler;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.config.FlashCache;
import cn.partytime.model.client.ClientCommand;
import cn.partytime.model.client.ClientCommandConfig;
import cn.partytime.model.client.ClientModel;
import cn.partytime.model.client.PartyInfo;
import cn.partytime.model.server.ServerInfo;
import cn.partytime.service.CommandExecuteService;
import cn.partytime.service.CommandHandlerService;
import cn.partytime.service.MessageSendToCollectorServer;
import cn.partytime.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import freemarker.template.utility.StringUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Qualifier("serverWebSocketClientHandler")
@ChannelHandler.Sharable
public class ServerWebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    @Autowired
    private ClientCache clientCache;

    @Autowired
    private CommandHandlerService commandHandlerService;

    @Autowired
    private CommandExecuteService commandExecuteService;

    private  WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private FlashCache flashCache;


    @Autowired
    private MessageSendToCollectorServer messageSendToCollectorServer;


    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        URI uri = null;
        try {
            ServerInfo serverInfo = clientCache.getServerInfo();
            uri = new URI(configUtils.getWebSocketUrl(serverInfo.getIp(),serverInfo.getPort()));
            handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());

            Channel channel = ctx.channel();

            handshaker.handshake(channel);



            //messageSendToCollectorServer.sendMessageToCollectorServer(map);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("WebSocket Client disconnected!");
        clientCache.removeClientChannelConcurrentHashMap(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            log.info("WebSocket Client connected!");
            handshakeFuture.setSuccess();


            if(flashCache.getSendFlashOpenCount()>0){
                return;
            }

            flashCache.setSendFlashOpenCount(1);

            //与服务器连接成功后向服务器发送flash全屏的指令
            Map<String,String> map = new HashMap<>();
            map.put("data","true");
            map.put("type","startStageAndFull");
            map.put("clientType","2");
            map.put("code",configUtils.getRegisterCode());
            Channel channel = ctx.channel();
            ClientModel clientModel = new ClientModel();
            clientCache.setServerClientChannelConcurrentHashMap(channel,clientModel);
            messageSendToCollectorServer.sendMessageToCollectorServer(map);
            /*Map<String,String> map = new HashMap<>();
            map.put("data","true");
            map.put("type","startStageAndFull");
            map.put("clientType","2");
            map.put("code",configUtils.getRegisterCode());
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(map)));*/
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            log.info("WebSocket Client received message: " + textFrame.text());
            String commandTxt = textFrame.text();
            ClientCommandConfig clientCommandConfig = JSON.parseObject(commandTxt,ClientCommandConfig.class);


            if("1".equals(configUtils.getMachineNum())){
                commandHandlerService.commandHandler(clientCommandConfig);
            }

        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //客户端发生异常。不做下线处理
        Channel channel = ctx.channel();
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                Map<String,String> map = new HashMap<>();
                map.put("type","ping");
                map.put("code",configUtils.getRegisterCode());
                map.put("clientType","2");
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(map)));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
