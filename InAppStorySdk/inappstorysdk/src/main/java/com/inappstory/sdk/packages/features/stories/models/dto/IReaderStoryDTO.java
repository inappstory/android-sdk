package com.inappstory.sdk.packages.features.stories.models.dto;



import java.util.List;

public interface IReaderStoryDTO {
    int id();
    String page(int index);
    int slidesCount();
    String layout();
    boolean hasSwipeUp();
    boolean closeIsDisabled();
    int likeStatus();
    boolean favoriteStatus();

    boolean hasLike();
    boolean hasFavorite();
    boolean hasShare();
    boolean hasAudio();

    boolean checkIfEmpty();

    List<IStoryResourceDTO> staticResources(int slideIndex);
    List<IStoryResourceDTO> imagePlaceholders(int slideIndex);
    List<IStoryResourceDTO> vodResources(int slideIndex);
    boolean isScreenshotShare(int slideIndex);
}
