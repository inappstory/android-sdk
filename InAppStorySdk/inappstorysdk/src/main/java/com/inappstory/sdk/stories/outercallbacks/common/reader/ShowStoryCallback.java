package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.ShowStoryAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;

public interface ShowStoryCallback {
    void showStory(
            StoryData storyData,
            ShowStoryAction action
    );
}
