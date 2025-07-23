package com.inappstory.sdk.stories.utils;


import java.util.ArrayList;
import java.util.List;

public class Observable<T> {
    private T value;
    private final Object listenerLock = new Object();
    private final List<Observer<T>> listeners = new ArrayList<>();

    public Observable() {
        this.value = null;
    }

    public Observable(T initialValue) {
        this.value = initialValue;
    }

    public boolean subscribe(Observer<T> listener) {
        synchronized (listenerLock) {
            if (listeners.contains(listener)) return false;
            listeners.add(listener);
        }
        return true;
    }


    public boolean subscribeAndGetValue(final Observer<T> listener) {
        synchronized (listenerLock) {
            if (listeners.contains(listener)) return false;
            listeners.add(listener);
        }
        listener.onUpdate(value);
        return true;
    }

    public void unsubscribe(Observer<T> listener) {
        synchronized (listenerLock) {
            listeners.remove(listener);
        }
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public List<Observer<T>> getSubscribers() {
        synchronized (listenerLock) {
            return new ArrayList<>(listeners);
        }
    }

    public void updateValue(final T value) {
        setValue(value);
        List<Observer<T>> subs = getSubscribers();
        for (Observer<T> listener : subs) {
            listener.onUpdate(value);
        }
    }
}
