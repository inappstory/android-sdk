package com.inappstory.sdk.stories.statistic;

public class ProfilingTask {
    String uniqueHash;
    String name;
    long startTime;
    long endTime;
    String sessionId;
    String userId;
    boolean isReady;

    @Override
    public String toString() {
        return "ProfilingTask{" +
                "uniqueHash='" + uniqueHash + '\'' +
                ", name='" + name + '\'' +
                ", endTime=" + endTime +
                '}';
    }
}
