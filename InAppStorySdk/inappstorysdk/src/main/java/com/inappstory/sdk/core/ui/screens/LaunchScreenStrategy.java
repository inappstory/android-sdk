package com.inappstory.sdk.core.ui.screens;

import android.content.Context;

import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

import java.util.List;

public interface LaunchScreenStrategy {
    void launch(
            Context context,
            IOpenReader openReader,
            ScreensHolder screensHolders
    );

    LaunchScreenStrategyType getType();
}
