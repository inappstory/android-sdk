package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StoryTimelineSettings {
    @SerializedName("foreground")
    public String foregroundColor;
    @SerializedName("background")
    public String backgroundColor;
}
