package com.tracy.agent.router;

import com.tracy.agent.registry.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 加权轮询负载均衡
 *
 * @author tracy.
 * @create 2018-05-31 14:26
 **/
public class WeightedRandomRouter extends AbstractRouter {
    private static Logger logger = LoggerFactory.getLogger(WeightedRandomRouter.class);

    /**
     * 初始化重新构造serverMap,以填充List,然后再简单随机。达到加权分散的效果
     */
    public WeightedRandomRouter() throws Exception {
        super();
    }

    @Override
    public Endpoint find(String serviceName) {
        List<Endpoint> endpoints = serverMap.get(serviceName);
        return getBestServer(endpoints);
    }

    @Override
    public void reshard() {

    }

    /**
     *
     * @param serverList
     * @return
     */
    private Endpoint getBestServer(List<Endpoint> serverList) {
        Endpoint server;
        Endpoint best = null;
        int total = 0;
        for (Endpoint aServerList : serverList) {
            //当前服务器对象
            server = aServerList;
            //当前服务器已宕机，排除
            if (server.isDown()) {
                continue;
            }
            server.setCurrentWeight(server.getCurrentWeight() + server.getEffectiveWeight());
            total += server.getEffectiveWeight();
            if (server.getEffectiveWeight() < server.getScore()) {
                server.setEffectiveWeight(server.getEffectiveWeight() + 1);
            }
            if (best == null || server.getCurrentWeight() > best.getCurrentWeight()) {
                best = server;
            }
        }
        if (best == null) {
            return null;
        }
        best.setCurrentWeight(best.getCurrentWeight() - total);
        best.setCheckedDate(new Date());
        return best;
    }
}

