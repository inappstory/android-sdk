package com.inappstory.sdk.core.ui.screens;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.ui.screens.gamereader.GameScreenHolder;
import com.inappstory.sdk.core.ui.screens.holder.IScreenHolder;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.holder.ScreensHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenHolder;
import com.inappstory.sdk.core.ui.screens.launcher.IScreensLauncher;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.launcher.ScreensLauncher;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public class ScreensManager implements IScreensLauncher, IScreensHolder {
    private final IScreensHolder screensHolder = new ScreensHolder();

    private final ScreensLauncher launcher = new ScreensLauncher(screensHolder);

    @Override
    public GameScreenHolder getGameScreenHolder() {
        return screensHolder.getGameScreenHolder();
    }

    @Override
    public StoryScreenHolder getStoryScreenHolder() {
        return screensHolder.getStoryScreenHolder();
    }

    @Override
    public IAMScreenHolder getIAMScreenHolder() {
        return screensHolder.getIAMScreenHolder();
    }

    @Override
    public void launchScreenActions() {
        screensHolder.launchScreenActions();
    }

    @Override
    public boolean hasActiveScreen() {
        return screensHolder.hasActiveScreen();
    }

    @Override
    public boolean hasActiveScreen(IScreenHolder holder) {
        return screensHolder.hasActiveScreen(holder);
    }

    @Override
    public ShareProcessHandler getShareProcessHandler() {
        return screensHolder.getShareProcessHandler();
    }

    @Override
    public void forceCloseAllReaders(ForceCloseReaderCallback callback) {
        screensHolder.forceCloseAllReaders(callback);
    }

    @Override
    public void openScreen(Context context, LaunchScreenStrategy strategy) {
        launcher.openScreen(context, strategy);
    }

    @Override
    public IOpenReader getOpenReader(ScreenType type) {
        return launcher.getOpenReader(type);
    }

    @Override
    public void setOpenGameReader(@NonNull IOpenGameReader openGameReader) {
        launcher.setOpenGameReader(openGameReader);
    }

    @Override
    public void setOpenStoriesReader(@NonNull IOpenStoriesReader openStoriesReader) {
        launcher.setOpenStoriesReader(openStoriesReader);
    }

    @Override
    public void setOpenInAppMessageReader(@NonNull IOpenInAppMessageReader openInAppMessageReader) {
        launcher.setOpenInAppMessageReader(openInAppMessageReader);
    }
}
