package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class StoryCacheLoadedEvent {

    public int getStoryId() {
        return storyId;
    }

    public int storyId;

    public StoryCacheLoadedEvent(int storyId) {
        this.storyId = storyId;
    }


}
