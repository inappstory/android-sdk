package com.inappstory.sdk.core.ui.screens;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public abstract class AbstractScreenHolder<T extends BaseScreen, K> implements IScreenHolder<T, K> {
    T currentScreen;
    K lastOpenedData;
    protected final Object screenLock = new Object();
    private boolean inLaunchProcess;


    @Override
    public void startLaunchProcess() {
        synchronized (screenLock) {
            inLaunchProcess = true;
        }
    }

    @Override
    public boolean isLaunchProcessStarted() {
        synchronized (screenLock) {
            return inLaunchProcess;
        }
    }

    @Override
    public boolean isOpened(@NonNull K data) {
        return lastOpenedData != null && lastOpenedData.equals(data);
    }

    @Override
    public void lastOpenedData(K data) {
        this.lastOpenedData = data;
    }

    @Override
    public boolean isOpened() {
        synchronized (screenLock) {
            return currentScreen != null;
        }
    }

    @Override
    public T getScreen() {
        synchronized (screenLock) {
            return currentScreen;
        }
    }

    @Override
    public void subscribeScreen(T screen) {
        synchronized (screenLock) {
            inLaunchProcess = false;
            currentScreen = screen;
        }
    }

    @Override
    public void unsubscribeScreen(T screen) {
        synchronized (screenLock) {
            inLaunchProcess = false;
            if (currentScreen == screen) {
                currentScreen = null;
            }
        }
    }

    @Override
    public void useCurrentReader(GetScreenCallback<T> callback) {
        T screen = getScreen();
        if (screen != null) callback.get(screen);
    }

    @Override
    public void closeScreen() {
        T screen = null;
        synchronized (screenLock) {
            screen = getScreen();
        }
        final T finalScreen = screen;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (finalScreen != null) {
                    finalScreen.close();
                }
            }
        });
    }

    @Override
    public void forceCloseScreen(ForceCloseReaderCallback callback) {
        T screen = null;
        synchronized (screenLock) {
            screen = getScreen();
        }
        final T finalScreen = screen;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (finalScreen != null) {
                    finalScreen.forceFinish();
                }
            }
        });
    }
}
