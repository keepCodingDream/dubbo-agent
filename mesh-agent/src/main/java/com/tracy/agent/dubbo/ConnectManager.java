package com.tracy.agent.dubbo;

import io.netty.bootstrap.Bootstrap;
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
        String value = System.getProperty("weight");
        if (StringUtils.isEmpty(value)) {
            //默认4核
            value = "4";
        }
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());
        if (!StringUtils.isEmpty(System.getProperty("dubbo.protocol.port"))) {
            int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
            channel = bootstrap.connect("127.0.0.1", port).sync().channel();
            logger.info("ConnectManager connect to dubbo success!");
        }
    }

    public Channel getChannel() {
        return channel;
    }

}
