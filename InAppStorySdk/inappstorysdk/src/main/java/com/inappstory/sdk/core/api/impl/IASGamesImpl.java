package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.InAppStoryManager.IAS_ERROR_TAG;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenData;
import com.inappstory.sdk.core.ui.screens.gamereader.LaunchGameScreenStrategy;
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
    public void open(@NonNull Context context, final String gameId) {
        if (gameCanBeOpened(gameId))
            core.screensManager().openScreen(context,
                    new LaunchGameScreenStrategy(core, false)
                            .data(new LaunchGameScreenData(
                                    null,
                                    null,
                                    gameId
                            ))
            );
    }

    @Override
    public void callback(GameReaderCallback gameReaderCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.GAME_READER, gameReaderCallback);
    }

    @Override
    public void preloadGames() {

    }

    @Override
    public boolean gameCanBeOpened(final String gameId) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    IAS_ERROR_TAG,
                    "Games are unavailable for anonymous mode"
            );
            core.callbacksAPI().useCallback(
                    IASCallbackType.GAME_READER,
                    new UseIASCallback<GameReaderCallback>() {
                        @Override
                        public void use(@NonNull GameReaderCallback callback) {
                            callback.gameOpenError(null, gameId);
                        }
                    }
            );
            return false;
        }
        if (settingsHolder.noCorrectUserIdOrDevice()) {
            core.callbacksAPI().useCallback(
                    IASCallbackType.GAME_READER,
                    new UseIASCallback<GameReaderCallback>() {
                        @Override
                        public void use(@NonNull GameReaderCallback callback) {
                            callback.gameOpenError(null, gameId);
                        }
                    }
            );
            return false;
        }
        return true;
    }
}
