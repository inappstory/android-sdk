package com.inappstory.sdk.stories.outercallbacks.common.objects;

import java.io.Serializable;

public class StoryItemCoordinates implements Serializable {
    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    private final int x;
    private final int y;

    public StoryItemCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
