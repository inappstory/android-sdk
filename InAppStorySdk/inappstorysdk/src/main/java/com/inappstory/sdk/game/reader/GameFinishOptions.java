package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.network.SerializedName;

public class GameFinishOptions {
    @SerializedName("openUrl")
    public String openUrl;
    @SerializedName("openStory")
    public GameFinishStoryOptions openStory;
}