package com.tracy.agent.dubbo.model;

import com.tracy.agent.model.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class InnerFuture implements Future<Object> {
    private CountDownLatch latch = new CountDownLatch(1);
    private Response response;
    private long startTime;

    public InnerFuture() {
        startTime = System.currentTimeMillis();
    }

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
    public Response get() throws InterruptedException {
        latch.await();
        try {
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(-1, null);
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean b = latch.await(timeout, unit);
        if (b) {
            return response;
        } else {
            return new Response(-1, null);
        }
    }

    public void done(Response response) {
        this.response = response;
        latch.countDown();
    }

    public long getStartTime() {
        return startTime;
    }

}
