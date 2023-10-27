package com.inappstory.sdk.stories.uidomain.list.items.story;

import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

import java.util.List;

public interface IStoriesListCommonItemClick {
    void onClick(List<PreviewStoryDTO> storiesData, int index);
}
