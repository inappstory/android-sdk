package com.inappstory.sdk.core.ui.screens;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.core.ui.screens.gamereader.GameScreenHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenHolder;
import com.inappstory.sdk.core.ui.screens.outsideapi.CloseUgcReaderCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public class ScreensHolder implements IScreensHolder {
    GameScreenHolder gameScreenHolder = new GameScreenHolder();
    StoryScreenHolder storyScreenHolder = new StoryScreenHolder();
    IAMScreenHolder IAMScreenHolder = new IAMScreenHolder();

    public void setUgcCloseCallback(CloseUgcReaderCallback ugcCloseCallback) {
        this.ugcCloseCallback = ugcCloseCallback;
    }

    private CloseUgcReaderCallback ugcCloseCallback;

    private void closeUGCEditor() {
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
}
