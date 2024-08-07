package com.inappstory.sdk.core.ui.screens.gamereader;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.ui.screens.IScreenHolder;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategy;
import com.inappstory.sdk.core.ui.screens.LaunchScreenStrategyType;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenReader;

public class LaunchGameScreenStrategy implements LaunchScreenStrategy {
    final GameStoryData data;
    final String gameId;
    final String observableId;
    final boolean openedFromStoriesReader;

    public LaunchGameScreenStrategy(
            GameStoryData data,
            String gameId,
            String observableId,
            boolean openedFromStoriesReader
    ) {
        this.data = data;
        this.gameId = gameId;
        this.observableId = observableId;
        this.openedFromStoriesReader = openedFromStoriesReader;
    }

    @Override
    public void launch(final Context context, final IOpenReader openReader, final IScreenHolder screenHolder) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getSession().getSessionId().isEmpty()) return;
        if (screenHolder.getScreen() != null) {
            screenHolder.closeScreen();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launch(
                            context,
                            openReader,
                            screenHolder
                    );
                }
            }, 500);
            return;
        }
        if (CallbackManager.getInstance().getGameReaderCallback() != null) {
            CallbackManager.getInstance().getGameReaderCallback().startGame(
                    data, gameId
            );
        }
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        GameReaderLaunchData gameReaderLaunchData = new GameReaderLaunchData(
                gameId,
                observableId,
                data != null ? data.slideData : null
        );
        Bundle bundle = new Bundle();
        GameReaderAppearanceSettings gameReaderAppearanceSettings = new GameReaderAppearanceSettings(
                openedFromStoriesReader ? "#000000" : null,
                openedFromStoriesReader ? "#000000" : null
        );
        bundle.putSerializable(gameReaderLaunchData.getSerializableKey(), gameReaderLaunchData);
        bundle.putSerializable(gameReaderAppearanceSettings.getSerializableKey(), gameReaderAppearanceSettings);
        openReader.onOpen(
                context,
                bundle
        );
    }

    @Override
    public LaunchScreenStrategyType getType() {
        return LaunchScreenStrategyType.GAME;
    }
}
