package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface ShowStoryCallback {
    void showStory(
            StoryData storyData,
            SourceType source,
            ShowStoryAction action
    );
}
