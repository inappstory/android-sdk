package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class GameConfigOptions {
    public String sessionId;
    public String lang;
    public String apiKey;
    public String appPackageId;
    public String deviceId;

    public String userId;
    public String sdkVersion;
    public String apiBaseUrl;
    public String userAgent;
    public ArrayList<GameDataPlaceholder> placeholders;
    public String screenOrientation;
    public boolean fullScreen;
    public SafeAreaInsets safeAreaInsets;
    @SerializedName("variables")
    public Map<String, Object> userExtraOptions;

    public String gameInstanceId;
}