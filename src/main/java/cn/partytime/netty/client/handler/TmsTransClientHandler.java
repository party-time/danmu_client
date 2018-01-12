package cn.partytime.netty.client.handler;

import cn.partytime.config.ClientCache;
import cn.partytime.model.client.ClientModel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 2018/1/12.
 */

@Component
@Qualifier("tmsTransClientHandler")
@ChannelHandler.Sharable
public class TmsTransClientHandler  extends ChannelInboundHandlerAdapter {


    @Autowired
    private ClientCache clientCache;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ClientModel clientModel = new ClientModel();
        clientCache.addChannelTmsClientModelConcurrentHashMap(channel,clientModel);
        System.out.println("客户端:" + channel.id() + " 加入");
        System.out.println("转发TMS指令的服务连接1号机器成功连接成功");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        ClientModel clientModel = new ClientModel();
        clientCache.removeChannelTmsClientModelConcurrentHashMap(channel);
        System.out.println("转发TMS指令的服务连接1号机器断开连接");
        ctx.fireChannelInactive();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
