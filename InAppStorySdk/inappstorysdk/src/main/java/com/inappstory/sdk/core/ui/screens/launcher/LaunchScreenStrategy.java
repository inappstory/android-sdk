package com.inappstory.sdk.core.ui.screens.launcher;

import android.content.Context;

import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.holder.ScreensHolder;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public interface LaunchScreenStrategy {
    void launch(
            Context context,
            IOpenReader openReader,
            IScreensHolder screensHolders
    );

    ScreenType getType();
}
