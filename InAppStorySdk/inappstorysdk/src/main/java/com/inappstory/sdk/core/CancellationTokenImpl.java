package com.inappstory.sdk.core;


import android.util.Log;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationTokenImpl implements CancellationTokenWithStatus {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean disabled = new AtomicBoolean(false);
    private final String uid = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();

    public CancellationTokenImpl(String operationData) {
        Log.e("IAS_Cancel_Operation", uid + " Created " + operationData);
    }

    public CancellationTokenImpl() {

    }

    public boolean cancelled() {
        boolean cancelledStatus = cancelled.get();
        Log.e("IAS_Cancel_Operation", uid + " Status: " + cancelled);
        return cancelledStatus;
    }

    @Override
    public long creationTime() {
        return creationTime;
    }

    @Override
    public void disable() {
        disabled.compareAndSet(false, true);
    }

    @Override
    public void cancel() {
        if (disabled.get()) {
            Log.e("IAS_Cancel_Operation", uid + " Can't be cancelled. Operation already finished");
        } else {
            Log.e("IAS_Cancel_Operation", uid + " Cancelled");
            cancelled.compareAndSet(false, true);
        }
    }

    @Override
    public String getUniqueId() {
        return uid;
    }
}
