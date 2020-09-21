package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryPageOpenEvent {


    public int getIndex() {
        return index;
    }

    public int index;

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public StoryPageOpenEvent(int narrativeId, int index) {
        this.index = index;
        this.storyId = narrativeId;
    }


}
