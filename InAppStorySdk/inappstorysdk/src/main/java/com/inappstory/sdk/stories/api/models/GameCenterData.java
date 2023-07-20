package com.inappstory.sdk.stories.api.models;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.network.Required;
import com.inappstory.sdk.network.SerializedName;

import java.util.List;
import java.util.Map;

public class GameCenterData {
    @SerializedName("id")
    public String id;
    @SerializedName("splashScreen")
    public GameSplashScreen splashScreen;
    @SerializedName("resources")
    public List<WebResource> resources;
    @SerializedName("options")
    public GameScreenOptions options;
    @Required
    @SerializedName("downloadUrl")
    public String url;
    @Required
    @SerializedName("initCode")
    public String initCode;

    @SerializedName("instanceUserData")
    public Map<String, Object> instanceUserData;
}
