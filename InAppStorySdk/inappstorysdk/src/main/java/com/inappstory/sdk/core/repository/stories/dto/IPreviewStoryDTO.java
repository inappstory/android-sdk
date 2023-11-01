package com.inappstory.sdk.core.repository.stories.dto;

import com.inappstory.sdk.stories.api.models.Image;

import java.util.HashMap;
import java.util.List;

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

    List<Image> getImages();

    String getTitleColor();

    boolean hasAudio();

    boolean isOpened();

    String getImageUrl(int coverQuality);

    void open();

    int getSlidesCount();
    HashMap<String, Object> getPayload();
}
