package com.inappstory.sdk.core;


import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationTokenImpl implements CancellationTokenWithStatus {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final String uid = UUID.randomUUID().toString();

    public boolean cancelled() {
        return cancelled.get();
    }

    @Override
    public void cancel() {
        cancelled.compareAndSet(false, true);
    }

    @Override
    public String getUniqueId() {
        return uid;
    }
}
