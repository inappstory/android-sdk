package com.inappstory.sdk.stories.utils;


public class SingleTimeEvent<T> extends Observable<T> {
    @Override
    public void updateValue(T value) {
        super.updateValue(value);
        setValue(null);
    }
}
