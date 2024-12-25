package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;


import android.graphics.Color;

public class StoryTimelineParameters {
    public StoryTimelineParameters(float gapWidth, float lineHeight, float lineRadius) {
        this.gapWidth = gapWidth;
        this.lineHeight = lineHeight;
        this.lineRadius = lineRadius;
    }

    public float gapWidth;
    public float lineHeight;
    public float lineRadius;
    public int fillColor = Color.parseColor("#ffffffff");
    public int backgroundColor = Color.parseColor("#8affffff");
}
