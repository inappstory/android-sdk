package com.inappstory.sdk.core.repository.game;

import android.content.Context;


import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.ui.ScreensManager;

public class GameRepository implements IGameRepository {

    @Override
    public void openGameReaderWithGC(Context context, GameStoryData data, String gameId) {
        ScreensManager.getInstance().openGameReader(
                context,
                data,
                gameId,
                null
        );
    }

    public void clearGames() {
        gameCacheManager().clearGames();
    }

    private GameCacheManager gameCacheManager = new GameCacheManager();

    public GameCacheManager gameCacheManager() {
        if (gameCacheManager == null) {
            gameCacheManager = new GameCacheManager();
        }
        return gameCacheManager;
    }


}
