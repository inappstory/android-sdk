package com.inappstory.sdk.stories.outerevents;

public class ShowStory extends BaseOuterEvent {


    public static final int SINGLE = 0;
    public static final int ONBOARDING = 1;
    public static final int LIST = 2;
    public static final int FAVORITE = 3;

    public int getSource() {
        return source;
    }

    int source;

    public ShowStory(int id, String title, String tags, int slidesCount, int source) {
        super(id, title, tags, slidesCount);
        this.source = source;
    }
}
