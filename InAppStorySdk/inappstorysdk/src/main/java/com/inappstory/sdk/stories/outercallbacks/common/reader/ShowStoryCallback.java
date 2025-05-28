package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.api.IASCallback;

public interface ShowStoryCallback extends IASCallback {
    void showStory(
            StoryData storyData,
            ShowStoryAction action
    );
}
