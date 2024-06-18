package com.inappstory.sdk.externalapi.games;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.ui.ScreensManager;

public class IASGames {
    public void close() {
        ScreensManager.getInstance().closeGameReader();
    }

    public void open(@NonNull final Context context, final String gameId) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.openGame(gameId, context);
            }
        });
    }

    public void callback(GameReaderCallback gameReaderCallback) {
        CallbackManager.getInstance().setGameReaderCallback(gameReaderCallback);
    }
}
