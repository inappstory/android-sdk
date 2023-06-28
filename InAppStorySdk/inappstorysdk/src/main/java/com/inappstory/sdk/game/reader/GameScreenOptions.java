package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.SerializedName;

public class GameScreenOptions {
    @SerializedName("screenOrientation")
    public String screenOrientation;
    @SerializedName("fullScreen")
    public boolean fullScreen;
}