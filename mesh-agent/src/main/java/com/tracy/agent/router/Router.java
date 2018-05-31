package com.tracy.agent.router;

import com.tracy.agent.registry.Endpoint;

/**
 * 抽象定义consumer router 的动作
 *
 * @author lurenjie
 */
public interface Router {
    Endpoint find(String serviceName);
    void reshard();
}
