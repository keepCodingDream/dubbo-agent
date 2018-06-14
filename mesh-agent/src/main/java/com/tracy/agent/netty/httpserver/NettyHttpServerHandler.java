package com.tracy.agent.netty.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author tracy.
 * @create 2018-06-13 10:48
 **/
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Logger logger = LoggerFactory.getLogger(NettyHttpServerHandler.class);
    public static String HA_HA = System.getProperty("salt");
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONNECTION = "Connection";
    private static final String KEEP_ALIVE = "keep-alive";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        msg.content().skipBytes(136);
        ByteBuf bf = msg.content().slice();
        byte[] byteArray = new byte[bf.capacity()];
        bf.readBytes(byteArray);
        String parameter = new String(byteArray);
//        Promise<Integer> agentResponsePromise = new DefaultPromise<>(ctx.executor());
//        agentResponsePromise.addListener(future -> {
//            int agentResponse = (Integer) future.get();
//            writeResponse(ctx, agentResponse);
//        });
        Thread.sleep(10);
        writeResponse(ctx, (parameter + HA_HA).hashCode());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("http服务器响应出错", cause);
        ctx.channel().close();
    }

    private void writeResponse(ChannelHandlerContext ctx, int agentResponse) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(Integer.toString(agentResponse).getBytes()));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, KEEP_ALIVE);
        ctx.channel().writeAndFlush(response);
    }

}
