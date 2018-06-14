package com.tracy.agent.netty.client;

import com.tracy.agent.dubbo.model.InnerFuture;
import com.tracy.agent.model.Response;
import com.tracy.agent.netty.KryoNettyHelper;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Sharable
public class NettyClientDispatchHandler extends SimpleChannelInboundHandler<Response> {
    private static Logger logger = LoggerFactory.getLogger(NettyClientDispatchHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        InnerFuture future = KryoNettyHelper.getResponse(response.getRequestId());
        if (future != null) {
            KryoNettyHelper.removeRequest(response.getRequestId());
            future.done(response);
        }
    }
}
