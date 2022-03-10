package com.inappstory.sdk.stories.cache;

import java.lang.reflect.Type;

public interface SimpleApiCallback<T> {
    void onSuccess(T response);
    void onError(String message);
    Type getType();
}