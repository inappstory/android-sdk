package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.core.network.content.models.Story;

public interface GetStoryByIdCallback {
    void getStory(Story story, String sessionId);
    void loadError(int type);
}
