package com.inappstory.sdk.core;

import com.inappstory.sdk.CancellationToken;

public interface CancellationTokenWithStatus extends CancellationToken {
    boolean cancelled();
    long creationTime();
    void disable();
}
