package com.inappstory.sdk.core.dataholders.models;

public interface IContentWithStatus {
    int id();
    boolean hasFavorite();
    boolean hasLike();
    boolean hasShare();
    boolean hasAudio();
    boolean favorite();
    void like(int like);
    void favorite(boolean favorite);
    int like();
    boolean hasSwipeUp();
    boolean disableClose();
    boolean isOpened();
    void setOpened(boolean isOpened);
}
