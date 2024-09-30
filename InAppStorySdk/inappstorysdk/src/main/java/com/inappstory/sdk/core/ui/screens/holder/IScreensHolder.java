package com.inappstory.sdk.core.ui.screens.holder;

import com.inappstory.sdk.core.ui.screens.gamereader.GameScreenHolder;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.IAMScreenHolder;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryScreenHolder;

public interface IScreensHolder {
    GameScreenHolder getGameScreenHolder();
    StoryScreenHolder getStoryScreenHolder();
    IAMScreenHolder getIAMScreenHolder();
    void launchScreenActions();
    boolean hasActiveScreen();
    boolean hasActiveScreen(IScreenHolder holder);
}
