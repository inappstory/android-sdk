package com.inappstory.sdk.core.repository.stories.dto;

public interface IFavoritePreviewStoryDTO {
    int getId();
    String getBackgroundColor();
    String getImageUrl(int coverQuality);
}
