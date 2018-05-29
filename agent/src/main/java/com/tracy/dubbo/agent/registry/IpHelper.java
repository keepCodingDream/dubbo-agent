package com.tracy.dubbo.agent.registry;

import java.net.InetAddress;

public class IpHelper {

    public static String getHostIp() throws Exception {

        return InetAddress.getLocalHost().getHostAddress();
    }
}
