package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface ShowStoryCallback {
    void showStory(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   SourceType source,
                   ShowStoryAction action);
}
