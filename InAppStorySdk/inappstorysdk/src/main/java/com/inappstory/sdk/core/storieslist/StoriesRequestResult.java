package com.inappstory.sdk.core.storieslist;

import java.util.List;

public class StoriesRequestResult {

    private List<Integer> storiesIds;

    public StoriesRequestResult(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
    }

    public StoriesRequestResult() {
    }

    public List<Integer> getStoriesIds() {
        return storiesIds;
    }
}
