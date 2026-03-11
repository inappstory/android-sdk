package com.inappstory.sdk.refactoring.core.utils.results;

public interface IResultCallback<T> {
    void invoke(Result<T> result);
}
