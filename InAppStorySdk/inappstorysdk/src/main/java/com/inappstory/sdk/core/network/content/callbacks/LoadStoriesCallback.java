package com.inappstory.sdk.core.network.content.callbacks;

import java.util.List;

public interface LoadStoriesCallback {
    void storiesLoaded(List<Integer> storiesIds);
    void setFeedId(String feedId);
    void onError();
}
