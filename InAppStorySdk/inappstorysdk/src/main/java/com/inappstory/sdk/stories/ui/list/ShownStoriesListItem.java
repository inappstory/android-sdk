package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

public class ShownStoriesListItem {

    public ShownStoriesListItem() {

    }

    public ShownStoriesListItem(StoryData storyData, int listIndex, float areaPercent) {
        this.storyData = storyData;
        this.listIndex = listIndex;
        this.areaPercent = areaPercent;
    }

    public StoryData storyData;
    public int listIndex;
    public float areaPercent;

    @Override
    public String toString() {
        return "ShownItem{" +
                "listIndex=" + listIndex +
                ", areaPercent=" + areaPercent +
                '}';
    }
}
