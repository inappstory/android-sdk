package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;


import android.graphics.Color;

public class StoryTimelineParameters {
    public StoryTimelineParameters(float gapWidth, float lineHeight, float lineRadius) {
        this.gapWidth = gapWidth;
        this.lineHeight = lineHeight;
        this.lineRadius = lineRadius;
    }

    float gapWidth;
    float lineHeight;
    float lineRadius;
    int fillColor = Color.parseColor("#ffffffff");
    int backgroundColor = Color.parseColor("#8affffff");
}
