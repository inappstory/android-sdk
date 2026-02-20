package com.inappstory.sdk.refactoring.core.utils.models;

public class Success<T> implements Result<T> {
    public T data() {
        return data;
    }

    final T data;

    public Success(T data) {
        this.data = data;
    }
}
