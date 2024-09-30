package com.inappstory.sdk.core.ui.screens.gamereader;

import android.content.Context;
import android.os.Bundle;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.ui.screens.holder.IScreensHolder;
import com.inappstory.sdk.core.ui.screens.launcher.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.holder.ScreensHolder;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public class LaunchGameScreenStrategy implements LaunchScreenStrategy {
    public LaunchGameScreenStrategy data(LaunchGameScreenData data) {
        this.data = data;
        return this;
    }

    public LaunchGameScreenStrategy(boolean openedFromReader) {
        this.openedFromReader = openedFromReader;
    }

    private final boolean openedFromReader;

    private LaunchGameScreenData data;

    @Override
    public void launch(Context context, IOpenReader openReader, IScreensHolder screensHolder) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getSession().getSessionId().isEmpty()) return;
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
        openReader.onOpen(
                context,
                bundle
        );
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().startGame(
                    data.gameStoryData, data.gameId
            );
        }
    }

    @Override
    public ScreenType getType() {
        return ScreenType.GAME;
    }
}
