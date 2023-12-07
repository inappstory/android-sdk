package com.inappstory.sdk.stories.uidomain.reader.views.bottompanel;

import androidx.lifecycle.LiveData;

public interface IBottomPanelViewModel {

    BottomPanelVisibilityState visibilityStateLD();

    LiveData<BottomPanelFavoriteState> favoriteStateLD();

    LiveData<BottomPanelLikeState> likeStateLD();

    LiveData<Boolean> soundOnStateLD();

    LiveData<Boolean> shareEnabledStateLD();

    void setVisibility(
            boolean hasFavorite,
            boolean hasLike,
            boolean hasShare,
            boolean hasSound
    );

    void shareClick();

    void likeClick();

    void dislikeClick();

    void favoriteClick();

    void soundClick();

    void likeEnabled(boolean enabled);

    void favoriteEnabled(boolean enabled);

    void shareEnabled(boolean enabled);
}
