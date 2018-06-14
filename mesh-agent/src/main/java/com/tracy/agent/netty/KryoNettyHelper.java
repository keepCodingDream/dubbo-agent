package com.tracy.agent.netty;

import com.tracy.agent.dubbo.model.InnerFuture;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tracy.
 * @create 2018-06-07 19:49
 **/
public class KryoNettyHelper {
    private static final ConcurrentHashMap<Long, InnerFuture> RESPONSE_MAP = new ConcurrentHashMap<>();

    /**
     * 调用之前，记录下id
     */
    public static void preCall(long requestId, InnerFuture future) {
        RESPONSE_MAP.put(requestId, future);
    }

    public static InnerFuture getResponse(Long requestId) {
        return RESPONSE_MAP.get(requestId);
    }

    public static void removeRequest(Long requestId) {
        RESPONSE_MAP.remove(requestId);
    }
}
