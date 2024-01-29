package com.inappstory.sdk.stories.stackfeed;

import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

public interface IStackStoryData {
    String title();

    int titleColor();

    boolean hasAudio();

    IStackStoryCover cover();

    boolean[] stackFeedOpenedStatuses();

    StoryData[] stackFeedStories();

    int stackFeedIndex();
}
