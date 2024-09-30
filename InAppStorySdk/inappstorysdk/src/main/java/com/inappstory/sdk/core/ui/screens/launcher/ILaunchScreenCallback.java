package com.inappstory.sdk.core.ui.screens.launcher;

import com.inappstory.sdk.core.ui.screens.ScreenType;

public interface ILaunchScreenCallback {
    void onSuccess(ScreenType type);

    void onError(ScreenType type, String message);
}
