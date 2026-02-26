package com.inappstory.sdk.refactoring.core.utils.models;

public interface UseCase<T> {
    void invoke(ResultCallback<T> callback);
}
