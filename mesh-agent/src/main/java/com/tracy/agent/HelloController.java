package com.tracy.agent;

import com.tracy.agent.dubbo.RpcClient;
import com.tracy.agent.model.Constants;
import com.tracy.agent.registry.Endpoint;
import com.tracy.agent.registry.EtcdRegistry;
import com.tracy.agent.registry.IRegistry;
import com.tracy.agent.router.RandomProxyRouter;
import com.tracy.agent.router.Router;
import com.tracy.agent.router.WeightedRandomRouter;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class HelloController implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    private IRegistry registry = new EtcdRegistry(System.getProperty(Constants.ETCD_URL));

    private RpcClient rpcClient = new RpcClient(registry);
    private OkHttpClient httpClient = new OkHttpClient();

    private Router router;

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

    /**
     * 调用同机器rpc服务,返回结果
     */
    private byte[] provider(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        Object result = rpcClient.invoke(interfaceName, method, parameterTypesString, parameter);
        return (byte[]) result;
    }

    /**
     * 找到对应代理，转发请求
     */
    private Object consumer(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {
        Endpoint endpoint = router.find(interfaceName);
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
        }
    }
}
