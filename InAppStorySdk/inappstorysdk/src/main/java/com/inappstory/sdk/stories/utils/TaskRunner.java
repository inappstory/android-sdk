package com.inappstory.sdk.stories.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(final Callable<R> callable, final Callback<R> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final R result = callable.call();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onComplete(result);
                        }
                    });
                } catch (Exception e) {
                    Log.d("InAppStory_Task", e.getCause() + " " + e.getMessage());
                }
            }
        });
    }
}