package com.inappstory.sdk.core.cache;

import com.inappstory.sdk.core.models.api.Story;

import java.util.Objects;

class StoryTaskData {
    public StoryTaskData(Integer storyId, Story.StoryType storyType) {
        this.storyId = storyId;
        this.storyType = storyType;
    }

    public Integer storyId;
    public Story.StoryType storyType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoryTaskData that = (StoryTaskData) o;
        return Objects.equals(storyId, that.storyId) && storyType == that.storyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, storyType);
    }
}