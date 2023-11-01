package com.inappstory.sdk.stories.uidomain.list.items.story;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

public interface IStoriesListDeeplinkItemClick {
    void onClick(
            IPreviewStoryDTO storiesData,
            int index
    );
}
