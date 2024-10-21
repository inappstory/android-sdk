package com.inappstory.sdk.stories.cache;

import java.util.Objects;

public class SlideTaskKey {

    public SlideTaskKey(ViewContentTaskKey viewContentTaskKey,
                        Integer index) {
        this.viewContentTaskKey = viewContentTaskKey;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlideTaskKey that = (SlideTaskKey) o;
        return Objects.equals(index, that.index) &&
                Objects.equals(viewContentTaskKey, that.viewContentTaskKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(viewContentTaskKey.contentId, index, viewContentTaskKey.contentType);
    }

    @Override
    public String toString() {
        return "SlideTaskData{" +
                "viewContentId=" + viewContentTaskKey.contentId +
                ", index=" + index +
                ", contentType=" + viewContentTaskKey.contentType +
                '}';
    }

    public ViewContentTaskKey viewContentTaskKey;
    public Integer index;
}