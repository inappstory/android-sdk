package com.inappstory.sdk.refactoring.stories.data.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryFeedDTO {
    public List<String> storiesIds() {
        return storiesIds;
    }
    public Map<String, Boolean> hideInReader() {
        return hideInReader;
    }

    public StoryFeedDTO() {}

    public boolean hasFavorite() {
        return hasFavorite;
    }

    public List<String> storiesIds = new ArrayList<>();
    public Map<String, Boolean> hideInReader = new HashMap<>();
    public boolean hasFavorite;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryFeedDTO)) return false;
        StoryFeedDTO feedDTO = (StoryFeedDTO) o;
        return hasFavorite == feedDTO.hasFavorite &&
                Objects.equals(storiesIds, feedDTO.storiesIds) &&
                Objects.equals(hideInReader, feedDTO.hideInReader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storiesIds, hasFavorite, hideInReader);
    }
}
