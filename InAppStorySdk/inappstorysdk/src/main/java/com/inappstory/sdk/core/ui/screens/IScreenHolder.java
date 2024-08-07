package com.inappstory.sdk.core.ui.screens;

import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public interface IScreenHolder<T> {
    boolean isOpened();
    T getScreen();
    void subscribeScreen(T screen);
    void unsubscribeScreen(T screen);
    void useCurrentReader(GetScreenCallback<T> callback);
    void closeScreen();
    void forceCloseScreen(ForceCloseReaderCallback callback);
}
