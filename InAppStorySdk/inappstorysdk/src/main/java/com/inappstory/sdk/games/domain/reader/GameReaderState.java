package com.inappstory.sdk.games.domain.reader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;

public class GameReaderState {
    public GameReaderLoadState loadState = GameReaderLoadState.IDLE;
    public GameReaderUIState uiState = GameReaderUIState.CLOSED;
    public GameReaderAppearanceSettings appearanceSettings;
    public GameReaderLaunchData gameReaderLaunchData;

}
