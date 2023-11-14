package com.inappstory.sdk.core.models.callbacks;

import com.inappstory.sdk.core.models.api.Story;

import java.util.List;

public interface LoadStoriesCallback {
    void storiesLoaded(List<Story> storiesIds);
    void setFeedId(String feedId);
    void onError();
}
