package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.core.data.ISlideTimeline;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class StorySlideTimeline implements ISlideTimeline {
    @SerializedName(ModelFields.StorySlideTimeline.foregroundColor)
    public String foregroundColor;
    @SerializedName(ModelFields.StorySlideTimeline.backgroundColor)
    public String backgroundColor;
    @SerializedName(ModelFields.StorySlideTimeline.isTimelineHidden)
    public boolean isTimelineHidden;

    public static String DEFAULT_TIMELINE_BACKGROUND_COLOR = "#ffffff8a";
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

    @Override
    public boolean isTimelineHidden() {
        return isTimelineHidden;
    }
}
