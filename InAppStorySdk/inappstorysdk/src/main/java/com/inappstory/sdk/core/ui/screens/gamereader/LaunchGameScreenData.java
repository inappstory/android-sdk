package com.inappstory.sdk.core.ui.screens.gamereader;

import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;

public class LaunchGameScreenData {
    public LaunchGameScreenData(
            String observableId,
            GameStoryData gameStoryData,
            String gameId
    ) {
        this.launchData = new GameReaderLaunchData(
                gameId,
                observableId,
                gameStoryData != null ? gameStoryData.slideData : null
        );
        this.gameStoryData = gameStoryData;
        this.gameId = gameId;
    }


    GameReaderLaunchData launchData;
    GameStoryData gameStoryData;
    String gameId;
}