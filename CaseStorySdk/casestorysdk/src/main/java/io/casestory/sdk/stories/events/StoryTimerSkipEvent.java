package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryTimerSkipEvent {


    public int getIndex() {
        return index;
    }

    private int index;

    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public StoryTimerSkipEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }


}
