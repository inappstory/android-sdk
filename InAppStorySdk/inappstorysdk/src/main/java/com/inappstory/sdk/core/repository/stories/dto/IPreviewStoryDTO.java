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

    String getTitleColor();

    boolean hasAudio();

    boolean isOpened();

    String getImageUrl(int coverQuality);

    void open();

    int getSlidesCount();
    HashMap<String, Object> getPayload();
}
