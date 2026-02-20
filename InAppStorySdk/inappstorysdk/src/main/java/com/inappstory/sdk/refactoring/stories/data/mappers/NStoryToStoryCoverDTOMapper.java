package com.inappstory.sdk.refactoring.stories.data.mappers;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.refactoring.core.utils.models.Mapper;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;

public class NStoryToStoryCoverDTOMapper implements Mapper<NStory, StoryCoverDTO> {
    @Override
    public StoryCoverDTO convert(NStory obj) {
        return new StoryCoverDTO(
                obj.id,
                obj.imageCoverByQuality(Image.QUALITY_MEDIUM),
                obj.backgroundColor
        );
    }

}
