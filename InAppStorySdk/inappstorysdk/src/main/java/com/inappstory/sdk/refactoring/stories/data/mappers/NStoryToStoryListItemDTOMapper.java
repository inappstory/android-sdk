package com.inappstory.sdk.refactoring.stories.data.mappers;

import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.refactoring.core.utils.usecases.Mapper;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;

import java.util.ArrayList;
import java.util.List;

public class NStoryToStoryListItemDTOMapper implements Mapper<NStory, StoriesListItemDTO> {
    @Override
    public StoriesListItemDTO convert(NStory obj) {
        List<String> timelineBackgroundColor = new ArrayList<>();
        List<String> timelineForegroundColor = new ArrayList<>();
        List<Integer> slidesDuration = new ArrayList<>();
        for (StorySlide slide: obj.slides) {
            timelineBackgroundColor.add(slide.timelineBackgroundColor());
            timelineForegroundColor.add(slide.timelineForegroundColor());
            slidesDuration.add(slide.duration);
        }
        return new StoriesListItemDTO(
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
                obj.hasShare,
                obj.timelineIsHidden,
                timelineBackgroundColor,
                timelineForegroundColor,
                slidesDuration
        );
    }

}
