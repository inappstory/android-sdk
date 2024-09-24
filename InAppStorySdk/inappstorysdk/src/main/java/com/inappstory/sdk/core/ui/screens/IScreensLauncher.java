package com.inappstory.sdk.core.ui.screens;

import android.content.Context;

import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public interface IScreensLauncher {
    void openScreen(Context context, LaunchScreenStrategy strategy);
    IOpenReader getOpenReader(ScreenType type);
}
