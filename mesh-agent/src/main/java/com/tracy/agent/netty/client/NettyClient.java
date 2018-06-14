package com.tracy.agent.netty.client;

import com.tracy.agent.dubbo.model.InnerFuture;
import com.tracy.agent.model.Request;
import com.tracy.agent.model.Response;
import com.tracy.agent.netty.KryoNettyHelper;
import com.tracy.agent.netty.server.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Netty 客户端
 *
 * @author lurenjie
 */
public class NettyClient implements Client {
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private int workerGroupThreads;

    private EventLoopGroup workerGroup;
    private Channel channel;
    private NettyClientChannelInitializer nettyClientChannelInitializer;

    public NettyClient(int workerGroupThreads, NettyClientChannelInitializer nettyClientChannelInitializer) {
        this.workerGroupThreads = workerGroupThreads;
        this.nettyClientChannelInitializer = nettyClientChannelInitializer;
    }

    @Override
    public void connect(final InetSocketAddress socketAddress) throws InterruptedException {
        workerGroup = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(nettyClientChannelInitializer);
        channel = bootstrap.connect(socketAddress.getAddress().getHostAddress(), socketAddress.getPort()).sync().channel();
        logger.info("NettyClient connect to {}:{} success! ", socketAddress.getHostName(), socketAddress.getPort());
    }

    @Override
    public Response sent(final Request request) {
        InnerFuture future = new InnerFuture();
        KryoNettyHelper.preCall(request.getRequestId(), future);
        channel.writeAndFlush(request);
        try {
            return future.get(1500L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return new Response(-2, null);
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!(remoteAddress instanceof InetSocketAddress)) {
            throw new RuntimeException("Get remote address error, should be InetSocketAddress");
        }
        return (InetSocketAddress) remoteAddress;
    }

    @Override
    public void close() {
        if (null == channel) {
            logger.warn("no channel when shutdown");
        }
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        workerGroup = null;
        channel = null;
    }

}
