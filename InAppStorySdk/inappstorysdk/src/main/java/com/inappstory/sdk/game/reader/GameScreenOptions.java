package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.io.Serializable;

public class GameScreenOptions implements Serializable {
    @SerializedName("screenOrientation")
    public String screenOrientation;
    @SerializedName("fullScreen")
    public boolean fullScreen;
}