package com.inappstory.sdk.refactoring.core.utils.usecases;

public interface Mapper<T, X> {
    X convert(T obj);
}
