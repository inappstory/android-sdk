package com.inappstory.sdk.refactoring.core.utils.models;

public interface IResultCallback<T> {
    void invoke(Result<T> result);
}
