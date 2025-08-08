package com.inappstory.sdk.externalapi;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.utils.StringWithPlaceholders;
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
                ", opened=" + opened +
                ", hasAudio=" + hasAudio +
                ", imageFilePath=" + imageFilePath +
                ", videoFilePath=" + videoFilePath +
                ", storyData=" + storyData +
                '}';
    }

    public StoryAPIData(
            final IListItemContent story,
            final StoryData storyData,
            final String imageFilePath,
            final String videoFilePath
    ) {
        this.id = story.id();
        this.backgroundColor = story.backgroundColor();
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                StoryAPIData.this.title = new StringWithPlaceholders().replace(story.title(), core);
            }

            @Override
            public void error() {
                StoryAPIData.this.title = story.title();
            }
        });
        this.storyData = storyData;
        this.titleColor = story.titleColor();
        this.imageFilePath = imageFilePath;
        this.videoFilePath = videoFilePath;
        this.hasAudio = story.hasAudio();
        this.opened = story.isOpened();
    }
}
