package com.inappstory.sdk.stories.outercallbacks.common.single;

import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

public interface SingleLoadCallback {
    void singleLoadSuccess(StoryData storyData);
    void singleLoadError(String storyId, String reason);
}
