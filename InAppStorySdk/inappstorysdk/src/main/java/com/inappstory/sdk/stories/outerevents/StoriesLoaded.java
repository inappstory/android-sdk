package com.inappstory.sdk.stories.outerevents;

public class StoriesLoaded {
    public StoriesLoaded(int count, String feed) {
        this.count = count;
        this.feed = feed;
    }

    public String feed;

    public String getFeed() { return feed; }

    public int getCount() {
        return count;
    }

    int count;
}
