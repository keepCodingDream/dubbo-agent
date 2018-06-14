package com.tracy.agent.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NettyClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private NettyClientDispatchHandler clientDispatchHandler;

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(clientDispatchHandler);
    }
}
