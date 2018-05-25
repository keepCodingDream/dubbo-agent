package com.tracy.dubbo.agent.dubbo;

import com.tracy.dubbo.agent.dubbo.model.RpcFuture;
import com.tracy.dubbo.agent.dubbo.model.RpcRequestHolder;
import com.tracy.dubbo.agent.dubbo.model.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        String requestId = response.getRequestId();
        RpcFuture future = RpcRequestHolder.get(requestId);
        if(null != future){
            RpcRequestHolder.remove(requestId);
            future.done(response);
        }
    }
}
