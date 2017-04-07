package cn.partytime.netty.server.clienthandler;

import cn.partytime.config.ClientCache;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by lENOVO on 2016/12/6.
 */

@Component
@Qualifier("webSocketServerHandler")
@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    private WebSocketServerHandshaker handshaker;


    @Autowired
    private ClientCache clientCache;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {
        if (object instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) object);
        }else if (object instanceof WebSocketFrame) {//如果是Websocket请求，则进行websocket操作
            //handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            System.out.print("======================");
            Channel channel = ctx.channel();
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) object;
            String accecptStr = textWebSocketFrame.text();
            //ProtocolModel protocolModel = JSON.parseObject(accecptStr, ProtocolModel.class);
            logger.info("收到的消息:{}",accecptStr);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

        String url = req.uri();
        logger.info("建立连接url:{}",url);
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();

        if(url.startsWith("/flashCheck")){
            if (parameters.size() == 1){
                String status = parameters.get("status").get(0);
                clientCache.setClientStatus(status);
            }
            ctx.close();
            return;
        }

        if(url.startsWith("/flashIsOk")){
            httpHandler(url,ctx,req,clientCache.getClientStatus());
            return;
        }

        if(url.startsWith("/javaIsOk")){
            httpHandler(url,ctx,req,"ok");
            return;
        }



        /*if (parameters.size() != 2) {
            logger.info("参数不可缺省");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }
        String code = parameters.get("code").get(0);
        String clientType = parameters.get("clientType").get(0);
        if(StringUtils.isEmpty(clientType) || StringUtils.isEmpty(code)){
            logger.info("唯一标识,客户端类型都不能为空");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }*/

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), req);
            //clientLoginService.clientLogin(code,clientType,ctx.channel());
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + req.uri();
        return "ws://" + location;
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void httpHandler(String url,ChannelHandlerContext ctx,FullHttpRequest req,String message){
        FullHttpResponse response = findFullHttpResponse(req,message);
        ctx.write(response);
        ctx.flush();
        return;
    }


    private FullHttpResponse findFullHttpResponse(FullHttpRequest req,String message){
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(message.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH,
                response.content().readableBytes());
        if (HttpHeaders.isKeepAlive(req)) {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        return response;
    }

}
