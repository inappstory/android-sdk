package com.inappstory.sdk.core.ui.screens.storyreader;

import android.content.Context;
import android.os.Bundle;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.ui.screens.IScreenHolder;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategyType;
import com.inappstory.sdk.core.ui.screens.ScreensHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;

import java.util.List;


public class LaunchStoryScreenStrategy implements LaunchScreenStrategy {
    private LaunchStoryScreenData launchStoryScreenData;
    private LaunchStoryScreenAppearance readerAppearanceSettings;

    public LaunchStoryScreenStrategy readerAppearanceSettings(LaunchStoryScreenAppearance readerAppearanceSettings) {
        this.readerAppearanceSettings = readerAppearanceSettings;
        return this;
    }

    public LaunchStoryScreenStrategy launchStoryScreenData(LaunchStoryScreenData launchStoryScreenData) {
        this.launchStoryScreenData = launchStoryScreenData;
        return this;
    }

    @Override
    public void launch(
            Context context,
            IOpenReader openReader,
            ScreensHolder screensHolder
    ) {
        StoryScreenHolder currentScreenHolder = screensHolder.getStoryScreenHolder();
        IScreenHolder gameScreenHolder = screensHolder.getGameScreenHolder();
        IScreenHolder iamScreenHolder = screensHolder.getIAMScreenHolder();

        if (
                currentScreenHolder.isLaunchProcessStarted() ||
                        gameScreenHolder.isLaunchProcessStarted() ||
                        iamScreenHolder.isLaunchProcessStarted()
        )
            return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getSession().getSessionId().isEmpty()) return;
        if (currentScreenHolder.isOpened(launchStoryScreenData)) return;

        currentScreenHolder.forceCloseScreen(null);
        gameScreenHolder.forceCloseScreen(null);
        iamScreenHolder.forceCloseScreen(null);
        Bundle bundle = new Bundle();
        bundle.putSerializable(
                launchStoryScreenData.getSerializableKey(),
                launchStoryScreenData
        );
        bundle.putSerializable(
                readerAppearanceSettings.getSerializableKey(),
                readerAppearanceSettings
        );
        openReader.onOpen(
                context,
                bundle
        );
    }


    @Override
    public LaunchScreenStrategyType getType() {
        return LaunchScreenStrategyType.STORY;
    }
}
