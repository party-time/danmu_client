package cn.partytime.netty.client.handler;

import cn.partytime.netty.server.tmsHandler.TmsServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2018/1/12.
 */

@Component
public class TmsTransClientInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    @Qualifier("tmsTransClientHandler")
    private TmsTransClientHandler tmsTransClientHandler;


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("idleStateHandler", new IdleStateHandler(20, 10, 0));

        ByteBuf delimiter = Unpooled.copiedBuffer("-aa".getBytes());
        pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(tmsTransClientHandler);
    }
}
