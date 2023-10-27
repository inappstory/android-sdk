package com.inappstory.sdk.stories.uidomain.list.items.story;

import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

public interface IStoriesListDeeplinkItemClick {
    void onClick(
            PreviewStoryDTO storiesData,
            int index
    );
}
