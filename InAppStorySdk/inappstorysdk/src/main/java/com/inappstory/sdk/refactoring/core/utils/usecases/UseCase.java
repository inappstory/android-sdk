package com.inappstory.sdk.refactoring.core.utils.usecases;

import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;

public interface UseCase<T> {
    void invoke(ResultCallback<T> callback);
}
