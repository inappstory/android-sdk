package com.inappstory.sdk.utils.systemapi.accelerator;

public interface IAcceleratorInitCallback {
    void onSuccess();
    void onError(String type, String message);
}
