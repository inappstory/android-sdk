package com.inappstory.sdk.game.cache;

public interface UseCaseCallback<T> {
    void onError(UseCaseError error);
    void onSuccess(T result);
}
