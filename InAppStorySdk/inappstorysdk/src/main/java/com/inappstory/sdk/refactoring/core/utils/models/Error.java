package com.inappstory.sdk.refactoring.core.utils.models;

public class Error<T> implements Result<T> {
    public String reason() {
        return reason;
    }

    final String reason;

    public Error(String reason) {
        this.reason = reason;
    }
}
