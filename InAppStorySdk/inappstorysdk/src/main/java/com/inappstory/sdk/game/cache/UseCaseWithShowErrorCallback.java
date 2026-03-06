package com.inappstory.sdk.game.cache;

public interface UseCaseWithShowErrorCallback<T> extends UseCaseCallback<T> {
    void onShowError(String message);
}
