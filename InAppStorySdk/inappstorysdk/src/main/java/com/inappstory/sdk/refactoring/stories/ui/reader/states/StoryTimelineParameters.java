package com.inappstory.sdk.refactoring.stories.ui.reader.states;

public class StoryTimelineParameters {
    public StoryTimelineParameters(float gapWidth, float lineHeight, float lineRadius) {
        this.gapWidth = gapWidth;
        this.lineHeight = lineHeight;
        this.lineRadius = lineRadius;
    }

    public float gapWidth() {
        return gapWidth;
    }

    public float lineHeight() {
        return lineHeight;
    }

    public float lineRadius() {
        return lineRadius;
    }

    private final float gapWidth;
    private final float lineHeight;
    private final float lineRadius;
}
