package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.Objects;

public class ViewContentTaskKey {
    public ViewContentTaskKey(int contentId, ContentType contentType) {
        this.contentId = contentId;
        this.contentType = contentType;
    }

    public int contentId;
    public ContentType contentType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewContentTaskKey that = (ViewContentTaskKey) o;
        return contentId == that.contentId && contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, contentType);
    }
}