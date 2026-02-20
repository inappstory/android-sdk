package com.inappstory.sdk.refactoring.stories.data.mappers;

import com.inappstory.sdk.refactoring.core.utils.models.Mapper;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryListItemDTO;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;

public class NStoryToStoryListItemDTOMapper implements Mapper<NStory, StoryListItemDTO> {
    @Override
    public StoryListItemDTO convert(NStory obj) {
        return new StoryListItemDTO(
                obj.id,
                obj.title,
                obj.titleColor,
                obj.statTitle,
                obj.videoUrl,
                obj.ugcPayload,
                obj.backgroundColor,
                obj.image,
                obj.hasSwipeUp,
                obj.like,
                obj.slidesCount,
                obj.favorite,
                obj.hideInReader,
                obj.deeplink,
                obj.gameInstance,
                obj.isOpened,
                obj.disableClose,
                obj.hasLike,
                obj.hasAudio,
                obj.hasFavorite,
                obj.hasShare
        );
    }

}
