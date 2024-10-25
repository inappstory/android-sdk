package com.inappstory.sdk.stories.cache;

import java.util.Objects;

public class SlideTaskKey {

    public SlideTaskKey(ContentIdAndType contentIdAndType,
                        Integer index) {
        this.contentIdAndType = contentIdAndType;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlideTaskKey that = (SlideTaskKey) o;
        return Objects.equals(index, that.index) &&
                Objects.equals(contentIdAndType, that.contentIdAndType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentIdAndType.contentId, index, contentIdAndType.contentType);
    }

    @Override
    public String toString() {
        return "SlideTaskData{" +
                "viewContentId=" + contentIdAndType.contentId +
                ", index=" + index +
                ", contentType=" + contentIdAndType.contentType +
                '}';
    }

    public ContentIdAndType contentIdAndType;
    public Integer index;
}