package com.inappstory.sdk.core.api;

public interface IASStatisticProfiling extends Disabled {
    String addTask(String name);
    String addTask(String name, String hash);
    void setReady(String hash, boolean force);
    void setReady(String hash);
    void cleanTasks();
}
