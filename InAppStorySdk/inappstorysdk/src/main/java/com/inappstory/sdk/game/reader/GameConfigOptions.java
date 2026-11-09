package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.ArrayList;
import java.util.Map;

public class GameConfigOptions {
    @SerializedName(ModelFields.GameConfigOptions.sessionId)
    public String sessionId;
    @SerializedName(ModelFields.GameConfigOptions.lang)
    public String lang;
    @SerializedName(ModelFields.GameConfigOptions.apiKey)
    public String apiKey;
    @SerializedName(ModelFields.GameConfigOptions.appPackageId)
    public String appPackageId;
    @SerializedName(ModelFields.GameConfigOptions.deviceId)
    public String deviceId;
    @SerializedName(ModelFields.GameConfigOptions.userId)
    public String userId;
    @SerializedName(ModelFields.GameConfigOptions.sdkVersion)
    public String sdkVersion;
    @SerializedName(ModelFields.GameConfigOptions.apiBaseUrl)
    public String apiBaseUrl;
    @SerializedName(ModelFields.GameConfigOptions.userAgent)
    public String userAgent;
    @SerializedName(ModelFields.GameConfigOptions.placeholders)
    public ArrayList<GameDataPlaceholder> placeholders;
    @SerializedName(ModelFields.GameConfigOptions.screenOrientation)
    public String screenOrientation;
    @SerializedName(ModelFields.GameConfigOptions.fullScreen)
    public boolean fullScreen;
    @SerializedName(ModelFields.GameConfigOptions.safeAreaInsets)
    public SafeAreaInsets safeAreaInsets;
    @SerializedName(ModelFields.GameConfigOptions.userExtraOptions)
    public Map<String, Object> userExtraOptions;
    @SerializedName(ModelFields.GameConfigOptions.gameInstanceId)
    public String gameInstanceId;
}