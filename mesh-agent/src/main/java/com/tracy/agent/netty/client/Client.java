package com.tracy.agent.netty.client;


import com.tracy.agent.model.Request;
import com.tracy.agent.model.Response;

import java.net.InetSocketAddress;

public interface Client {

    void connect(InetSocketAddress socketAddress) throws InterruptedException;

    Response sent(Request request);

    InetSocketAddress getRemoteAddress();

    void close();
}
