package com.inappstory.sdk.core.ui.screens.gamereader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public class LaunchGameScreenStrategy implements LaunchScreenStrategy {
    public LaunchGameScreenStrategy data(LaunchGameScreenData data) {
        this.data = data;
        return this;
    }

    public LaunchGameScreenStrategy(IASCore core, boolean openedFromReader) {
        this.core = core;
        this.openedFromReader = openedFromReader;
    }

    private final boolean openedFromReader;
    private final IASCore core;

    private LaunchGameScreenData data;

    @Override
    public void launch(Context context, IOpenReader openReader, IScreensHolder screensHolder) {
        if (!(openReader instanceof IOpenGameReader)) return;
        if (core.sessionManager().getSession().getSessionId().isEmpty()) return;
        if ((!openedFromReader && screensHolder.hasActiveScreen()) ||
                screensHolder.hasActiveScreen(screensHolder.getGameScreenHolder())
        ) {
            return;
        }
        Bundle bundle = new Bundle();
        GameReaderAppearanceSettings gameReaderAppearanceSettings = new GameReaderAppearanceSettings(
                openedFromReader ? "#000000" : null,
                openedFromReader ? "#000000" : null
        );
        bundle.putSerializable(data.launchData.getSerializableKey(), data.launchData);
        bundle.putSerializable(gameReaderAppearanceSettings.getSerializableKey(), gameReaderAppearanceSettings);
        ((IOpenGameReader) openReader).onOpen(
                context,
                bundle
        );
        core.callbacksAPI().useCallback(
                IASCallbackType.GAME_READER,
                new UseIASCallback<GameReaderCallback>() {
                    @Override
                    public void use(@NonNull GameReaderCallback callback) {
                        callback.startGame(
                                data.gameStoryData, data.gameId
                        );
                    }
                }
        );
    }

    @Override
    public ScreenType getType() {
        return ScreenType.GAME;
    }
}
