package com.inappstory.sdk.core.storieslist;

public class StoriesRequestResultHolder {

    private StoriesRequestResult state;

    public StoriesRequestResult getState() {
        return state;
    }

    public void setState(StoriesRequestResult state) {
        this.state = state;
    }

    public StoriesRequestResultHolder(StoriesRequestResult state) {
        this.state = state;
    }

}
