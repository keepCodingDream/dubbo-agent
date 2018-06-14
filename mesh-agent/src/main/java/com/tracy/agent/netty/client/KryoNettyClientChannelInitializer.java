package com.tracy.agent.netty.client;

import com.tracy.agent.codec.KryoDecoder;
import com.tracy.agent.codec.KryoEncoder;
import com.tracy.agent.codec.KryoPool;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KryoNettyClientChannelInitializer extends NettyClientChannelInitializer {

    @Resource
    private KryoPool kryoSerializationFactory;

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new KryoEncoder(kryoSerializationFactory));
        ch.pipeline().addLast(new KryoDecoder(kryoSerializationFactory));
        super.initChannel(ch);
    }
}
