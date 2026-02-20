package com.inappstory.sdk.refactoring.stories.data.mappers;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.refactoring.core.utils.models.Mapper;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;

public class StoryDTOToStoryCoverDTOMapper implements Mapper<StoryDTO, StoryCoverDTO> {

    @Override
    public StoryCoverDTO convert(StoryDTO obj) {
        return new StoryCoverDTO(
                obj.id(),
                obj.imageCoverByQuality(Image.QUALITY_MEDIUM),
                obj.backgroundColor
        );
    }
}
