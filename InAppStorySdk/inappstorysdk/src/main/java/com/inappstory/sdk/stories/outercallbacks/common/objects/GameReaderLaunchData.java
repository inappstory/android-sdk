package com.inappstory.sdk.stories.outercallbacks.common.objects;


import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;

import java.util.List;

public class GameReaderLaunchData implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "gameReaderLaunchData";

    public GameReaderLaunchData(
            String gameId,
            String observableUID,
            String gameUrl,
            String splashImagePath,
            String gameConfig,
            List<WebResource> gameResources,
            GameScreenOptions options,
            SlideData slideData
    ) {
        this.gameId = gameId;
        this.observableUID = observableUID;
        this.gameUrl = gameUrl;
        this.splashImagePath = splashImagePath;
        this.gameConfig = gameConfig;
        this.gameResources = gameResources;
        this.options = options;
        this.slideData = slideData;
    }

    public String getGameId() {
        return gameId;
    }

    public String getObservableUID() {
        return observableUID;
    }

    public List<WebResource> getGameResources() {
        return gameResources;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public SlideData getSlideData() {
        return slideData;
    }

    public String getSplashImagePath() {
        return splashImagePath;
    }

    public String getGameConfig() {
        return gameConfig;
    }

    public GameScreenOptions getOptions() {
        return options;
    }

    private String gameId;
    private String observableUID;
    private List<WebResource> gameResources;
    private String gameUrl;
    private String splashImagePath;
    private String gameConfig;
    private GameScreenOptions options;
    private SlideData slideData;

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}
