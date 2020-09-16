package io.casestory.sdk.stories.api.models.callbacks;

import io.casestory.sdk.stories.api.models.Story;

public interface GetStoryByIdCallback {
    void getStory(Story story);
}
