package com.inappstory.sdk.refactoring.core.utils.observers;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Observable<T> {
    private T value;
    private final Object listenerLock = new Object();
    private final List<Observer<T>> listeners = new ArrayList<>();

    public Observable() {
        this.value = null;
    }

    public Observable(T initialValue) {
        synchronized (valueLock) {
            this.value = initialValue;
        }
    }

    public boolean subscribe(Observer<T> listener) {
        synchronized (listenerLock) {
            if (listeners.contains(listener)) return false;
            listeners.add(listener);
        }
        return true;
    }


    public boolean subscribeAndGetValue(final Observer<T> listener) {
        T value = getValue();
        synchronized (listenerLock) {
            if (listeners.contains(listener)) return false;
            listeners.add(listener);
        }
        listener.onUpdate(value);
        return true;
    }


    public boolean subscribeAndGetValueForced(final Observer<T> listener) {
        T value = getValue();
        synchronized (listenerLock) {
            if (listeners.contains(listener)) {
                listener.onUpdate(value);
                return false;
            }
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
        synchronized (valueLock) {
            return value;
        }
    }

    public void setValue(T value) {
        synchronized (valueLock) {
            this.value = value;
        }
    }

    private final Object valueLock = new Object();

    public List<Observer<T>> getSubscribers() {
        synchronized (listenerLock) {
            return new ArrayList<>(listeners);
        }
    }

    public void updateValue(final T value) {
        synchronized (valueLock) {
            if (Objects.equals(this.value, value)) return;
            this.value = value;
        }
        List<Observer<T>> subs = getSubscribers();
        for (Observer<T> listener : subs) {
            listener.onUpdate(value);
        }
    }
}
