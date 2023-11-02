package com.inappstory.sdk.core.repository.stories.dto;

import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UgcPreviewStoryDTO implements IPreviewStoryDTO {
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

    @Override
    public List<Image> getImages() {
        return null;
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

    public HashMap<String, Object> getPayload() {
        return payload;
    }

    @Override
    public boolean hasLike() {
        return false;
    }

    @Override
    public boolean hasFavorite() {
        return false;
    }

    @Override
    public boolean hasShare() {
        return false;
    }

    @Override
    public int getLike() {
        return 0;
    }

    @Override
    public boolean getFavorite() {
        return false;
    }

    @Override
    public void setLike(int like) {

    }

    @Override
    public void setFavorite(boolean favorite) {

    }

    @Override
    public void setOpened(boolean isOpened) {

    }

    @Override
    public boolean hasSwipeUp() {
        return false;
    }

    @Override
    public boolean disableClose() {
        return false;
    }

    public boolean isHasAudio() {
        return hasAudio;
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

    private HashMap<String, Object> payload;
    int slidesCount;

    public UgcPreviewStoryDTO(Story story) {
        this.id = story.id;
        this.slidesCount = story.getSlidesCount();
        this.tags = story.tags;
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
        this.payload = story.payload;
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
        if (!(o instanceof UgcPreviewStoryDTO))
            return false;
        UgcPreviewStoryDTO other = (UgcPreviewStoryDTO)o;
        return (this.getId() == other.getId() && this.isOpened() == other.isOpened());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isOpened);
    }
}
