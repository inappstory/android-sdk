package com.inappstory.sdk.core.ui.screens.launcher;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;

public interface IScreensLauncher {
    void openScreen(Context context, LaunchScreenStrategy strategy);
    IOpenReader getOpenReader(ScreenType type);
    void setOpenGameReader(@NonNull IOpenGameReader openGameReader);
    void setOpenStoriesReader(@NonNull IOpenStoriesReader openStoriesReader);
    void setOpenInAppMessageReader(@NonNull IOpenInAppMessageReader openInAppMessageReader);
}
