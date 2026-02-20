package com.inappstory.sdk.refactoring.core.utils.models;

public interface Mapper<T, X> {
    X convert(T obj);
}
