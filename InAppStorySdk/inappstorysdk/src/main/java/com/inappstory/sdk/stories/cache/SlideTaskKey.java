package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.Story;

import java.util.Objects;

public class SlideTaskKey {
    public SlideTaskKey(Integer storyId,
                        Integer index,
                        Story.StoryType storyType) {
        this.storyId = storyId;
        this.index = index;
        this.storyType = storyType;
    }

    public SlideTaskKey(StoryTaskKey key,
                        Integer index) {
        this.storyId = key.storyId;
        this.index = index;
        this.storyType = key.storyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlideTaskKey that = (SlideTaskKey) o;
        return Objects.equals(storyId, that.storyId) && Objects.equals(index, that.index) && storyType == that.storyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, index, storyType);
    }

    public Integer storyId;
    public Integer index;
    public Story.StoryType storyType;
}