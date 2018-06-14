package com.tracy.agent.model;

/**
 * @author tracy.
 * @create 2018-06-07 15:13
 **/
public class Response {
    private long requestId;
    private byte[] value;
    private Throwable exception;

    public Response(long requestId, byte[] value) {
        this.requestId = requestId;
        this.value = value;

    }

    public Response(Throwable exception) {
        this.exception = exception;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
