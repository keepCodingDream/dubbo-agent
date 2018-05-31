package com.tracy.agent.registry;

import java.util.List;

public interface IRegistry {

    /**
     * 服务注册
     *
     * @param serviceName 服务名称
     * @param port        端口
     * @throws Exception 注册异常
     */
    void register(String serviceName, int port) throws Exception;

    /**
     * 获取服务列表
     *
     * @param serviceName 服务名称
     * @return 服务列表
     * @throws Exception 获取异常
     */
    List<Endpoint> find(String serviceName) throws Exception;
}
