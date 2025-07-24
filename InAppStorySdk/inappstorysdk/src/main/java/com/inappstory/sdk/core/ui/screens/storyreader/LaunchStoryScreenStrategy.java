package com.inappstory.sdk.core.ui.screens.storyreader;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.holder.ScreensHolder;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;

import java.util.ArrayList;
import java.util.List;


public class LaunchStoryScreenStrategy implements LaunchScreenStrategy {
    private final IASCore core;
    private LaunchStoryScreenData launchStoryScreenData;
    private LaunchStoryScreenAppearance readerAppearanceSettings;
    private List<ILaunchScreenCallback> launchScreenCallbacks = new ArrayList<>();
    private final boolean openedFromReader;

    public LaunchStoryScreenStrategy(IASCore core, boolean openedFromReader) {
        this.openedFromReader = openedFromReader;
        this.core = core;
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
            final Context context,
            final IOpenReader openReader,
            final IScreensHolder screensHolder
    ) {
        StoryScreenHolder currentScreenHolder = screensHolder.getStoryScreenHolder();
        boolean cantBeOpened = false;
        if (!(openReader instanceof IOpenStoriesReader)) return;
        String message = "Story reader can't be opened. Please, close another opened reader first.";

        if (screensHolder.hasActiveScreen(currentScreenHolder)) {
            cantBeOpened = !openedFromReader;
        }
        if (currentScreenHolder.isOpened(launchStoryScreenData)) {
            cantBeOpened = true;
        }
        if (launchStoryScreenData != null) {
            if (launchStoryScreenData.getStoriesIds().isEmpty()) {
                cantBeOpened = true;
                message = "No story from list can be opened in reader.";
            } else if (launchStoryScreenData.getStoriesIds().size() <= launchStoryScreenData.getListIndex()) {
                cantBeOpened = true;
                message = "Can't open reader from this story index";
            }
        }
        if (core.sessionManager().getSession().getSessionId().isEmpty()) {
            cantBeOpened = true;
            message = "User not authorized";
        }
        if (cantBeOpened) {
            for (ILaunchScreenCallback callback : launchScreenCallbacks) {
                if (callback != null) callback.onError(getType(), message);
            }
            return;
        }

        Runnable openNewScreenInstance = new Runnable() {
            @Override
            public void run() {
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
                ((IOpenStoriesReader) openReader).onOpen(
                        context,
                        bundle
                );
            }
        };
        if (openedFromReader) {
            if (currentScreenHolder.isOpened()) {
                currentScreenHolder.closeScreen();
                new Handler(Looper.getMainLooper()).postDelayed(openNewScreenInstance, 500);
                return;
            }
        }
        openNewScreenInstance.run();
    }


    @Override
    public ScreenType getType() {
        return ScreenType.STORY;
    }
}
