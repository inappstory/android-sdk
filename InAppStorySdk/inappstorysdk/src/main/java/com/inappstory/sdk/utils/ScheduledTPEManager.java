package com.inappstory.sdk.utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledTPEManager {
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private final Object scheduledThreadLock = new Object();

    public void shutdownNow() {
        synchronized (scheduledThreadLock) {
            if (!executor.isShutdown()) {
                executor.shutdownNow();
            }
        }
    }

    public void shutdown() {
        synchronized (scheduledThreadLock) {
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }

    public ScheduledFuture<?> scheduleAtFixedRate(
            Runnable command,
            long initialDelay,
            long period,
            TimeUnit unit
    ) {
        synchronized (scheduledThreadLock) {
            if (executor.isShutdown()) {
                executor = new ScheduledThreadPoolExecutor(1);
            }
            return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
        }
    }
}
