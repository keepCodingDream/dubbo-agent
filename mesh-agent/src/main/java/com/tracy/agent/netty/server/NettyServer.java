package com.tracy.agent.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author tracy.
 * @create 2018-06-07 14:48
 **/
public class NettyServer implements Server {
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private static final String NETTY_PORT = "netty.port";
    private static int port;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private int bossGroupThreads;
    private int workerGroupThreads;
    private int backlogSize;

    private KryoNettyServerChannelInitializer kryoNettyServerChannelInitializer;

    public NettyServer(KryoNettyServerChannelInitializer kryoNettyServerChannelInitializer, int bossGroupThreads,
                       int workerGroupThreads, int backlogSize) throws Exception {
        if (StringUtils.isEmpty(System.getProperty(NETTY_PORT))) {
            port = 3888;
        } else {
            port = Integer.valueOf(System.getProperty(NETTY_PORT));
        }
        this.kryoNettyServerChannelInitializer = kryoNettyServerChannelInitializer;
        this.bossGroupThreads = bossGroupThreads;
        this.workerGroupThreads = workerGroupThreads;
        this.backlogSize = backlogSize;
    }

    @Override
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(4);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, backlogSize)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(kryoNettyServerChannelInitializer);
        channel = serverBootstrap.bind(port).sync().channel();
        logger.info("Netty server start success.Port :{} bossGroup:{} workerGroupThreads:{} ", port, bossGroupThreads, workerGroupThreads);
    }

    @Override
    public void stop() {
        if (null == channel) {
            logger.warn("no channel when shutdown");
            return;
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workerGroup = null;
        channel = null;
    }
}
