package com.inappstory.sdk.stories.uidomain.list;

import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.List;
import java.util.Objects;

public class StoriesAdapterStoryData {
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatTitle() {
        return statTitle;
    }

    public String getTags() {
        return tags;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public String getGameInstanceId() {
        return gameInstanceId;
    }

    public boolean isHideInReader() {
        return hideInReader;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public boolean hasAudio() {
        return hasAudio;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public int getSlidesCount() {
        return slidesCount;
    }

    private int id;
    private String title;

    private String statTitle;
    private String tags;
    private String deeplink;
    private String gameInstanceId;
    private boolean hideInReader;
    private List<Image> images;
    private String videoUrl;
    private String backgroundColor;
    private String titleColor;
    private boolean hasAudio;
    private boolean isOpened;
    int slidesCount;

    public StoriesAdapterStoryData(Story story) {
        this.id = story.id;
        this.slidesCount = story.getSlidesCount();
        this.backgroundColor = story.getBackgroundColor();
        this.titleColor = story.getTitleColor();
        this.statTitle = story.statTitle;
        this.title = story.getTitle();
        this.deeplink = story.getDeeplink();
        this.gameInstanceId = story.getGameInstanceId();
        this.hasAudio = story.hasAudio();
        this.isOpened = story.isOpened();
        this.hideInReader = story.isHideInReader();
        this.videoUrl = story.getVideoUrl();
        this.images = story.getImage();
    }

    public String getImageUrl(int coverQuality) {
        Image proper = Image.getProperImage(images, coverQuality);
        if (proper != null) return proper.getUrl();
        return null;
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this)
            return true;
        if (!(o instanceof StoriesAdapterStoryData))
            return false;
        StoriesAdapterStoryData other = (StoriesAdapterStoryData)o;
        return (this.getId() == other.getId() && this.isOpened() == other.isOpened());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isOpened);
    }
}
