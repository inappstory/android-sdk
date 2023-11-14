package com.inappstory.sdk.core.repository.stories.dto;


import java.util.HashMap;

public interface IPreviewStoryDTO {
    int getId();

    String getTitle();

    String getStatTitle();

    String getTags();

    String getDeeplink();

    String getGameInstanceId();

    boolean isHideInReader();

    String getVideoUrl();

    String getBackgroundColor();

    IListOfImages getImages();

    String getTitleColor();

    boolean hasAudio();

    boolean isOpened();

    String getImageUrl(int coverQuality);

    int getSlidesCount();

    HashMap<String, Object> getPayload();

    boolean hasLike();

    boolean hasFavorite();

    boolean hasShare();

    int getLike();

    boolean getFavorite();

    void setLike(int like);

    void setFavorite(boolean favorite);

    void setOpened(boolean isOpened);

    boolean hasSwipeUp();

    boolean disableClose();
}
