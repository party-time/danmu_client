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
import cn.partytime.model.client.ClientCommand;
import cn.partytime.model.client.ClientCommandConfig;
import cn.partytime.model.client.PartyInfo;
import cn.partytime.model.server.ServerInfo;
import cn.partytime.service.CommandExecuteService;
import cn.partytime.service.ServerCommandHandlerService;
import cn.partytime.util.CommonUtil;
import cn.partytime.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import freemarker.template.utility.StringUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;


@Component
@Qualifier("serverWebSocketClientHandler")
@ChannelHandler.Sharable
public class ServerWebSocketClientHandler extends SimpleChannelInboundHandler<Object> {


    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ClientCache clientCache;

    @Autowired
    private CommandExecuteService commandExecuteService;

    private  WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;



    @Autowired
    private ServerCommandHandlerService serverCommandHandlerService;


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
            String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
            final String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
            final int port = uri.getPort();
            handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
            handshaker.handshake(ctx.channel());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            System.out.println("WebSocket Client connected!");
            handshakeFuture.setSuccess();
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
            System.out.println("WebSocket Client received message: " + textFrame.text());
            String commandTxt = textFrame.text();
            ClientCommandConfig clientCommandConfig = JSON.parseObject(commandTxt,ClientCommandConfig.class);

            if("command".equals(clientCommandConfig.getType())){
                System.out.print(clientCommandConfig.getData());
                String partyInfoStr = String.valueOf(clientCommandConfig.getData());
                PartyInfo partyInfo =  JSON.parseObject(partyInfoStr,PartyInfo.class);
                clientCache.setPartyInfo(partyInfo);
            }else if("clientCommand".equals(clientCommandConfig.getType())){
                String clientCommandData = String.valueOf(clientCommandConfig.getData());
                ClientCommand clientCommand = JSON.parseObject(clientCommandData,ClientCommand.class);
                String type = clientCommand.getName();
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //直接脚本
                        execute(type);
                        //执行回调
                        if(!StringUtils.isEmpty(clientCommand.getBcallBack())){
                            HttpUtils.httpRequestStr(clientCommand.getBcallBack(),"GET",null);
                        }
                    }
                }).start();
            }
        }
    }

    public void execute(String command){

        if(CommonUtil.hasDigit(command)){
            if(!serverCommandHandlerService.chckerIsLocalCommand(command)){
                System.out.print("不是本机要执行的命令");
                //通知下个服务器
                return;
            }
        }else{
            //通知下个服务器
        }
        command = command.replaceAll("\\d+", "");
        /*if(!serverCommandHandlerService.chckerIsLocalCommand(command)){
            System.out.print("不是本机要执行的命令");
            return;
        }*/
        String commandStr = command.substring(0, 1).toUpperCase() + command.substring(1);
        String methodName="execute"+commandStr+"CallBack";
        try {
            Class<CommandExecuteService> clz = CommandExecuteService.class;
            Method method = clz.getMethod(methodName);
            method.invoke(commandExecuteService);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        /*System.out.print("与服务器断开。。。。");
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ct``x.close();*/
    }
}
