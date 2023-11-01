package com.inappstory.sdk.core.repository.stories.dto;


import java.util.HashMap;
import java.util.List;

public interface IStoryDTO {
    int getId();

    String getStatTitle();

    String getTags();

    boolean isOpened();

    int getSlidesCount();

    String getSlideEventPayload(int slideIndex);

    HashMap<String, Object> getPayload();

    long getUpdatedAt();

    String getLayout();

    List<String> getPages();

    int[] getDurations();

    boolean getHasLike();

    boolean getHasAudio();

    boolean getHasFavorite();

    boolean getHasShare();

    int getLike();

    boolean getFavorite();

    void setLike(int like);

    void setFavorite(boolean favorite);

    void setOpened(boolean isOpened);

    int[] getSlidesShare();

    List<ImagePlaceholderMappingObjectDTO> getImagePlaceholdersList();

    List<ResourceMappingObjectDTO> getSrcList();

    List<ImagePlaceholderMappingObjectDTO> getImagePlaceholdersList(int slideIndex);

    List<ResourceMappingObjectDTO> getSrcList(int slideIndex);

    List<PayloadObjectDTO> getSlidesPayload();

    public boolean isScreenshotShare(int index);


    public int shareType(int index);
}
