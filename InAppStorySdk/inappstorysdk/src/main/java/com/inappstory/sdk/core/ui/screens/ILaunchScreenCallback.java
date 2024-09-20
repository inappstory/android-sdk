package com.inappstory.sdk.core.ui.screens;

public interface ILaunchScreenCallback {
    void onSuccess(LaunchScreenStrategyType type);

    void onError(LaunchScreenStrategyType type, String message);
}
