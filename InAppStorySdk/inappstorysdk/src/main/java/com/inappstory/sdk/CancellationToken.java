package com.inappstory.sdk;

public interface CancellationToken {
    void cancel();
    String getUniqueId();
}
