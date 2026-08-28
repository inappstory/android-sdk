package com.inappstory.sdk.stories.utils;


import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoopedExecutor {

    public LoopedExecutor(long startDelay, long period) {
        this.startDelay = startDelay;
        this.period = period;
        launch();
    }


    public LoopedExecutor(long startDelay, long period, String name) {
        this.startDelay = startDelay;
        this.period = period;
        this.name = name;
        launch();
    }

    private String name = "";
    private final long startDelay;
    private final long period;
    private boolean interrupted;
    Runnable runnable = null;

    public void task(final Runnable runnable) {
        synchronized (taskLaunchLock) {
            this.runnable = runnable;
        }
    }

    public void cancelTask() {
        synchronized (taskLaunchLock) {
            runnable = null;
            taskLaunched = false;
        }
    }

    private void launch() {
        if (managerThread.isShutdown())
            managerThread = Executors.newSingleThreadExecutor();
        interrupted = false;
        managerThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(startDelay);
                    while (true) {
                        synchronized (taskLaunchLock) {
                            if (!taskLaunched && runnable != null) {
                                Log.e("loopedExecutor", "launchTask " + name);
                                taskLaunched = true;
                                runnable.run();
                            }
                            if (interrupted) {
                                break;
                            }
                        }
                        Thread.sleep(period);
                        synchronized (taskLaunchLock) {
                            if (interrupted) {
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private final Object taskLaunchLock = new Object();
    boolean taskLaunched;

    public void freeExecutor() {
        synchronized (taskLaunchLock) {
            taskLaunched = false;
        }
    }

    public void shutdown() {
        synchronized (taskLaunchLock) {
            interrupted = true;
            runnable = null;
            managerThread.shutdown();
        }
    }

    private ExecutorService managerThread = Executors.newSingleThreadExecutor();
}
