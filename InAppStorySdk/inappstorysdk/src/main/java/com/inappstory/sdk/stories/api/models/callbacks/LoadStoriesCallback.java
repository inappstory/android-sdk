package com.inappstory.sdk.stories.api.models.callbacks;

import com.inappstory.sdk.stories.api.models.Story;

import java.util.List;

public interface LoadStoriesCallback {
    void storiesLoaded(List<Story> storiesIds);
    void setFeedId(String feedId);
    void onError();
}
