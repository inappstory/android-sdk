package com.inappstory.sdk.externalapi.games;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;

public class IASGamesExternalAPIImpl implements IASGames {
    public void close() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.gamesAPI().close();
            }
        });
    }

    public void open(@NonNull final Context context, final String gameId) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.openGame(gameId, context);
            }
        });
    }

    public void callback(final GameReaderCallback gameReaderCallback) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.gamesAPI().callback(gameReaderCallback);
            }
        });
    }

    @Override
    public void preloadGames() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.contentPreload().restartGamePreloader();
            }
        });
    }
}
