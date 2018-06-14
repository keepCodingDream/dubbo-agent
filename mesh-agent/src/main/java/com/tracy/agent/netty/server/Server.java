package com.tracy.agent.netty.server;

/**
 * @author tracy.
 * @create 2018-06-07 15:33
 **/
public interface Server {
    void start() throws InterruptedException;

    void stop();
}
