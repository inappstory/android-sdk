package com.inappstory.sdk.core.ui.screens.gamereader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;

public class LaunchGameScreenData {
    public LaunchGameScreenData(
            String observableId,
            ContentData gameLaunchSourceData,
            String gameId
    ) {
        this.launchData = new GameReaderLaunchData(
                gameId,
                observableId,
                gameLaunchSourceData
        );
        this.gameLaunchSourceData = gameLaunchSourceData;
        this.gameId = gameId;
    }


    GameReaderLaunchData launchData;
    ContentData gameLaunchSourceData;
    String gameId;
}
