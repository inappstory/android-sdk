package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 03.10.2018.
 */

public class PlayVideoEvent {
    public int getIndex() {
        return index;
    }

    public int index;

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public PlayVideoEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }

}
