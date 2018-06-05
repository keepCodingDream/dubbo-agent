package com.tracy.agent.dubbo;

import com.tracy.agent.dubbo.model.*;
import com.tracy.agent.registry.IRegistry;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author lurenjie
 *         封装了到dubbo的调用
 */
public class RpcClient {
    private Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ConnectManager connectManager;
    /**
     * 每次都使用这一个对象，不用每次都创建新的
     */
    private RpcInvocation invocation = new RpcInvocation();
    private Request request = new Request();

    public RpcClient(IRegistry registry) throws InterruptedException {
        this.connectManager = new ConnectManager();
    }

    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        Channel channel = connectManager.getChannel();

        invocation.setMethodName(method);
        invocation.setAttachment("path", interfaceName);
        invocation.setParameterTypes(parameterTypesString);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());

        request.setVersion("2.0.0");
        request.setTwoWay(true);
        request.setData(invocation);
        logger.info("requestId=" + request.getId());
        RpcFuture future = new RpcFuture();
        RpcRequestHolder.put(String.valueOf(request.getId()), future);
        channel.writeAndFlush(request);
        Object result = null;
        try {
            result = future.get();
        } catch (Exception e) {
            logger.error("call rpc error!", e);
        }
        return result;
    }
}
