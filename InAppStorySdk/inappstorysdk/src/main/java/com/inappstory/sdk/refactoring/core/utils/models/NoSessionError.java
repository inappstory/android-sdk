package com.inappstory.sdk.refactoring.core.utils.models;

public class NoSessionError<T> extends Error<T> {
    public String reason() {
        return "Can't retrieve session";
    }

    public NoSessionError() {
        super("");
    }
}
