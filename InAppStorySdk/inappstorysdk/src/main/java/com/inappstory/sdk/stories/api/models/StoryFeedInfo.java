package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StoryFeedInfo {

    @SerializedName("pin_position")
    public int pinPosition;

    @SerializedName("unpin_mode")
    public int unpinMode;

    @SerializedName("priority")
    public int priority;
}
