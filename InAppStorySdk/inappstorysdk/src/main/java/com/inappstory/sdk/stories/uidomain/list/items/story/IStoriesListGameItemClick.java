package com.inappstory.sdk.stories.uidomain.list.items.story;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;

public interface IStoriesListGameItemClick {
    void onClick(
            IPreviewStoryDTO storiesData,
            int index
    );
}
