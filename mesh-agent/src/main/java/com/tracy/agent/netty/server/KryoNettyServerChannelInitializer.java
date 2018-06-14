package com.tracy.agent.netty.server;

import com.tracy.agent.codec.KryoDecoder;
import com.tracy.agent.codec.KryoEncoder;
import com.tracy.agent.codec.KryoPool;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KryoNettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private NettyServerDispatchHandler serverDispatchHandler;

    @Resource
    private KryoPool kryoSerializationFactory;

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new KryoEncoder(kryoSerializationFactory));
        ch.pipeline().addLast(new KryoDecoder(kryoSerializationFactory));
        ch.pipeline().addLast(serverDispatchHandler);
    }
}
