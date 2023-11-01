package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;

import java.util.List;

public interface IFavoriteCellUpdate {
    void update(List<IFavoritePreviewStoryDTO> images, boolean isEmpty);

    List<IFavoritePreviewStoryDTO> getFavoriteImages();
}
