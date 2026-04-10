package com.inappstory.sdk.refactoring.core.utils.observers;


public class SingleTimeEvent<T> extends Observable<T> {
    @Override
    public void updateValue(T value) {
        super.updateValue(value);
        setValue(null);
    }
}
