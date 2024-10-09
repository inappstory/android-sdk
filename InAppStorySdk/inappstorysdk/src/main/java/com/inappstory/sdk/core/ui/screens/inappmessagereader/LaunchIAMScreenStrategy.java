package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import android.content.Context;
import android.view.ViewGroup;

import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.holder.ScreensHolder;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public class LaunchIAMScreenStrategy implements LaunchScreenStrategy {
    private ViewGroup parentContainer;
    private IAMScreenType iamScreenType = IAMScreenType.FULLSCREEN;

    public LaunchIAMScreenStrategy parentContainer(ViewGroup parentContainer) {
        this.parentContainer = parentContainer;
        return this;
    }

    public LaunchIAMScreenStrategy iamScreenType(IAMScreenType iamScreenType) {
        this.iamScreenType = iamScreenType;
        return this;
    }


    @Override
    public void launch(Context context, IOpenReader openReader, IScreensHolder screensHolders) {

    }

    @Override
    public ScreenType getType() {
        return ScreenType.IN_APP_MESSAGE;
    }
}
