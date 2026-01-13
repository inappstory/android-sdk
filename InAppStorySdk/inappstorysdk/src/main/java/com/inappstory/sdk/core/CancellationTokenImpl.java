package com.inappstory.sdk.core;


import android.util.Log;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationTokenImpl implements CancellationTokenWithStatus {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final String uid = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();

    public CancellationTokenImpl() {
        Log.e("CancellationCheck", uid + " Created");
    }

    public boolean cancelled() {
        boolean cancelledStatus = cancelled.get();
        Log.e("CancellationCheck", uid + " Status: " + cancelled);
        return cancelledStatus;
    }

    @Override
    public long creationTime() {
        return creationTime;
    }

    @Override
    public void cancel() {
        Log.e("CancellationCheck", uid + " Cancel");
        cancelled.compareAndSet(false, true);
    }

    @Override
    public String getUniqueId() {
        return uid;
    }
}
