package com.tracy.agent.router;

import com.tracy.agent.model.Constants;
import com.tracy.agent.registry.Endpoint;
import com.tracy.agent.registry.EtcdRegistry;
import com.tracy.agent.registry.IRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由器的抽象定义
 *
 * @author tracy.
 * @create 2018-05-31 11:14
 **/
public abstract class AbstractRouter implements Router {
    private IRegistry registry = new EtcdRegistry(System.getProperty(Constants.ETCD_URL));
    protected Map<String, List<Endpoint>> serverMap;

    public AbstractRouter() throws Exception {
        serverMap = new ConcurrentHashMap<>();
        initConsumerEndpoint();
    }

    private void initConsumerEndpoint() throws Exception {
        List<String> allServices = getAllServiceNames();
        for (String seriviceName : allServices) {
            List<Endpoint> serivceList = registry.find(seriviceName);
            serverMap.put(seriviceName, serivceList);
        }
    }

    /**
     * 允许子类重写此方法。例如通过读取dubbo的Zookeeper配置获取所有注册的service
     * <p>
     * 目前先构造默认的
     *
     * @return 所有服务列表
     */
    protected List<String> getAllServiceNames() {
        List<String> serviceNames = new ArrayList<>(1);
        serviceNames.add("com.alibaba.dubbo.performance.demo.provider.IHelloService");
        return serviceNames;
    }
}
