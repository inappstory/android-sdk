package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface ShowSlideCallback {

    void showSlide(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index,
                   String payload);
}
