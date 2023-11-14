package com.inappstory.sdk.core.models.api;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

public class GameScreenOptions {
    @SerializedName("screenOrientation")
    public String screenOrientation;
    @SerializedName("fullScreen")
    public boolean fullScreen;
}