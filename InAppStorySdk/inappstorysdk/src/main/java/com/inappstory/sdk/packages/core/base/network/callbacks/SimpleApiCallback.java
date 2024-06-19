package com.inappstory.sdk.packages.core.base.network.callbacks;

import java.lang.reflect.Type;

public interface SimpleApiCallback<T> {
    void onSuccess(T response, Object... args);
    void onError(String message);
    Type getType();
}
