package com.inappstory.sdk.core.data;

public interface IContentWithTimeline {
    boolean timelineIsHidden();
    int slideDuration(int slideIndex);
    String timelineBackgroundColor(int slideIndex);
    String timelineForegroundColor(int slideIndex);
}
