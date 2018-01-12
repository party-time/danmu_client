package cn.partytime.netty.client;

import cn.partytime.model.device.DeviceInfo;
import cn.partytime.model.server.ServerInfo;
import cn.partytime.netty.client.handler.ServerWebSocketClientHandler;
import cn.partytime.netty.client.handler.TmsTransClientHandler;
import cn.partytime.netty.client.handler.TmsTransClientInitializer;
import cn.partytime.netty.server.tmsHandler.TmsServerInitializer;
import cn.partytime.service.DeviceService;
import cn.partytime.service.LogLogicService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Created by admin on 2018/1/12.
 */

@Component
public class TmsTransClient {

    @Autowired
    @Qualifier("tmsTransClientHandler")
    private TmsTransClientHandler tmsTransClientHandler;

    @Autowired
    private LogLogicService logLogicService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TmsTransClientInitializer tmsTransClientInitializer;

    public  void init() throws Exception {
        Thread.sleep(5000);
        DeviceInfo deviceInfo = deviceService.findServiceDevice();
        ChannelFuture channelFuture = null;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(tmsTransClientInitializer);
            channelFuture = b.connect(deviceInfo.getIp(), 2016).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            System.out.println("接收TMS指令的服务异常，重新启动");
            logLogicService.logUploadHandler("接收TMS指令的服务异常，重新启动");
            init();
        }
    }
}
