package com.inappstory.sdk.stories.outercallbacks.common.reader;

public abstract class ShowSlideCallback {

    public abstract void showSlide(int id,
                                   String title,
                                   String tags,
                                   int slidesCount,
                                   int index,
                                   String payload);
}
