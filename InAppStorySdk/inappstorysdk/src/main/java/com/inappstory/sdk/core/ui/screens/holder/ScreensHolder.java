package com.inappstory.sdk.core.ui.screens.holder;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.exceptions.NotImplementedMethodException;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.gamereader.GameScreenHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenHolder;
import com.inappstory.sdk.core.ui.screens.outsideapi.CloseUgcReaderCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public class ScreensHolder implements IScreensHolder {
    public ScreensHolder(IASCore core) {
        this.storyScreenHolder = new StoryScreenHolder(core, shareProcessHandler);
        this.gameScreenHolder = new GameScreenHolder(core, shareProcessHandler);
        this.IAMScreenHolder = new IAMScreenHolder();
    }

    public ShareProcessHandler getShareProcessHandler() {
        return shareProcessHandler;
    }


    private final ShareProcessHandler shareProcessHandler = new ShareProcessHandler();
    private final GameScreenHolder gameScreenHolder;
    private final StoryScreenHolder storyScreenHolder;
    private final IAMScreenHolder IAMScreenHolder;

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
        throw new NotImplementedMethodException();
        //this.ugcCloseCallback = ugcCloseCallback;
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
