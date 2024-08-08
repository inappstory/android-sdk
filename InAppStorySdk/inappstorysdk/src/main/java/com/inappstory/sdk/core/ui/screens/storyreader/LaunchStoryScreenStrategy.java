package com.inappstory.sdk.core.ui.screens.storyreader;

import android.content.Context;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.ui.screens.IScreenHolder;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategyType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;


public class LaunchStoryScreenStrategy implements LaunchScreenStrategy {
    LaunchStoryScreenData launchStoryScreenData;

    public LaunchStoryScreenStrategy(LaunchStoryScreenData launchStoryScreenData) {
        this.launchStoryScreenData = launchStoryScreenData;
    }

    @Override
    public void launch(Context context, IOpenReader openReader, IScreenHolder screenHolder) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getSession().getSessionId().isEmpty()) return;
        if (screenHolder.isOpened(launchStoryScreenData)) return;


    }

    @Override
    public LaunchScreenStrategyType getType() {
        return LaunchScreenStrategyType.STORY;
    }
}
