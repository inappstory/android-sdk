package com.inappstory.sdk.core.repository.game;

import android.content.Context;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.game.reader.GameStoryData;

public class GameRepository implements IGameRepository {

    @Override
    public void openGameReaderWithGC(Context context, GameStoryData data, String gameId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.openGameReaderWithGC(context, data, gameId);
    }
}
