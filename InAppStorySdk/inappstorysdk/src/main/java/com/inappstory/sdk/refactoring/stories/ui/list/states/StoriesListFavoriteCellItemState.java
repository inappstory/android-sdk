package com.inappstory.sdk.refactoring.stories.ui.list.states;


import java.util.Objects;

public class StoriesListFavoriteCellItemState {
    int id;
    int backgroundColor;
    String filePath;

    public int id() {
        return id;
    }

    public StoriesListFavoriteCellItemState id(int id) {
        this.id = id;
        return this;
    }

    public int backgroundColor() {
        return backgroundColor;
    }

    public StoriesListFavoriteCellItemState backgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public String filePath() {
        return filePath;
    }

    public StoriesListFavoriteCellItemState filePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoriesListFavoriteCellItemState)) return false;
        StoriesListFavoriteCellItemState that = (StoriesListFavoriteCellItemState) o;
        return id == that.id
                && Objects.equals(backgroundColor, that.backgroundColor)
                && Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, backgroundColor, filePath);
    }

    public StoriesListFavoriteCellItemState copy() {
        return new StoriesListFavoriteCellItemState()
                .id(id)
                .filePath(filePath)
                .backgroundColor(backgroundColor);
    }
}
