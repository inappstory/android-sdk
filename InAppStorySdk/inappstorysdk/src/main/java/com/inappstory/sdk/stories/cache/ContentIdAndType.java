package com.inappstory.sdk.stories.cache;

import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.Objects;

public class ContentIdAndType {
    public ContentIdAndType(int contentId, ContentType contentType) {
        this.contentId = contentId;
        this.contentType = contentType;
    }

    public int contentId;
    public ContentType contentType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentIdAndType that = (ContentIdAndType) o;
        return contentId == that.contentId && contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId, contentType);
    }

    @Override
    public String toString() {
        return "ContentIdAndType{" +
                "contentId=" + contentId +
                ", contentType=" + contentType +
                '}';
    }
}