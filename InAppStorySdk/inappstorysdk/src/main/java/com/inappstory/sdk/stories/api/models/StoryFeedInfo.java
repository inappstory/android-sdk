package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StoryFeedInfo {

    @SerializedName("pin_position")
    public int pinPosition;

    @SerializedName("pin_position_ignore_mode")
    public int pinPositionIgnoreMode;

    @SerializedName("priority")
    public int priority;
}
