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
import cn.partytime.model.server.ServerInfo;
import cn.partytime.netty.client.handler.ServerWebSocketClientHandler;
import cn.partytime.service.CommonService;
import cn.partytime.service.LogLogicService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public final class ServerWebSocketClient {

    @Autowired
    @Qualifier("serverWebSocketClientHandler")
    private ServerWebSocketClientHandler serverWebSocketClientHandler;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ClientCache clientCache;

    @Autowired
    private CommonService commonService;

    @Autowired
    private LogLogicService logLogicService;


    public  void init() throws Exception {
        commonService.getServerInfo();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerInfo serverInfo = clientCache.getServerInfo();
            URI uri = new URI(configUtils.getWebSocketUrl(serverInfo.getIp(),serverInfo.getPort()));
            final int port = uri.getPort();
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
                             serverWebSocketClientHandler);
                 }
             });
            ChannelFuture channelFuture = b.connect(uri.getHost(), port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            logLogicService.logUploadHandler("远程服务器连接不上，重新接连");
            Thread.sleep(2000);
            init();

        }
    }
}
