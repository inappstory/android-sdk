package com.inappstory.sdk.core.ui.screens;

import android.content.Context;

public interface IScreensLauncher {
    void openScreen(Context context, LaunchScreenStrategy strategy);
}
