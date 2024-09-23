package com.inappstory.sdk.core.ui.screens.storyreader;

import android.content.Context;
import android.os.Bundle;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.ui.screens.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategyType;
import com.inappstory.sdk.core.ui.screens.ScreensHolder;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

import java.util.ArrayList;
import java.util.List;


public class LaunchStoryScreenStrategy implements LaunchScreenStrategy {
    private LaunchStoryScreenData launchStoryScreenData;
    private LaunchStoryScreenAppearance readerAppearanceSettings;
    private List<ILaunchScreenCallback> launchScreenCallbacks = new ArrayList<>();
    private final boolean openedFromReader;

    public LaunchStoryScreenStrategy(boolean openedFromReader) {
        this.openedFromReader = openedFromReader;
    }

    public LaunchStoryScreenStrategy readerAppearanceSettings(LaunchStoryScreenAppearance readerAppearanceSettings) {
        this.readerAppearanceSettings = readerAppearanceSettings;
        return this;
    }

    public LaunchStoryScreenStrategy launchStoryScreenData(LaunchStoryScreenData launchStoryScreenData) {
        this.launchStoryScreenData = launchStoryScreenData;
        return this;
    }

    public LaunchStoryScreenStrategy addLaunchScreenCallback(ILaunchScreenCallback callback) {
        this.launchScreenCallbacks.add(callback);
        return this;
    }

    @Override
    public void launch(
            Context context,
            IOpenReader openReader,
            ScreensHolder screensHolder
    ) {
        StoryScreenHolder currentScreenHolder = screensHolder.getStoryScreenHolder();
        boolean cantBeOpened = false;
        String message = "";
        if (screensHolder.hasActiveScreen(currentScreenHolder)) {
            cantBeOpened = !openedFromReader;
        }
        if (currentScreenHolder.isOpened(launchStoryScreenData)) {
            cantBeOpened = true;
        }
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getSession().getSessionId().isEmpty()) {
            cantBeOpened = true;
        }
        if (cantBeOpened) {
            for (ILaunchScreenCallback callback : launchScreenCallbacks) {
                if (callback != null) callback.onError(getType(), message);
            }
            return;
        }
        if (openedFromReader) {
            currentScreenHolder.closeScreen();
        }
        for (ILaunchScreenCallback callback : launchScreenCallbacks) {
            if (callback != null) callback.onSuccess(getType());
        }
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
