package com.inappstory.sdk.stories.utils;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> {
    private T value;
    private final List<Observer<T>> listeners = new ArrayList<>();

    public Observable() {
        this.value = null;
    }

    public Observable(T initialValue) {
        this.value = initialValue;
    }

    public boolean subscribe(Observer<T> listener) {
        if (listeners.contains(listener)) return false;
        listeners.add(listener);
        return true;
    }


    public boolean subscribeAndGetValue(Observer<T> listener) {
        if (listeners.contains(listener)) return false;
        listeners.add(listener);
        listener.onUpdate(value);
        return true;
    }

    public void unsubscribe(Observer<T> listener) {
        listeners.remove(listener);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void updateValue(T value) {
        setValue(value);
        for (Observer<T> listener : listeners) {
            listener.onUpdate(value);
        }
    }
}
