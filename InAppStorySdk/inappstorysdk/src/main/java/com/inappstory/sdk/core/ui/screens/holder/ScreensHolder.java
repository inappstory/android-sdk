package com.inappstory.sdk.core.ui.screens.holder;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.gamereader.GameScreenHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenHolder;
import com.inappstory.sdk.core.ui.screens.outsideapi.CloseUgcReaderCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public class ScreensHolder implements IScreensHolder {
    public ShareProcessHandler getShareProcessHandler() {
        return shareProcessHandler;
    }

    private final ShareProcessHandler shareProcessHandler = new ShareProcessHandler();
    GameScreenHolder gameScreenHolder = new GameScreenHolder(shareProcessHandler);
    StoryScreenHolder storyScreenHolder = new StoryScreenHolder(shareProcessHandler);
    IAMScreenHolder IAMScreenHolder = new IAMScreenHolder();

    public boolean hasActiveScreen(IScreenHolder activeHolder) {
        return checkHolderForScreen(storyScreenHolder, activeHolder) ||
                checkHolderForScreen(gameScreenHolder, activeHolder) ||
                checkHolderForScreen(IAMScreenHolder, activeHolder);
    }

    private boolean checkHolderForScreen(IScreenHolder holderToCheck, IScreenHolder holderToCompare) {
        return (holderToCheck == holderToCompare) && (holderToCheck.isOpened() || holderToCheck.isLaunchProcessStarted());
    }

    public boolean hasActiveScreen() {
        return storyScreenHolder.isOpened() || storyScreenHolder.isLaunchProcessStarted() ||
                gameScreenHolder.isOpened() || gameScreenHolder.isLaunchProcessStarted() ||
                IAMScreenHolder.isOpened() || IAMScreenHolder.isLaunchProcessStarted();
    }

    public void setUgcCloseCallback(CloseUgcReaderCallback ugcCloseCallback) {
        this.ugcCloseCallback = ugcCloseCallback;
    }

    private CloseUgcReaderCallback ugcCloseCallback;

    public void closeUGCEditor() {
        if (ugcCloseCallback != null) ugcCloseCallback.onClose();
    }


    public void forceCloseAllReaders(final ForceCloseReaderCallback callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                gameScreenHolder.forceCloseScreen(null);
                storyScreenHolder.forceCloseScreen(null);
                IAMScreenHolder.forceCloseScreen(null);
                callback.onComplete();
                closeUGCEditor();
            }
        });
    }

    @Override
    public GameScreenHolder getGameScreenHolder() {
        return gameScreenHolder;
    }

    @Override
    public StoryScreenHolder getStoryScreenHolder() {
        return storyScreenHolder;
    }

    @Override
    public IAMScreenHolder getIAMScreenHolder() {
        return IAMScreenHolder;
    }

    @Override
    public void launchScreenActions() {
        closeUGCEditor();
    }
}
