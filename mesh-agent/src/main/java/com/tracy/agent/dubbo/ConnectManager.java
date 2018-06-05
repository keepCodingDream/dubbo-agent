package com.tracy.agent.dubbo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author lurenjie
 */
public class ConnectManager {
    private static Logger logger = LoggerFactory.getLogger(ConnectManager.class);
    private Channel channel;

    public ConnectManager() throws InterruptedException {
        int processors = Runtime.getRuntime().availableProcessors();
        logger.info("processors size :{}", processors);
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(processors + 1);
        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());
        if (!StringUtils.isEmpty(System.getProperty("dubbo.protocol.port"))) {
            int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
            channel = bootstrap.connect("127.0.0.1", port).sync().channel();
        }
    }

    public Channel getChannel() {
        return channel;
    }

}
