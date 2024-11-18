package com.inappstory.sdk.stories.utils;

public interface Observer<T> {
    void onUpdate(T newValue);
}
