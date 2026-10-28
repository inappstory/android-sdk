package com.inappstory.sdk.stories.utils;

public abstract class LoopedExecutorTask implements Runnable {
    private final boolean longTask;

    public boolean isLongTask() {
        return longTask;
    }

    public LoopedExecutorTask(boolean longTask) {
        this.longTask = longTask;
    }

    public LoopedExecutorTask() {
        longTask = false;
    }
}
