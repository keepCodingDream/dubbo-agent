package com.tracy.agent.registry;

import java.util.Date;

public class Endpoint {
    private final String host;
    private final int port;
    private boolean down;
    private int effectiveWeight;
    private int currentWeight = 0;
    private Date checkedDate;
    /**
     * 默认配置是1.5G的内存
     */
    private int score = 1543503872;

    public Endpoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        this.currentWeight = score;
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

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public int getEffectiveWeight() {
        return effectiveWeight;
    }

    public void setEffectiveWeight(int effectiveWeight) {
        this.effectiveWeight = effectiveWeight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
    }
}
