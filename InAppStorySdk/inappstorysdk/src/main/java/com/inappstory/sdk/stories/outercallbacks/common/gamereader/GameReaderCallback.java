package com.inappstory.sdk.stories.outercallbacks.common.gamereader;

import com.inappstory.sdk.game.reader.GameStoryData;

public interface GameReaderCallback {
    void startGame(
            GameStoryData data,
            String gameId
    );

    void finishGame(
            GameStoryData data,
            String result,
            String gameId
    );

    void closeGame(
            GameStoryData data,
            String gameId
    );

    void eventGame(
            GameStoryData data,
            String gameId,
            String eventName,
            String payload
    );

    void gameLoadError(
            GameStoryData data,
            String gameId
    );

    void gameOpenError(
            GameStoryData data,
            String gameId
    );
}
