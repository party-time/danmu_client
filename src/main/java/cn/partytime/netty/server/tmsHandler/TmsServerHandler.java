package cn.partytime.netty.server.tmsHandler;

import cn.partytime.service.LogLogicService;
import cn.partytime.service.TmsCommandService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String command = (String)msg;
        command = replaceBlank(command);
        logLogicService.logUploadHandler("接收的命令:"+command);
        tmsCommandService.projectorHandler(command);
        tmsCommandService.movieHandler(command);
        tmsCommandService.adHandler(command);

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