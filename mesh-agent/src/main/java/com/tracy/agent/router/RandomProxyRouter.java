package com.tracy.agent.router;

import com.tracy.agent.registry.Endpoint;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * 随机获取
 *
 * @author tracy.
 * @create 2018-05-31 11:13
 **/
public class RandomProxyRouter extends AbstractRouter {
    public RandomProxyRouter() throws Exception {
        super();
    }


    @Override
    public Endpoint find(String serviceName) {
        List<Endpoint> endpoints = serverMap.get(serviceName);
        if (CollectionUtils.isEmpty(endpoints)) {
            throw new RuntimeException("No services available for interface:[" + serviceName + "]");
        }
        Random random = new Random();
        return endpoints.get(random.nextInt(endpoints.size()));
    }

    @Override
    public void reshard() {
        // TODO: 2018/5/31
    }
}
