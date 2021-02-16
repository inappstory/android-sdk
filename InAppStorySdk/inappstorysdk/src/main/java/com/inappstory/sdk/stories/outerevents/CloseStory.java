package com.inappstory.sdk.stories.outerevents;

import android.util.Log;

public class CloseStory extends BaseOuterEvent {


    public static final int AUTO = 0;
    public static final int CLICK = 1;
    public static final int SWIPE = 2;
    public static final int CUSTOM = 3;


    public static final int SINGLE = 0;
    public static final int ONBOARDING = 1;
    public static final int LIST = 2;
    public static final int FAVORITE = 3;

    int index;

    public int getIndex() {
        return index;
    }

    public int getAction() {
        return action;
    }

    int action;

    public int getSource() {
        return source;
    }

    int source;

    public CloseStory(int id, String title, String tags,
                      int slidesCount, int index, int action, int source) {
        super(id, title, tags, slidesCount);
        this.index = index;
        this.action = action;
        this.source = source;
       // Log.e("CloseStory", source + "");
    }
}
