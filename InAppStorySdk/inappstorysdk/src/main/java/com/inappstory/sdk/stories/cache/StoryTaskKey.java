package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.Story;

import java.util.Objects;

class StoryTaskKey {
    public StoryTaskKey(Integer storyId, Story.StoryType storyType) {
        this.storyId = storyId;
        this.storyType = storyType;
    }

    public Integer storyId;
    public Story.StoryType storyType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoryTaskKey that = (StoryTaskKey) o;
        return Objects.equals(storyId, that.storyId) && storyType == that.storyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, storyType);
    }
}