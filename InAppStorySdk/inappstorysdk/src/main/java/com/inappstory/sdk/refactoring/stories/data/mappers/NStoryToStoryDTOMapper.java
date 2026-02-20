package com.inappstory.sdk.refactoring.stories.data.mappers;

import com.inappstory.sdk.refactoring.core.utils.models.Mapper;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;

public class NStoryToStoryDTOMapper implements Mapper<NStory, StoryDTO> {
    @Override
    public StoryDTO convert(NStory obj) {
        return new StoryDTO(
                obj.id,
                obj.title,
                obj.titleColor,
                obj.statTitle,
                obj.videoUrl,
                obj.ugcPayload,
                obj.backgroundColor,
                obj.image,
                obj.hasSwipeUp,
                obj.slides,
                obj.like,
                obj.slidesCount,
                obj.hasFavorite,
                obj.deeplink,
                obj.isOpened,
                obj.disableClose,
                obj.hasLike,
                obj.hasAudio,
                obj.hasFavorite,
                obj.hasShare,
                obj.layout
        );
    }

}
