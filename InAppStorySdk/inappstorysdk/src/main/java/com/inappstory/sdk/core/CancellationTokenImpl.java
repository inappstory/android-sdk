package com.inappstory.sdk.core;


import android.util.Log;

import com.inappstory.sdk.CancellationTokenCancelResult;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationTokenImpl implements CancellationTokenWithStatus {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean disabled = new AtomicBoolean(false);
    private final String uid = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();

    public CancellationTokenImpl(String operationData) {
        Log.e("IAS_SDK_Cancel_Token", uid + " Created " + operationData);
    }

    public CancellationTokenImpl() {

    }

    public boolean cancelled() {
        boolean cancelledStatus = cancelled.get();
        Log.e("IAS_SDK_Cancel_Token", uid + " Status: " + cancelled);
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
    public CancellationTokenCancelResult cancel() {
        if (disabled.get()) {
            Log.e("IAS_SDK_Cancel_Token", uid + " can't be cancelled. Operation already finished");
            return CancellationTokenCancelResult.ERROR_OPERATION_FINISHED;
        } else {
            boolean success = cancelled.compareAndSet(false, true);
            if (success) {
                Log.e("IAS_SDK_Cancel_Token", uid + " cancelled");
                return CancellationTokenCancelResult.SUCCESS;
            } else {
                Log.e("IAS_SDK_Cancel_Token", uid + " already cancelled");
                return CancellationTokenCancelResult.ERROR_ALREADY_CANCELLED;
            }
        }
    }

    @Override
    public String getUniqueId() {
        return uid;
    }
}
