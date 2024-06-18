package com.inappstory.sdk.externalapi.storylist;

import java.util.List;
import java.util.Objects;

public class IASStoryListRequestData {
    public String feed;
    public String uniqueId;
    public List<String> tags;
    public boolean hasFavorite;
    public boolean isFavorite;

    public IASStoryListRequestData(
            String feed,
            String uniqueId,
            List<String> tags,
            boolean hasFavorite,
            boolean isFavorite
    ) {
        this.feed = feed;
        this.uniqueId = uniqueId;
        this.tags = tags;
        this.hasFavorite = hasFavorite;
        this.isFavorite = isFavorite;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IASStoryListRequestData that = (IASStoryListRequestData) o;
        return Objects.equals(feed, that.feed) &&
                Objects.equals(uniqueId, that.uniqueId) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(isFavorite, that.isFavorite) &&
                Objects.equals(hasFavorite, that.hasFavorite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feed, uniqueId, tags, isFavorite, hasFavorite);
    }
}
