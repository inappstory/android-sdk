package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.Objects;

public class SlideTaskKey {
    public SlideTaskKey(Integer storyId,
                        Integer index,
                        ContentType contentType) {
        this.storyId = storyId;
        this.index = index;
        this.contentType = contentType;
    }

    public SlideTaskKey(StoryTaskKey key,
                        Integer index) {
        this.storyId = key.storyId;
        this.index = index;
        this.contentType = key.contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlideTaskKey that = (SlideTaskKey) o;
        return Objects.equals(storyId, that.storyId) && Objects.equals(index, that.index) && contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, index, contentType);
    }

    @Override
    public String toString() {
        return "SlideTaskData{" +
                "storyId=" + storyId +
                ", index=" + index +
                ", storyType=" + contentType +
                '}';
    }

    public Integer storyId;
    public Integer index;
    public ContentType contentType;
}