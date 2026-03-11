package com.inappstory.sdk.refactoring.stories.data.mappers;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.refactoring.core.utils.usecases.Mapper;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;

import java.util.List;

public class NStoryToStoryCoverDTOMapper implements Mapper<NStory, StoryCoverDTO> {
    @Override
    public StoryCoverDTO convert(NStory obj) {
        return new StoryCoverDTO(
                obj.id,
                imageCoverByQuality(obj.image),
                obj.backgroundColor
        );
    }

    private String imageCoverByQuality(List<Image> images) {
        if (images == null || images.isEmpty())
            return null;
        for (Image img : images) {
            if (img.getType().equals(Image.TYPE_MEDIUM)) return img.getUrl();
        }
        return images.get(0).getUrl();
    }

}
