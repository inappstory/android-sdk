package com.inappstory.sdk.refactoring.core.utils.models;

public abstract class ResultCallback<T> implements IResultCallback<T> {
    @Override
    public void invoke(Result<T> result) {
        if (result instanceof Success)
            success(((Success<T>) result).data());
        else if (result instanceof Error)
            error((Error<T>) result);
    }

    public abstract void success(T result);

    public void error(Error<T> result) {
    }
}
