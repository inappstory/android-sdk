package com.inappstory.sdk.stories.outercallbacks.common.gamereader;

import com.inappstory.sdk.game.reader.GameStoryData;

public interface GameReaderCallback {
    void startGame(GameStoryData data, String gameId);
    void finishGame(GameStoryData data, String gameId,
                    String result);
    void closeGame(GameStoryData data, String gameId);
}
