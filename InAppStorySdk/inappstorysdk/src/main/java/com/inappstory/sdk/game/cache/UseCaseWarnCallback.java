package com.inappstory.sdk.game.cache;

public interface UseCaseWarnCallback<T> extends UseCaseCallback<T> {
    void onWarn(String message);
}
