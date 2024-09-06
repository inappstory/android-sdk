package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import android.content.Context;
import android.view.ViewGroup;

import com.inappstory.sdk.core.ui.screens.IScreenHolder;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategyType;
import com.inappstory.sdk.core.ui.screens.ScreensHolder;
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
    public void launch(Context context, IOpenReader openReader, ScreensHolder screensHolders) {

    }

    @Override
    public LaunchScreenStrategyType getType() {
        return LaunchScreenStrategyType.IN_APP_MESSAGE;
    }
}
