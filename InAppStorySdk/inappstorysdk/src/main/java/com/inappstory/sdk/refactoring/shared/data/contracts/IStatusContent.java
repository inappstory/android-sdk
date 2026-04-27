package com.inappstory.sdk.refactoring.shared.data.contracts;

public interface IStatusContent {
    int id();
    boolean hasFavorite();
    boolean hasLike();
    boolean hasShare();
    boolean hasAudio();
    boolean favorite();
    void like(int like);
    void favorite(boolean favorite);
    int like();
}
