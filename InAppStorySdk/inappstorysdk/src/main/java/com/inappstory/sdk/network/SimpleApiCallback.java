package com.inappstory.sdk.network;

import java.lang.reflect.Type;

public interface SimpleApiCallback<T> {
    void onSuccess(T response);
    void onError(String message);
    Type getType();
}
