package com.inappstory.sdk.stories.outercallbacks.storieslist;

import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

import java.util.List;

public interface ListCallback {
    void storiesLoaded(int size, String feed, List<StoryData> storyData);

    void storiesUpdated(int size, String feed);

    void loadError(String feed);

    void itemClick(
            StoryData storyData,
            int listIndex
    );
}
