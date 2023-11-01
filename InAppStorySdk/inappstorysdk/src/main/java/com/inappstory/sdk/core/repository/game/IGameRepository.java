package com.inappstory.sdk.core.repository.game;

import android.content.Context;

import com.inappstory.sdk.game.reader.GameStoryData;

public interface IGameRepository {
    void openGameReaderWithGC(
            Context context,
            GameStoryData data,
            String gameId
    );
}
