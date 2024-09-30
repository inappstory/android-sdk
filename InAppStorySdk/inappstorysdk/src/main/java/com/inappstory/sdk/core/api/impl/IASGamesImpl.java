package com.inappstory.sdk.core.api.impl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;

public class IASGamesImpl implements IASGames {
    private final IASCore core;

    public IASGamesImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void close() {
        core.screensManager().getGameScreenHolder().closeScreen();
    }

    @Override
    public void open(@NonNull Context context, String gameId) {

    }

    @Override
    public void callback(GameReaderCallback gameReaderCallback) {
        CallbackManager.getInstance().setGameReaderCallback(gameReaderCallback);
    }

    @Override
    public void preloadGames() {

    }
}
