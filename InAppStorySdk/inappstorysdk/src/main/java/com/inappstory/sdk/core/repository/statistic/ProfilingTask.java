package com.inappstory.sdk.core.repository.statistic;

public class ProfilingTask {
    String uniqueHash;
    String name;
    long startTime;
    long endTime;
    String sessionId;
    String userId;
    boolean isReady;
    boolean isAllowToForceSend;

    @Override
    public String toString() {
        return "ProfilingTask{" +
                "uniqueHash='" + uniqueHash + '\'' +
                ", name='" + name + '\'' +
                ", endTime=" + endTime +
                '}';
    }
}
