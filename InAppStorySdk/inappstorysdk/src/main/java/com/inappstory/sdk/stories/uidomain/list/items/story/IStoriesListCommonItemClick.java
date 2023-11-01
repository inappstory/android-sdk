package com.inappstory.sdk.stories.uidomain.list.items.story;

import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

import java.util.List;

public interface IStoriesListCommonItemClick {
    void onClick(List<IPreviewStoryDTO> storiesData, int index);
}
