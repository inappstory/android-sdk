package com.inappstory.sdk.stories.ui.list.items;

import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;

import java.util.List;

public interface IStoriesListFavoriteItem {
    void bindFavorite(List<IFavoritePreviewStoryDTO> favoriteImages);

    void setImages(List<IFavoritePreviewStoryDTO> favoriteImages);
}
