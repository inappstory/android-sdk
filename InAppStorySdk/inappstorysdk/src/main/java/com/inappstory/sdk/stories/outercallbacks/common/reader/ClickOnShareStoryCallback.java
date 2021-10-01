package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface ClickOnShareStoryCallback {
    void shareClick(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index);
}
