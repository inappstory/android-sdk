package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

public class StoriesListItemData {
    public StoriesListItemData(StoryData storyData, int listIndex, float shownPercent) {
        this.storyData = storyData;
        this.listIndex = listIndex;
        this.shownPercent = shownPercent;
    }

    public StoryData storyData;
    public int listIndex;
    public float shownPercent;

    @Override
    public String toString() {
        return "StoriesListItemData{" +
                "listIndex=" + listIndex +
                ", shownPercent=" + shownPercent +
                ", storyData=" + storyData +
                '}';
    }
}
