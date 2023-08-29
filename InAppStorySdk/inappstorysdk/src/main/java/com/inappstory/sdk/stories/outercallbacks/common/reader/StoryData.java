package com.inappstory.sdk.stories.outercallbacks.common.reader;

public class StoryData {
    public int id;
    public String title;
    public String tags;
    public int slidesCount;

    public StoryData() {}
    public StoryData(int id, String title, String tags, int slidesCount) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.slidesCount = slidesCount;
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
