package com.inappstory.sdk.core.models.callbacks;

import com.inappstory.sdk.core.models.api.Story;

public interface GetStoryByIdCallback {
    void getStory(Story story);
    void loadError(int type);
}
