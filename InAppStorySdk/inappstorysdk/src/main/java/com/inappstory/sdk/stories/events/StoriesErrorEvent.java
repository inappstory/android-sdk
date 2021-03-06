package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class StoriesErrorEvent {
    public static final int OPEN_SESSION = 0;
    public static final int LOAD_LIST = 1;
    public static final int LOAD_SINGLE = 2;
    public static final int LOAD_ONBOARD = 3;
    public static final int READER = 4;
    public static final int EMPTY_LINK = 5;
    public static final int CACHE = 6;

    public int getType() {
        return type;
    }

    int type;

    public StoriesErrorEvent(int type) {
        this.type = type;
    }
}
