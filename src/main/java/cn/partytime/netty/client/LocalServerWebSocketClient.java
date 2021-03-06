/*
 * Copyright 2014 The Netty Project
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
package cn.partytime.netty.client;

import cn.partytime.config.ClientCache;
import cn.partytime.config.ConfigUtils;
import cn.partytime.model.device.DeviceInfo;
import cn.partytime.netty.client.handler.LocalServerWebSocketClientHandler;
import cn.partytime.netty.client.handler.ServerWebSocketClientHandler;
import cn.partytime.service.DeviceService;
import cn.partytime.service.LogLogicService;
import cn.partytime.util.CommonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class LocalServerWebSocketClient {


    @Autowired
    private ClientCache clientCache;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    @Qualifier("localServerWebSocketClientHandler")
    private LocalServerWebSocketClientHandler localServerWebSocketClientHandler;

    public  void init() throws Exception {
        Thread.sleep(1000);
        ChannelFuture channelFuture = null;
        DeviceInfo deviceInfo = deviceService.findServiceDevice();
        String url = "ws://"+deviceInfo.getIp()+":"+deviceInfo.getPort()+"/ws";
        URI uri = new URI(url);
        String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
        final int port = uri.getPort();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(
                             new HttpClientCodec(),
                             new HttpObjectAggregator(8192),
                             WebSocketClientCompressionHandler.INSTANCE,
                             localServerWebSocketClientHandler);
                 }
             });
            channelFuture = b.connect(uri.getHost(), port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            if(channelFuture!=null){
                if(channelFuture.channel()!=null && channelFuture.channel().isOpen()){
                    channelFuture.channel().close();
                }
            }
            group.shutdownGracefully();
            logLogicService.logUploadHandler("本地服务器连接不上，重新接连");
            init();
        }
    }
}
