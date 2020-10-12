package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryPageLoadedEvent {


    public int getIndex() {
        return index;
    }

    public int index;

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public StoryPageLoadedEvent(int storyId, int index) {
        this.index = index;
        this.storyId = storyId;
    }


}
