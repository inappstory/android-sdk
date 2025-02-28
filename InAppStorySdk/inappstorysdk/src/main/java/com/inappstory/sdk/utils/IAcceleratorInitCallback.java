package com.inappstory.sdk.utils;

public interface IAcceleratorInitCallback {
    void onSuccess();
    void onError(String type, String message);
}
