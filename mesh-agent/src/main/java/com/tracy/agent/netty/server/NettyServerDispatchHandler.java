package com.tracy.agent.netty.server;

import com.tracy.agent.dubbo.RpcClient;
import com.tracy.agent.model.Constants;
import com.tracy.agent.model.Request;
import com.tracy.agent.model.Response;
import com.tracy.agent.registry.EtcdRegistry;
import com.tracy.agent.registry.IRegistry;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Sharable
public class NettyServerDispatchHandler extends SimpleChannelInboundHandler<Request> implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(NettyServerDispatchHandler.class);
    private RpcClient rpcClient = null;
    private ExecutorService executorService = Executors.newFixedThreadPool(40);

    public NettyServerDispatchHandler() throws InterruptedException {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        executorService.execute(new Invoker(channelHandlerContext, request));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ctx.writeAndFlush(new Response(-1, new byte[]{-1}));
    }


    private byte[] execute(final Request request) throws Exception {
        return (byte[]) rpcClient.invoke(request.getInterfaceName(), request.getMethod(), request.getParameterTypesString(), request.getParameter());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        IRegistry registry = new EtcdRegistry(System.getProperty(Constants.ETCD_URL));
        rpcClient = new RpcClient(registry);
    }

    private class Invoker implements Runnable {
        private ChannelHandlerContext channelHandlerContext;
        private Request request;

        public Invoker(ChannelHandlerContext channelHandlerContext, Request request) {
            this.channelHandlerContext = channelHandlerContext;
            this.request = request;
        }

        @Override
        public void run() {
            byte[] returnValue = new byte[0];
            try {
                returnValue = execute(request);
                channelHandlerContext.writeAndFlush(new Response(request.getRequestId(), returnValue));
            } catch (Exception e) {
                channelHandlerContext.writeAndFlush(new Response(request.getRequestId(), new byte[]{-1}));
            }
        }
    }
}
