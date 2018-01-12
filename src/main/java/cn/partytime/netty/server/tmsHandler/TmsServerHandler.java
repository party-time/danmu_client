package cn.partytime.netty.server.tmsHandler;

import cn.partytime.config.ClientCache;
import cn.partytime.model.Properties;
import cn.partytime.model.client.ClientModel;
import cn.partytime.service.LogLogicService;
import cn.partytime.service.TmsCommandService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Qualifier("tmsServerHandler")
@ChannelHandler.Sharable
public class TmsServerHandler extends ChannelInboundHandlerAdapter {


    //private int counter;


    @Autowired
    private TmsCommandService tmsCommandService;

    @Autowired
    private LogLogicService logLogicService;


    @Autowired
    private ClientCache clientCache;

    @Autowired
    private Properties properties;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String command = (String)msg;
        logLogicService.logUploadHandler("接收的命令:"+command);
        if("3".equals(properties.getMachineNum())) {
            command = replaceBlank(command)+"-aa";
            logLogicService.logUploadHandler("转发接收的命令:"+command);
            ConcurrentHashMap<Channel,ClientModel> channelClientModelConcurrentHashMap = clientCache.findChannelTmsClientModelConcurrentHashMap();
            if (channelClientModelConcurrentHashMap != null && channelClientModelConcurrentHashMap.size() > 0) {
                for (ConcurrentHashMap.Entry<Channel, ClientModel> entry : channelClientModelConcurrentHashMap.entrySet()) {
                    Channel channel = entry.getKey();
                    //channel.writeAndFlush(command);
                    ByteBuf message;
                    byte[] req = command.getBytes();
                    message = Unpooled.buffer(req.length);
                    message.writeBytes(req);
                    channel.writeAndFlush(message);
                }
            }
        }else{
            tmsCommandService.projectorHandler(command);
            tmsCommandService.movieHandler(command);
            tmsCommandService.adHandler(command);
        }



        /*System.out.println("The time server receive order ："+ body+" ; the counter is :"+ ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?
                new Date(System.currentTimeMillis()).toString():"BAD ORDER";
        currentTime = currentTime +System.getProperty("line.separator");
        currentTime = currentTime + "$_";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);*/
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}