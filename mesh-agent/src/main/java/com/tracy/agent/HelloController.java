package com.tracy.agent;

import com.alibaba.fastjson.JSON;
import com.tracy.agent.dubbo.RpcClient;
import com.tracy.agent.model.Constants;
import com.tracy.agent.registry.Endpoint;
import com.tracy.agent.registry.EtcdRegistry;
import com.tracy.agent.registry.IRegistry;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    private IRegistry registry = new EtcdRegistry(System.getProperty(Constants.ETCD_URL));

    private RpcClient rpcClient = new RpcClient(registry);
    private Random random = new Random();
    private List<Endpoint> endpoints = null;
    private final Object lock = new Object();
    private OkHttpClient httpClient = new OkHttpClient();


    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        String type = System.getProperty(Constants.TYPE);
        if (Constants.CONSUMER.equals(type)) {
            return consumer(interfaceName, method, parameterTypesString, parameter);
        } else if (Constants.PROVIDER.equals(type)) {
            return provider(interfaceName, method, parameterTypesString, parameter);
        } else {
            return "Environment variable type is needed to set to provider or consumer.";
        }
    }

    private byte[] provider(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        Object result = rpcClient.invoke(interfaceName, method, parameterTypesString, parameter);
        return (byte[]) result;
    }

    private Object consumer(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = registry.find(interfaceName);
                }
            }
        }
        if (CollectionUtils.isEmpty(endpoints)) {
            throw new RuntimeException("interfaceName:" + interfaceName + " can not find!");
        }
        logger.info("endpoints detail:{}", JSON.toJSONString(endpoints));
        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));

        String url = "http://" + endpoint.getHost() + ":" + endpoint.getPort();

        RequestBody requestBody = new FormBody.Builder()
                .add("interface", interfaceName)
                .add("method", method)
                .add("parameterTypesString", parameterTypesString)
                .add("parameter", parameter)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body();
        }
    }
}
