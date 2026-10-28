package com.inappstory.sdk.stories.outercallbacks.common.gamereader;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;

public interface GameReaderCallback extends IASCallback {
    void startGame(
            ContentData gameLaunchSourceData,
            String gameId
    );

    void closeGame(
            ContentData gameLaunchSourceData,
            String gameId
    );

    void eventGame(
            ContentData gameLaunchSourceData,
            String gameId,
            String eventName,
            String payload
    );

    void gameLoadError(
            ContentData gameLaunchSourceData,
            String gameId
    );

    void gameOpenError(
            ContentData gameLaunchSourceData,
            String gameId
    );
}
