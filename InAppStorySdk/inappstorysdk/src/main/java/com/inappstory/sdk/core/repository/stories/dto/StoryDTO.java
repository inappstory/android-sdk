package com.inappstory.sdk.core.repository.stories.dto;

import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.api.models.ImagePlaceholderMappingObject;
import com.inappstory.sdk.stories.api.models.PayloadObject;
import com.inappstory.sdk.stories.api.models.ResourceMappingObject;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StoryDTO implements IStoryDTO {
    public int getId() {
        return id;
    }

    public String getStatTitle() {
        return statTitle;
    }

    public String getTags() {
        return tags;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public int getSlidesCount() {
        return slidesCount;
    }

    @Override
    public void setSlidesCount(int slidesCount) {
        this.slidesCount = slidesCount;
    }

    public boolean checkIfEmpty() {
        return (getLayout() == null || pages == null || pages.isEmpty());
    }

    public HashMap<String, Object> getPayload() {
        return payload;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getLayout() {
        return layout;
    }

    public List<String> getPages() {
        return pages;
    }

    public int[] getDurations() {
        return durations;
    }

    public boolean hasLike() {
        return hasLike;
    }

    public boolean hasAudio() {
        return hasAudio;
    }

    public boolean hasFavorite() {
        return hasFavorite;
    }

    public boolean hasShare() {
        return hasShare;
    }

    public int getLike() {
        return like;
    }

    @Override
    public boolean getFavorite() {
        return favorite;
    }

    public int[] getSlidesShare() {
        return slidesShare;
    }

    public List<ImagePlaceholderMappingObjectDTO> getImagePlaceholdersList() {
        return imagePlaceholdersList;
    }

    public List<ResourceMappingObjectDTO> getSrcList() {
        return srcList;
    }

    @Override
    public List<ImagePlaceholderMappingObjectDTO> getImagePlaceholdersList(int slideIndex) {
        if (imagePlaceholdersList == null) return new ArrayList<>();
        List<ImagePlaceholderMappingObjectDTO> result = new ArrayList<>();
        for (ImagePlaceholderMappingObjectDTO objectDTO : imagePlaceholdersList) {
            if (objectDTO.getIndex() == slideIndex) result.add(objectDTO);
        }
        return result;
    }

    @Override
    public List<ResourceMappingObjectDTO> getSrcList(int slideIndex) {
        if (srcList == null) return new ArrayList<>();
        List<ResourceMappingObjectDTO> result = new ArrayList<>();
        for (ResourceMappingObjectDTO objectDTO : srcList) {
            if (objectDTO.getIndex() == slideIndex) result.add(objectDTO);
        }
        return result;
    }

    public List<PayloadObjectDTO> getSlidesPayload() {
        return slidesPayload;
    }

    @Override
    public boolean isScreenshotShare(int index) {
        return shareType(index) == 1;
    }

    @Override
    public boolean hasSwipeUp() {
        return hasSwipeUp;
    }

    @Override
    public boolean disableClose() {
        return disableClose;
    }

    @Override
    public String getSlideEventPayload(int slideIndex) {
        if (slidesPayload == null) return null;
        for (PayloadObjectDTO payloadObject : slidesPayload) {
            if (slideIndex == payloadObject.getSlideIndex()) {
                return payloadObject.getPayload();
            }
        }
        return null;
    }

    @Override
    public int shareType(int index) {
        if (slidesShare == null) return 0;
        if (slidesShare.length <= index) return 0;
        return slidesShare[index];
    }

    private int id;
    private String statTitle;
    private String tags;
    private boolean isOpened;
    private long updatedAt;
    private HashMap<String, Object> payload;
    private int slidesCount;
    private String layout;
    private List<String> pages;
    private int[] durations;

    private boolean hasLike;
    private boolean hasAudio;
    private boolean hasFavorite;
    private boolean hasShare;
    private boolean hasSwipeUp;
    private boolean disableClose;
    private int like;
    private boolean favorite;

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    private int[] slidesShare;
    private List<ImagePlaceholderMappingObjectDTO> imagePlaceholdersList;
    private List<ResourceMappingObjectDTO> srcList;

    private List<PayloadObjectDTO> slidesPayload;

    public StoryDTO(Story story) {
        this.id = story.id;
        this.slidesCount = story.getSlidesCount();
        this.statTitle = story.statTitle;
        this.isOpened = story.isOpened();
        this.tags = story.tags;
        this.payload = story.payload;
        this.layout = story.getLayout();
        this.updatedAt = story.getUpdatedAt();
        this.durations = ArrayUtil.toIntArray(story.getDurations());
        this.slidesShare = ArrayUtil.toIntArray(story.getSlidesShare());
        this.hasAudio = story.hasAudio();
        this.hasFavorite = story.hasFavorite();
        this.hasLike = story.hasLike();
        this.hasShare = story.hasShare();
        this.like = story.getLike();
        this.pages = story.getPages();
        this.imagePlaceholdersList = new ArrayList<>();
        this.srcList = new ArrayList<>();
        this.slidesPayload = new ArrayList<>();
        this.hasSwipeUp = story.hasSwipeUp();
        this.disableClose = story.disableClose;
        for (ImagePlaceholderMappingObject object : story.getImagePlaceholdersList()) {
            this.imagePlaceholdersList.add(new ImagePlaceholderMappingObjectDTO(object));
        }
        for (ResourceMappingObject object : story.getSrcList()) {
            this.srcList.add(new ResourceMappingObjectDTO(object));
        }
        for (PayloadObject object : story.getSlidesPayload()) {
            this.slidesPayload.add(new PayloadObjectDTO(object));
        }
    }

    public StoryDTO(int id, long updatedAt) {
        this.id = id;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this)
            return true;
        if (!(o instanceof StoryDTO))
            return false;
        StoryDTO other = (StoryDTO) o;
        return (this.getId() == other.getId() && this.updatedAt == other.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
