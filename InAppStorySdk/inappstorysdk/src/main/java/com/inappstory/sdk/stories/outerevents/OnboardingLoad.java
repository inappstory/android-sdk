package com.inappstory.sdk.stories.outerevents;

public class OnboardingLoad {
    boolean isEmpty;

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getCount() {
        return count;
    }

    int count;

    public String feed;

    public String getFeed() { return feed; }

    public OnboardingLoad(int count, String feed) {
        this.count = count;
        this.feed = feed;
        if (count == 0)
            isEmpty = true;
    }
}
