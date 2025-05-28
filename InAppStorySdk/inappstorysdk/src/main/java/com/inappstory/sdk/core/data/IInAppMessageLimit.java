package com.inappstory.sdk.core.data;

public interface IInAppMessageLimit {
    boolean canOpen();
    long expireInSeconds();
    int messageId();
}
