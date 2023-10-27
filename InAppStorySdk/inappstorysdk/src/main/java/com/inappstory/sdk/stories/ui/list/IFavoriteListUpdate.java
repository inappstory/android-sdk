package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

public interface IFavoriteListUpdate {
    void favorite(PreviewStoryDTO data);
    void removeFromFavorite(int storyId);
}
