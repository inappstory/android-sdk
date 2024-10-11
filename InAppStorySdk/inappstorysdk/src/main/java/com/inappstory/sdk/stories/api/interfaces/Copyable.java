package com.inappstory.sdk.stories.api.interfaces;

public interface Copyable<T> {
    T copy();
    T mergedCopy(T comparedObject);
}
