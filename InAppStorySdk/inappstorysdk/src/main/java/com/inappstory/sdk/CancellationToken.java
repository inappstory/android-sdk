package com.inappstory.sdk;

public interface CancellationToken {
    CancellationTokenCancelResult cancel();
    String getUniqueId();
}
