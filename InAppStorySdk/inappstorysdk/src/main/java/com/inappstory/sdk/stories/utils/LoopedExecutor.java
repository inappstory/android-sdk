package com.inappstory.sdk.stories.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoopedExecutor {

    public LoopedExecutor(long startDelay, long period) {
        this.startDelay = startDelay;
        this.period = period;
        launch();
    }

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
        if (executorThread.isShutdown())
            executorThread = Executors.newSingleThreadExecutor();
        managerThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(startDelay);
                    while (true) {
                        synchronized (taskLaunchLock) {
                            if (!taskLaunched && runnable != null) {
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
            executorThread.shutdown();
            managerThread.shutdown();
        }
    }

    private ExecutorService executorThread = Executors.newSingleThreadExecutor();
    private ExecutorService managerThread = Executors.newSingleThreadExecutor();
}
