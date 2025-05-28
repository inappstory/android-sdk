package com.inappstory.sdk.core.storieslist;

import java.util.List;

public class StoriesRequestResult {

    private final List<Integer> storiesIds;

    public StoriesRequestResult(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
    }

    public List<Integer> getStoriesIds() {
        return storiesIds;
    }
}
