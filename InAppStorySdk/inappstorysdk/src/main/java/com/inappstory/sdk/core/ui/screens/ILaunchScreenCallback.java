package com.inappstory.sdk.core.ui.screens;

public interface ILaunchScreenCallback {
    void onSuccess(ScreenType type);

    void onError(ScreenType type, String message);
}
