package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class NoConnectionEvent {

    public static final int OPEN_SESSION = 0;
    public static final int LOAD_LIST = 1;
    public static final int LOAD_SINGLE = 2;
    public static final int LOAD_ONBOARD = 3;
    public static final int READER = 4;
    public static final int LINK = 5;

    public int getType() {
        return type;
    }

    int type;

    public NoConnectionEvent(int type) {
        this.type = type;
    }
}
