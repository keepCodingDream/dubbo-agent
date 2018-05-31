package com.tracy.agent.router;

import com.tracy.agent.registry.Endpoint;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 加权轮询负载均衡
 *
 * @author tracy.
 * @create 2018-05-31 14:26
 **/
public class WeightedRandomRouter extends AbstractRouter {

    /**
     * 初始化重新构造serverMap,以填充List,然后再简单随机。达到加权分散的效果
     *
     * @throws Exception
     */
    public WeightedRandomRouter() throws Exception {
        super();
        for (Map.Entry<String, List<Endpoint>> entry : serverMap.entrySet()) {
            List<Endpoint> endpoints = entry.getValue();
            int sumScore = 0;
            long min = Long.MAX_VALUE;
            for (Endpoint item : endpoints) {
                sumScore += item.getScore();
                if (item.getScore() < min) {
                    min = item.getScore();
                }
            }
            int totalSize = (int) (sumScore / min);
            for (Endpoint item : endpoints) {
                long currentScore = item.getScore();
                //按照比例，当前节点应该有几个
                int willCount = (int) (currentScore / sumScore) * totalSize;
                while (willCount > 1) {
                    endpoints.add(item);
                    --willCount;
                }
            }
            Collections.shuffle(endpoints);
        }
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

    }
}
