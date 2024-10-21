package com.inappstory.sdk.core.dataholders;

public interface IReaderContentWithStatus extends IReaderContent {
    boolean hasFavorite();
    boolean hasLike();
    boolean hasShare();
    boolean hasAudio();
    boolean favorite();
    void like(int like);
    void favorite(boolean favorite);
    int like();
}
