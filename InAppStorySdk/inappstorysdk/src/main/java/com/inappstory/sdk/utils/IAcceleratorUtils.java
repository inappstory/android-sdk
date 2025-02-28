package com.inappstory.sdk.utils;

public interface IAcceleratorUtils {
    void init(double frequency, IAcceleratorInitCallback callback);

    void subscribe(IAcceleratorSubscriber subscriber);
    void unsubscribe(IAcceleratorSubscriber subscriber);
}
