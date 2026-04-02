package com.inappstory.sdk.refactoring.stories.ui.list.states;

public class StoryListItemCoordinates {
    private final int x;
    private final int y;

    public StoryListItemCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
