package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.network.annotations.models.Ignore;
import com.inappstory.sdk.stories.api.models.Story;

public class StoryData {
    public int id;
    public String title;
    public String tags;
    public String feed;
    public int slidesCount;
    @Ignore
    public Story.StoryType storyType;

    public StoryData() {}
    public StoryData(int id, String title, String tags, int slidesCount) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.slidesCount = slidesCount;
        this.storyType = Story.StoryType.COMMON;
    }
    public StoryData(int id, String title, String tags, int slidesCount, String feed) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.slidesCount = slidesCount;
        this.storyType = Story.StoryType.COMMON;
        this.feed = feed;
    }

    public StoryData(int id, Story.StoryType storyType, String title, String tags, int slidesCount, String feed) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.slidesCount = slidesCount;
        this.storyType = storyType;
        this.feed = feed;
    }

    @Override
    public String toString() {
        return "StoryData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", slidesCount=" + slidesCount +
                '}';
    }
}
