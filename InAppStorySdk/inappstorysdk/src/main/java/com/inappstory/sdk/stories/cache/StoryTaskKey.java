package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.Objects;

class StoryTaskKey {
    public StoryTaskKey(Integer storyId, ContentType contentType) {
        this.storyId = storyId;
        this.contentType = contentType;
    }

    public Integer storyId;
    public ContentType contentType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoryTaskKey that = (StoryTaskKey) o;
        return Objects.equals(storyId, that.storyId) && contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, contentType);
    }
}