package com.inappstory.sdk.share;

public interface ShareListener {
    void onSuccess(boolean shared);
    void onCancel();
}