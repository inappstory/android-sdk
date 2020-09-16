package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class OpenStoryByIndexEvent {

    public int getIndex() {
        return index;
    }

    private int index;

    public OpenStoryByIndexEvent(int index) {
        this.index = index;
    }
}
