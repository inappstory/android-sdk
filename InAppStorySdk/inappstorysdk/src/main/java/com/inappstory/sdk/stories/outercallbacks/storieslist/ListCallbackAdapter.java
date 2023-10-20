package com.inappstory.sdk.stories.outercallbacks.storieslist;

import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

import java.util.List;

public class ListCallbackAdapter implements ListCallback {

    @Override
    public void storiesLoaded(int size, String feed, List<StoryData> storyData) {

    }

    @Override
    public void storiesUpdated(int size, String feed, List<StoryData> storyData) {

    }

    @Override
    public void loadError(String feed) {

    }

    @Override
    public void itemClick(StoryData storyData, int listIndex) {

    }


}
