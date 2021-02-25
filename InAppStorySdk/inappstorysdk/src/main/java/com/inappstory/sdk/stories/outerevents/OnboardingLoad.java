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

    public OnboardingLoad(int count) {
        this.count = count;
        if (count == 0)
            isEmpty = true;
    }
}
