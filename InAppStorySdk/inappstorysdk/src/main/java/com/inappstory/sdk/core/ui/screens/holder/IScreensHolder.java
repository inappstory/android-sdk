package com.inappstory.sdk.core.ui.screens.holder;

import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.gamereader.GameScreenHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenHolder;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public interface IScreensHolder {
    GameScreenHolder getGameScreenHolder();
    StoryScreenHolder getStoryScreenHolder();
    IAMScreenHolder getIAMScreenHolder();
    void launchScreenActions();
    boolean hasActiveScreen();
    boolean hasActiveScreen(IScreenHolder holder);
    ShareProcessHandler getShareProcessHandler();
    void forceCloseAllReaders(final ForceCloseReaderCallback callback);


}
