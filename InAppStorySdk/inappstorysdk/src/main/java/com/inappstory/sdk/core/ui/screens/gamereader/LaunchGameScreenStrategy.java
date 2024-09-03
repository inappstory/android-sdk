package com.inappstory.sdk.core.ui.screens.gamereader;

import android.content.Context;
import android.os.Bundle;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategyType;
import com.inappstory.sdk.core.ui.screens.ScreensHolder;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public class LaunchGameScreenStrategy implements LaunchScreenStrategy {
    public LaunchGameScreenStrategy data(LaunchGameScreenData data) {
        this.data = data;
        return this;
    }

    private LaunchGameScreenData data;

    @Override
    public void launch(Context context, IOpenReader openReader, ScreensHolder screensHolder) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getSession().getSessionId().isEmpty()) return;
        if ((!data.openedFromStoryReader && screensHolder.hasActiveScreen()) ||
                screensHolder.hasActiveScreen(screensHolder.getStoryScreenHolder())
        ) {
            return;
        }
        Bundle bundle = new Bundle();
        GameReaderAppearanceSettings gameReaderAppearanceSettings = new GameReaderAppearanceSettings(
                data.openedFromStoryReader ? "#000000" : null,
                data.openedFromStoryReader ? "#000000" : null
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
    public LaunchScreenStrategyType getType() {
        return LaunchScreenStrategyType.GAME;
    }
}
