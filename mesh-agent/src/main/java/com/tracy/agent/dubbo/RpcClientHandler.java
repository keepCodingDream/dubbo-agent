package com.tracy.agent.dubbo;

import com.tracy.agent.dubbo.model.RpcFuture;
import com.tracy.agent.dubbo.model.RpcRequestHolder;
import com.tracy.agent.dubbo.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        RpcFuture future = RpcRequestHolder.get(requestId);
        if (null != future) {
            RpcRequestHolder.remove(requestId);
            logger.info("Rpc spend :{} ms", System.currentTimeMillis() - future.getStartTime());
            future.done(response);
        }
    }

}
