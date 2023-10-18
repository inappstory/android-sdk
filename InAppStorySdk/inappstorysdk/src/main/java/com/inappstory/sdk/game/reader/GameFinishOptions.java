package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

public class GameFinishOptions {
    @SerializedName("openUrl")
    public String openUrl;
    @SerializedName("openStory")
    public GameFinishStoryOptions openStory;
}