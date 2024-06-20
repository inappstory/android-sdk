package com.inappstory.sdk.packages.features.stories.models.dto;

public interface IListStoryDTO {
    int id();
    String title();
    String imageCover(int quality);
    String videoCover();
    String titleColor();
    String backgroundColor();
    boolean hasAudio();
    boolean isOpened();
    boolean hideInReader();
    String deeplink();
    String gameInstanceId();
}
