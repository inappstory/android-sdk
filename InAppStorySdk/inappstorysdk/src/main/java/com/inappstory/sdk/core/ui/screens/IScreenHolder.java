package com.inappstory.sdk.core.ui.screens;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public interface IScreenHolder<T, K> {
    boolean isOpened();
    boolean isOpened(@NonNull K data);
    void lastOpenedData(K data);
    T getScreen();
    void startLaunchProcess();
    boolean isLaunchProcessStarted();
    void subscribeScreen(T screen);
    void unsubscribeScreen(T screen);
    void useCurrentReader(GetScreenCallback<T> callback);
    void closeScreen();
    void forceCloseScreen(ForceCloseReaderCallback callback);
}
