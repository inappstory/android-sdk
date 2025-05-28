package com.inappstory.sdk.stories.utils;

import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoopedExecutor {

    public LoopedExecutor(long startDelay, long period) {
        this.startDelay = startDelay;
        this.period = period;
    }

    private long startDelay;
    private long period;

    public void init(final Runnable runnable) {
        freeExecutor();
        if (executorThread.isShutdown()) {
            executorThread =
                    Executors.newSingleThreadExecutor();
        }
        statisticScheduledThread.scheduleAtFixedRate(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                if (count == 100) {
                    shutdown();
                    init(runnable);
                } else {
                    synchronized (taskLaunchLock) {
                        if (taskLaunched) return;
                        taskLaunched = true;
                    }
                    executorThread.submit(runnable);
                }
            }
        }, startDelay, period, TimeUnit.MILLISECONDS);
    }

    private final Object taskLaunchLock = new Object();
    boolean taskLaunched;

    public void freeExecutor() {
        synchronized (taskLaunchLock) {
            taskLaunched = false;
        }
    }

    public void shutdown() {
        executorThread.shutdown();
        statisticScheduledThread.shutdown();
    }


    private ScheduledTPEManager statisticScheduledThread =
            new ScheduledTPEManager();


    private ExecutorService executorThread = Executors.newSingleThreadExecutor();
}
