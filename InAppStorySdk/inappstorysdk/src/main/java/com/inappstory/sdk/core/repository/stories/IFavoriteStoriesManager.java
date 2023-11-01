package com.inappstory.sdk.core.repository.stories;

import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IFavoriteCellUpdatedCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IFavoriteListUpdatedCallback;

import java.util.List;

public interface IFavoriteStoriesManager {
    void addToFavorite(int storyId);

    void removeFromFavorite(int storyId);

    void removeAllFavorites();

    List<IPreviewStoryDTO> getCachedFavorites();

    List<IFavoritePreviewStoryDTO> getCachedFavoriteCell();

    void addFavoriteCellUpdatedCallback(IFavoriteCellUpdatedCallback callback);

    void removeFavoriteCellUpdatedCallback(IFavoriteCellUpdatedCallback callback);

    void addFavoriteListUpdatedCallback(IFavoriteListUpdatedCallback callback);

    void removeFavoriteListUpdatedCallback(IFavoriteListUpdatedCallback callback);
}
