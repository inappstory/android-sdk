package com.inappstory.sdk.games.domain.reader;

import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideState;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;

public class GameReaderState {
    private GameReaderLoadState loadState = GameReaderLoadState.IDLE;
    private GameReaderUIState uiState = GameReaderUIState.CLOSED;
    private GameReaderAppearanceSettings appearanceSettings;
    private GameReaderLaunchData gameReaderLaunchData;
    private GameLoadStatusState gameLoadStatusState = new GameLoadStatusState();

    public GameReaderLoadState loadState() {
        return loadState;
    }

    public GameReaderState loadState(GameReaderLoadState loadState) {
        this.loadState = loadState;
        return this;
    }

    public GameReaderUIState uiState() {
        return uiState;
    }

    public GameReaderState uiState(GameReaderUIState uiState) {
        this.uiState = uiState;
        return this;
    }

    public GameReaderAppearanceSettings appearanceSettings() {
        return appearanceSettings;
    }

    public GameReaderState appearanceSettings(GameReaderAppearanceSettings appearanceSettings) {
        this.appearanceSettings = appearanceSettings;
        return this;
    }

    public GameReaderLaunchData gameReaderLaunchData() {
        return gameReaderLaunchData;
    }

    public GameReaderState gameReaderLaunchData(GameReaderLaunchData gameReaderLaunchData) {
        this.gameReaderLaunchData = gameReaderLaunchData;
        return this;
    }

    public GameLoadStatusState gameLoadStatusState() {
        return gameLoadStatusState;
    }

    public GameReaderState gameLoadStatusState(GameLoadStatusState gameLoadStatusState) {
        this.gameLoadStatusState = gameLoadStatusState;
        return this;
    }

    public GameReaderState copy() {
        return new GameReaderState()
                .gameReaderLaunchData(this.gameReaderLaunchData)
                .appearanceSettings(this.appearanceSettings)
                .gameLoadStatusState(this.gameLoadStatusState)
                .uiState(this.uiState)
                .loadState(this.loadState);
    }
}
