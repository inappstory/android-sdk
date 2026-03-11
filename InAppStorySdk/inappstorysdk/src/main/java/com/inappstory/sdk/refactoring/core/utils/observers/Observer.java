package com.inappstory.sdk.refactoring.core.utils.observers;

public interface Observer<T> {
    void onUpdate(T newValue);
}
