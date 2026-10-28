package com.inappstory.sdk.core.ui.screens;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.ui.screens.gamereader.GameScreenHolder;
import com.inappstory.sdk.core.ui.screens.holder.IScreenHolder;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.holder.ScreensHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenHolder;
import com.inappstory.sdk.core.ui.screens.launcher.IScreensLauncher;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.launcher.ScreensLauncher;
import com.inappstory.sdk.core.ui.screens.outsideapi.CloseUgcReaderCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.domain.IScreenViewModelsHolder;
import com.inappstory.sdk.domain.ScreenViewModelsHolder;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderState;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderUIStates;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderViewModel;
import com.inappstory.sdk.inappmessage.domain.reader.IIAMReaderViewModel;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.Sizes;

public class ScreensManager implements IScreensLauncher,
        IScreensHolder,
        IScreenViewModelsHolder,
        IASBackPressHandler {
    private final IScreensHolder screensHolder;
    private final IASCore core;
    private final ScreensLauncher launcher;
    private final IScreenViewModelsHolder screenViewModelsHolder;

    public ScreensManager(IASCore core) {
        this.core = core;
        this.screenViewModelsHolder = new ScreenViewModelsHolder(core);
        this.screensHolder = new ScreensHolder(core);
        this.launcher = new ScreensLauncher(screensHolder);
    }

    public boolean isTablet() {
        return Sizes.isTablet(core.appContext());
    }

    public boolean isPhone() {
        return !isTablet();
    }

    @Override
    public void setUgcCloseCallback(CloseUgcReaderCallback ugcCloseCallback) {
        this.screensHolder.setUgcCloseCallback(ugcCloseCallback);
    }

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

    @Override
    public IIAMReaderViewModel iamReaderViewModel() {
        return screenViewModelsHolder.iamReaderViewModel();
    }

    @Override
    public IIAMReaderViewModel iamReaderViewModel(int id) {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        IIAMReaderViewModel iamReaderViewModel = iamReaderViewModel();
        IAMReaderState state = iamReaderViewModel.getCurrentState();
        if (!state.uiState.equals(IAMReaderUIStates.CLOSED)) {
            if (state.canBeClosed)
                getIAMScreenHolder().closeScreen();
            return true;
        }
        return false;
    }
}
