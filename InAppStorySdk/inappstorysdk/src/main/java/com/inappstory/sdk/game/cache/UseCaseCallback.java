package com.inappstory.sdk.game.cache;

public interface UseCaseCallback<T> {
    void onError(String message);
    void onSuccess(T result);
}
