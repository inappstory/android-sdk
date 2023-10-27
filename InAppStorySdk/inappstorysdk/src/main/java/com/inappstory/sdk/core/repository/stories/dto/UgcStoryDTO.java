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

public class UgcStoryDTO implements IStoryDTO {
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

    public boolean getHasLike() {
        return hasLike;
    }

    public boolean getHasAudio() {
        return hasAudio;
    }

    public boolean getHasFavorite() {
        return hasFavorite;
    }

    public boolean getHasShare() {
        return hasShare;
    }

    public int getLike() {
        return like;
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

    public List<PayloadObjectDTO> getSlidesPayload() {
        return slidesPayload;
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
    private int like;
    private int[] slidesShare;
    private List<ImagePlaceholderMappingObjectDTO> imagePlaceholdersList;
    private List<ResourceMappingObjectDTO> srcList;

    private List<PayloadObjectDTO> slidesPayload;

    public UgcStoryDTO(Story story) {
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
        this.srcList  = new ArrayList<>();
        this.slidesPayload = new ArrayList<>();
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

    public UgcStoryDTO(int id, long updatedAt) {
        this.id = id;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UgcStoryDTO))
            return false;
        UgcStoryDTO other = (UgcStoryDTO) o;
        return (this.getId() == other.getId() && this.updatedAt == other.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
