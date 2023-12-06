package com.inappstory.sdk.stories.uidomain.reader.page;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public final class BottomPanelViewModel implements IBottomPanelViewModel {
    public BottomPanelViewModel() {

    }

    public BottomPanelVisibilityState visibilityStateLD() {
        return visibilityState;
    }

    public LiveData<BottomPanelFavoriteState> favoriteStateLD() {
        return favoriteState;
    }

    public LiveData<BottomPanelLikeState> likeStateLD() {
        return likeState;
    }

    public LiveData<Boolean> soundOnStateLD() {
        return soundOnState;
    }

    public LiveData<Boolean> shareEnabledStateLD() {
        return shareEnabledState;
    }

    private final BottomPanelVisibilityState visibilityState = new BottomPanelVisibilityState();

    private final MutableLiveData<BottomPanelFavoriteState> favoriteState =
            new MutableLiveData<>(new BottomPanelFavoriteState());

    private final MutableLiveData<BottomPanelLikeState> likeState =
            new MutableLiveData<>(new BottomPanelLikeState());

    private final MutableLiveData<Boolean> soundOnState =
            new MutableLiveData<>(true);

    private final MutableLiveData<Boolean> shareEnabledState =
            new MutableLiveData<>(true);

    public void setVisibility(
            boolean hasFavorite,
            boolean hasLike,
            boolean hasShare,
            boolean hasSound
    ) {
        this.visibilityState
                .hasLike(hasLike)
                .hasFavorite(hasFavorite)
                .hasShare(hasShare)
                .hasSound(hasSound);
    }

    public void like(int like) {
        this.likeState.postValue(
                new BottomPanelLikeState()
                        .like(like)
        );
    }

    public void likeEnabled(boolean enabled) {
        BottomPanelLikeState state = likeState.getValue();
        if (state == null) return;
        this.likeState.postValue(
                state.copy()
                        .likeEnabled(enabled)
        );
    }

    public void favorite(boolean favorite) {
        this.favoriteState.postValue(
                new BottomPanelFavoriteState()
                        .favorite(favorite)
        );
    }

    public void favoriteEnabled(boolean enabled) {
        BottomPanelFavoriteState state = this.favoriteState.getValue();
        if (state == null) return;
        this.favoriteState.postValue(
                state.copy()
                        .favoriteEnabled(enabled)
        );
    }

    public void shareEnabled(boolean enabled) {
        this.shareEnabledState.postValue(enabled);
    }

    public void soundOn(boolean soundOn) {
        this.soundOnState.postValue(soundOn);
    }
}
