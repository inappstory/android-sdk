package com.inappstory.sdk.core.repository.stories.dto;

import com.inappstory.sdk.core.repository.stories.dto.ImagePlaceholderMappingObjectDTO;
import com.inappstory.sdk.core.repository.stories.dto.PayloadObjectDTO;
import com.inappstory.sdk.core.repository.stories.dto.ResourceMappingObjectDTO;

import java.util.HashMap;
import java.util.List;

public interface IStoryDTO {
    int getId();

    String getStatTitle();

    String getTags();

    boolean isOpened();

    int getSlidesCount();

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

    int[] getSlidesShare();

    List<ImagePlaceholderMappingObjectDTO> getImagePlaceholdersList();

    List<ResourceMappingObjectDTO> getSrcList();

    List<PayloadObjectDTO> getSlidesPayload();
}
