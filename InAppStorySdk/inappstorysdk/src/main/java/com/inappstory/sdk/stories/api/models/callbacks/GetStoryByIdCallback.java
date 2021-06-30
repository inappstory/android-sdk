package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.stories.api.models.Story;

public interface GetStoryByIdCallback {
    void getStory(Story story);
    void loadError(int type);
}
