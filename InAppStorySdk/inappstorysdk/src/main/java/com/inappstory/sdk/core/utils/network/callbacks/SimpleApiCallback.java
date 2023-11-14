package com.inappstory.sdk.core.utils.network.callbacks;

import java.lang.reflect.Type;

public interface SimpleApiCallback<T> {
    void onSuccess(T response, Object... args);
    void onError(String message);
    Type getType();
}
