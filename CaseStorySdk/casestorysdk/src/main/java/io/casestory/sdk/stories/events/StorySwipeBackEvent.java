package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StorySwipeBackEvent {


    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public StorySwipeBackEvent(int storyId) {
        this.storyId = storyId;
    }


}
