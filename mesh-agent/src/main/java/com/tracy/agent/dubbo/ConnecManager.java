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

/**
 * @author lurenjie
 */
public class ConnecManager {
    private static Logger logger = LoggerFactory.getLogger(ConnecManager.class);

    private EventLoopGroup eventLoopGroup;

    private Bootstrap bootstrap;

    private Channel channel;
    private final Object lock = new Object();

    public ConnecManager() {
        int processors = Runtime.getRuntime().availableProcessors();
        logger.info("processors size :{}", processors);
        eventLoopGroup = new NioEventLoopGroup(processors + 1);
    }

    public Channel getChannel() throws Exception {
        if (null != channel) {
            return channel;
        }

        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (lock) {
                if (null == channel) {
                    int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
                    channel = bootstrap.connect("127.0.0.1", port).sync().channel();
                }
            }
        }

        return channel;
    }

    private void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());
    }
}
