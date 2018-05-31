package com.tracy.agent.registry;

public class Endpoint {
    private final String host;
    private final int port;
    /**
     * 默认配置是1.5G的内存
     */
    private long score = 1543503872L;

    public Endpoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Endpoint)) {
            return false;
        }
        Endpoint other = (Endpoint) o;
        return other.host.equals(this.host) && other.port == this.port;
    }

    @Override
    public int hashCode() {
        return host.hashCode() + port;
    }
}
