package com.inappstory.sdk.utils;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class SingleTimeLiveEvent<T> extends SingleLiveEvent<T> {
    @MainThread
    public void observe(LifecycleOwner owner, final Observer<? super T> observer) {
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                if (t != null) {
                    observer.onChanged(t);
                    postValue(null);
                }
            }
        });
    }

    @MainThread
    @Override
    public void observeForever(@NonNull final Observer<? super T> observer) {
        super.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if (t != null) {
                    observer.onChanged(t);
                    postValue(null);
                }
            }
        });
    }
}