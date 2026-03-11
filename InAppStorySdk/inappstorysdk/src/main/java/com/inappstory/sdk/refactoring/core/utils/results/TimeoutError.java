package com.inappstory.sdk.refactoring.core.utils.results;

public class TimeoutError<T> extends Error<T> {
    public TimeoutError(String reason) {
        super(reason);
    }
}
