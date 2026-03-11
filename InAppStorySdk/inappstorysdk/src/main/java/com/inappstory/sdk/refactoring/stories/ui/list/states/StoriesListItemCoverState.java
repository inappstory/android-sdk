package com.inappstory.sdk.refactoring.stories.ui.list.states;

import java.util.Objects;

public class StoriesListItemCoverState {
    private String backgroundColor = "#000000";
    private String imagePath;
    private String videoPath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoriesListItemCoverState)) return false;
        StoriesListItemCoverState that = (StoriesListItemCoverState) o;
        return Objects.equals(backgroundColor, that.backgroundColor)
                && Objects.equals(imagePath, that.imagePath)
                && Objects.equals(videoPath, that.videoPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroundColor, imagePath, videoPath);
    }

    public String backgroundColor() {
        return backgroundColor;
    }

    public StoriesListItemCoverState backgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public String imagePath() {
        return imagePath;
    }

    public StoriesListItemCoverState imagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String videoPath() {
        return videoPath;
    }

    public StoriesListItemCoverState videoPath(String videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    public StoriesListItemCoverState copy() {
        return new StoriesListItemCoverState()
                .imagePath(imagePath)
                .videoPath(videoPath)
                .backgroundColor(backgroundColor);
    }
}
