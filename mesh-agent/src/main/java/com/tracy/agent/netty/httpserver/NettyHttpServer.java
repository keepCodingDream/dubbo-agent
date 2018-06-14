package com.tracy.agent.netty.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tracy.
 * @create 2018-06-13 10:43
 **/
public class NettyHttpServer {
    private Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyHttpServerInitializer())
                    .childOption(ChannelOption.TCP_NODELAY, true);
            Channel ch = bootstrap.bind(Integer.valueOf(System.getProperty("netty.port"))).sync().channel();
            logger.info(" export at port:{}", System.getProperty("netty.port"));
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
