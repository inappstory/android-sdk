package com.inappstory.sdk.externalapi;

import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

public class StoryAPIData {
    public int id;
    public StoryData storyData;
    public String imageFilePath;
    public String videoFilePath;
    public boolean hasAudio;
    public String title;
    public String titleColor;
    public String backgroundColor;
    public boolean opened;

    @Override
    public String toString() {
        return "StoryData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", titleColor='" + titleColor + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", opened='" + opened + '\'' +
                ", hasAudio=" + hasAudio +
                ", imageFilePath=" + imageFilePath +
                ", videoFilePath=" + videoFilePath +
                ", storyData=" + storyData +
                '}';
    }

    public StoryAPIData(
            Story story,
            StoryData storyData,
            String imageFilePath,
            String videoFilePath
    ) {
        this.id = story.id;
        this.backgroundColor = story.backgroundColor();
        this.title = story.title();
        this.storyData = storyData;
        this.titleColor = story.titleColor();
        this.imageFilePath = imageFilePath;
        this.videoFilePath = videoFilePath;
        this.hasAudio = story.hasAudio();
        this.opened = story.isOpened();
    }
}
