package com.inappstory.sdk.refactoring.stories.data.mappers;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.refactoring.core.utils.usecases.Mapper;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;

public class StoryListItemDTOToStoryCoverDTOMapper implements Mapper<StoriesListItemDTO, StoryCoverDTO> {

    @Override
    public StoryCoverDTO convert(StoriesListItemDTO obj) {
        return new StoryCoverDTO(
                obj.id(),
                obj.imageCoverByQuality(Image.QUALITY_MEDIUM),
                obj.backgroundColor()
        );
    }
}
