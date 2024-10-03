package com.inappstory.sdk.core.stories;

import java.util.List;

public class StoriesListVMState {

    private List<Integer> storiesIds;

    public StoriesListVMState(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
    }

    public StoriesListVMState() {
    }

    public List<Integer> getStoriesIds() {
        return storiesIds;
    }
}
