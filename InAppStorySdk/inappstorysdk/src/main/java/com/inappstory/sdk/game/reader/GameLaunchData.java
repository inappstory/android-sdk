package com.inappstory.sdk.game.reader;


import java.io.Serializable;

public class GameLaunchData implements Serializable {
    public GameLaunchData(
            String gameUrl,
            String splashImagePath,
            String gameConfig,
            String resources,
            String options
    ) {
        this.gameUrl = gameUrl;
        this.splashImagePath = splashImagePath;
        this.gameConfig = gameConfig;
        this.resources = resources;
        this.options = options;
    }

    private String gameUrl;
    private String splashImagePath;
    private String gameConfig;
    private String resources;
    private String options;

    public String getGameUrl() {
        return gameUrl;
    }

    public String getSplashImagePath() {
        return splashImagePath;
    }

    public String getGameConfig() {
        return gameConfig;
    }

    public String getResources() {
        return resources;
    }

    public String getOptions() {
        return options;
    }
}
