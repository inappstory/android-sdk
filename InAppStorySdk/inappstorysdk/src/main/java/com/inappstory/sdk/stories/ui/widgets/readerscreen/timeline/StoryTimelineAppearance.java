package com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline;

import android.content.Context;

import com.inappstory.sdk.stories.utils.Sizes;

public class StoryTimelineAppearance {
    public StoryTimelineAppearance height(float height) {
        this.height = height;
        return this;
    }

    public StoryTimelineAppearance gapWidth(float gapWidth) {
        this.gapWidth = gapWidth;
        return this;
    }

    public StoryTimelineAppearance cornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        return this;
    }

    public StoryTimelineAppearance backgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public StoryTimelineAppearance fillColor(int fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    StoryTimelineAppearance convertDpToPx(Context context) {
        height = Sizes.dpFloatToPxExt(height, context);
        gapWidth = Sizes.dpFloatToPxExt(gapWidth, context);
        cornerRadius = Sizes.dpFloatToPxExt(cornerRadius, context);
        return this;
    }
    float height = 3f;
    float gapWidth = 4f;
    float cornerRadius = 1.5f;
    int backgroundColor = 0x80FFFFFF;
    int fillColor = 0xFFFFFFFF;
}
