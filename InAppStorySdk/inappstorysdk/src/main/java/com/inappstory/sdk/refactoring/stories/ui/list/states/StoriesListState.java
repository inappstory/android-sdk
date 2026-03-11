package com.inappstory.sdk.refactoring.stories.ui.list.states;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoriesListState {
    public List<String> storiesIds() {
        return storiesIds != null ? storiesIds : new ArrayList<>();
    }

    public boolean hasFavorite() {
        return hasFavorite;
    }

    public StoriesListState storiesIds(List<String> storiesIds) {
        if (storiesIds != null) {
            this.storiesIds = new ArrayList<>(storiesIds);
        }
        return this;
    }

    public StoriesListState hasFavorite(boolean hasFavorite) {
        this.hasFavorite = hasFavorite;
        return this;
    }

    private List<String> storiesIds;
    private boolean hasFavorite;

    public StoriesListState copy() {
        return new StoriesListState()
                .storiesIds(storiesIds)
                .hasFavorite(hasFavorite);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoriesListState)) return false;
        StoriesListState that = (StoriesListState) o;
        return hasFavorite == that.hasFavorite && storiesIds.equals(that.storiesIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storiesIds, hasFavorite);
    }
}
