package com.inappstory.sdk.stories.api.models.callbacks;

import java.util.List;

public interface LoadStoriesCallback {
    void storiesLoaded(List<Integer> storiesIds);
    void onError();
}
