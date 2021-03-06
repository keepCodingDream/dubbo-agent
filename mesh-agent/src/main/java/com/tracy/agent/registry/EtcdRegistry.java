package com.tracy.agent.registry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tracy.agent.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EtcdRegistry implements IRegistry {
    private Logger logger = LoggerFactory.getLogger(EtcdRegistry.class);
    // 该EtcdRegistry没有使用etcd的Watch机制来监听etcd的事件
    // 添加watch，在本地内存缓存地址列表，可减少网络调用的次数
    // 使用的是简单的随机负载均衡，如果provider性能不一致，随机策略会影响性能

    private final String rootPath = "dubbomesh";

    public EtcdRegistry(String registryAddress) {
        String type = System.getProperty(Constants.TYPE);
        if (Constants.PROVIDER.equals(type)) {
            try {
                int port = Integer.valueOf(System.getProperty("server.port"));
                // TODO: 2018/5/31 dynamic find the service need to register
                register("com.alibaba.dubbo.performance.demo.provider.IHelloService", port);
                keepAlive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void register(String serviceName, int port) throws Exception {
        //服务注册的key为:  /dubbomesh/com.some.package.IHelloService/192.168.100.100:2000
        String strKey = MessageFormat.format("/{0}/{1}/{2}:{3}", rootPath, serviceName, IpHelper.getHostIp(), String.valueOf(port));
        // 原先权重使用memory,但发现效果不好，现在使用JVM参数，后期可以直接从启动参数中获取
        String value = System.getProperty("weight");
        if (StringUtils.isEmpty(value)) {
            logger.warn("weight is null! will use default 1");
            value = "1";
        }
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(System.getProperty(Constants.ETCD_URL) + "/v2/keys/" + strKey + "?value=" + value, "");
        logger.info("Register a new service at:{}", strKey);
    }

    @Override
    public List<Endpoint> find(String serviceName) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = System.getProperty(Constants.ETCD_URL) + "/v2/keys/" + rootPath + "/" + serviceName;
        JSONObject object = restTemplate.getForObject(url, JSONObject.class);
        logger.info("find etcd returned:{}", object.toJSONString());
        JSONArray nodes = object.getJSONObject("node").getJSONArray("nodes");
        List<Endpoint> endpoints = new ArrayList<>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject objectItem = nodes.getJSONObject(i);
            String key = objectItem.getString("key");
            String value = objectItem.getString("value");
            int index = key.lastIndexOf("/");
            String endpointStr = key.substring(index + 1, key.length());
            String host = endpointStr.split(":")[0];
            int port = Integer.valueOf(endpointStr.split(":")[1]);
            Endpoint endpoint = new Endpoint(host, port);
            endpoint.setScore(Integer.valueOf(value));
            endpoints.add(endpoint);
        }
        return endpoints;
    }

    /**
     * 发送心跳到ETCD,表明该host是活着的
     */
    private void keepAlive() {
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    try {
                        while (true) {
                            // TODO: 2018/6/7
                            // keep alive
                            logger.info("KeepAlive");
                            Thread.sleep(30000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
