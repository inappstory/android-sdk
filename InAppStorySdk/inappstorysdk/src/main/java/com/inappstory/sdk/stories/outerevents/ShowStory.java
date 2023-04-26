package com.inappstory.sdk.stories.outerevents;

public class ShowStory extends BaseOuterEvent {


    public static final int SINGLE = 0;
    public static final int ONBOARDING = 1;
    public static final int LIST = 2;
    public static final int FAVORITE = 3;
    public static final int UGC_LIST = 4;


    public static final int ACTION_OPEN = 0;
    public static final int ACTION_CLICK = 1;
    public static final int ACTION_SWIPE = 2;
    public static final int ACTION_AUTO = 3;
    public static final int ACTION_CUSTOM = 4;

    int action;

    public int getSource() {
        return source;
    }

    public int getAction() {
        return action;
    }

    int source;

    public ShowStory(int id, String title, String tags, int slidesCount, int source, int action) {
        super(id, title, tags, slidesCount);
        this.source = source;
        this.action = action;
    }
}
