package com.inappstory.sdk.refactoring.core.utils.models;

public class NoInternetError<T> extends Error<T> {
    public String reason() {
        return "No internet connection";
    }

    public NoInternetError() {
        super("");
    }
}
