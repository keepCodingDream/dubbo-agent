package com.tracy.agent.dubbo.model;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RpcFuture implements Future<Object> {
    private CountDownLatch latch = new CountDownLatch(1);
    private long startTime = System.currentTimeMillis();
    private RpcResponse response;
    private byte[] defaultResponse = new byte[]{-1, -1, -1, -1};

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException {
        latch.await();
        try {
            return response.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultResponse;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean b = latch.await(timeout, unit);
        if (b) {
            return response.getBytes();
        } else {
            return defaultResponse;
        }
    }

    public void done(RpcResponse response) {
        this.response = response;
        latch.countDown();
    }

    public long getStartTime() {
        return startTime;
    }

}
