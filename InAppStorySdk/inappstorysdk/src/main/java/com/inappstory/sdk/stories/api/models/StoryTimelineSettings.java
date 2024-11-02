package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StoryTimelineSettings {
    @SerializedName("foreground")
    public String foregroundColor;
    @SerializedName("background")
    public String backgroundColor;

    public static final String DEFAULT_FG_COLOR = "#FFFFFFFF";
    public static final String DEFAULT_BG_COLOR = "#8aFFFFFF";
}
