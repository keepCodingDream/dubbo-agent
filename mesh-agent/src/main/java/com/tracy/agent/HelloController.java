package com.tracy.agent;

import com.tracy.agent.model.Constants;
import com.tracy.agent.model.Request;
import com.tracy.agent.model.Response;
import com.tracy.agent.netty.client.KryoNettyClientChannelInitializer;
import com.tracy.agent.netty.client.NettyClient;
import com.tracy.agent.netty.server.KryoNettyServerChannelInitializer;
import com.tracy.agent.netty.server.NettyServer;
import com.tracy.agent.registry.Endpoint;
import com.tracy.agent.router.AbstractRouter;
import com.tracy.agent.router.RandomProxyRouter;
import com.tracy.agent.router.WeightedRandomRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class HelloController implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    private AbstractRouter router;

    @Value("${client.worker.group.threads}")
    private int clientThreads;

    @Value("${server.boss.group.threads}")
    private int bossGroupThreads;

    @Value("${server.worker.group.threads}")
    private int workerGroupThreads;

    @Value("${server.backlog.size}")
    private int backlogSize;


    private Map<String, NettyClient> clientMap;

    private NettyServer server = null;


    public HelloController() throws InterruptedException {
    }

    @Resource
    private KryoNettyClientChannelInitializer kryoNettyClientChannelInitializer;

    @Resource
    private KryoNettyServerChannelInitializer kryoNettyServerChannelInitializer;

    private static final ExecutorService executer = Executors.newFixedThreadPool(250);

    @RequestMapping(value = "")
    public DeferredResult<Integer> invoke(@RequestParam("interface") String interfaceName,
                                          @RequestParam("method") String method,
                                          @RequestParam("parameterTypesString") String parameterTypesString,
                                          @RequestParam("parameter") String parameter) throws Exception {
        String salt = System.getProperty("salt");
        DeferredResult<Integer> response = new DeferredResult<>();
        executer.execute(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //ignore
            }
            int result = (parameter + salt).hashCode();
            response.setResult(result);
        });
        return response;
    }

    /**
     * 找到对应代理，转发请求
     */
    private Object consumer(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        Endpoint endpoint = router.find(interfaceName);
        NettyClient nettyClient = clientMap.get(endpoint.getHost());
        Request request = new Request();
        request.setInterfaceName(interfaceName);
        request.setMethod(method);
        request.setParameter(parameter);
        request.setParameterTypesString(parameterTypesString);
        long start = System.currentTimeMillis();
        Response response = nettyClient.sent(request);
        if (response.getRequestId() < 0) {
            return "";
        }
        logger.info("host:{} total spend:{} ms", endpoint.getHost(), System.currentTimeMillis() - start);
        return new String(response.getValue());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String type = System.getProperty(Constants.TYPE);
        if (Constants.CONSUMER.equals(type)) {
            String policy = System.getProperty("router.policy");
            if (StringUtils.isEmpty(policy)) {
                router = new WeightedRandomRouter();
            } else {
                router = new RandomProxyRouter();
            }
            Map<String, List<Endpoint>> serverMap = router.getServerMap();
            List<Endpoint> endpoints = null;
            for (Map.Entry<String, List<Endpoint>> entry : serverMap.entrySet()) {
                endpoints = entry.getValue();
                break;
            }
            if (CollectionUtils.isEmpty(endpoints)) {
                throw new RuntimeException("NO service in remote,Just shutdown");
            }
            //初始化netty客户端，每个server都建立连接
            clientMap = new HashMap<>(endpoints.size());
            for (Endpoint endpoint : endpoints) {
                NettyClient nettyClient = new NettyClient(clientThreads, kryoNettyClientChannelInitializer);
                nettyClient.connect(new InetSocketAddress(endpoint.getHost(), 3888));
                clientMap.put(endpoint.getHost(), nettyClient);
            }
        } else if (Constants.PROVIDER.equals(type)) {
            server = new NettyServer(kryoNettyServerChannelInitializer, bossGroupThreads, workerGroupThreads, backlogSize);
            server.start();
        }
    }


    @PreDestroy
    public void clear() {
        if (server != null) {
            server.stop();
        }
    }

}
