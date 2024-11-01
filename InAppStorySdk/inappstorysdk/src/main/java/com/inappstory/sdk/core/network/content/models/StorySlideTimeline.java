package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.core.data.ISlideTimeline;
import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StorySlideTimeline implements ISlideTimeline {
    @SerializedName("foreground")
    public String foregroundColor;
    @SerializedName("background")
    public String backgroundColor;

    public static String DEFAULT_TIMELINE_BACKGROUND_COLOR = "#8affffff";
    public static String DEFAULT_TIMELINE_FOREGROUND_COLOR = "#ffffffff";

    @Override
    public String timelineForegroundColor() {
        return foregroundColor != null ?
                foregroundColor :
                DEFAULT_TIMELINE_FOREGROUND_COLOR;
    }

    @Override
    public String timelineBackgroundColor() {
        return backgroundColor != null ?
                backgroundColor :
                DEFAULT_TIMELINE_BACKGROUND_COLOR;
    }
}
