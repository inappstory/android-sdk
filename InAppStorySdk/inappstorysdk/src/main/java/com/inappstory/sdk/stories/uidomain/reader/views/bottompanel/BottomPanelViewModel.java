package com.inappstory.sdk.stories.uidomain.reader.views.bottompanel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;

public final class BottomPanelViewModel implements IBottomPanelViewModel {
    IStoriesReaderPageViewModel pageViewModel;

    public BottomPanelViewModel(IStoriesReaderPageViewModel pageViewModel) {
        this.pageViewModel = pageViewModel;
        StoriesReaderAppearanceSettings appearanceSettings = pageViewModel.getState().appearanceSettings();
        soundOnState.postValue(InAppStoryManager.getInstance().soundOn());
        int storyId = pageViewModel.getState().storyId();
        StoryType storyType = pageViewModel.getState().getStoryType();
        IPreviewStoryDTO previewStoryDTO = IASCore.getInstance()
                .getStoriesRepository(storyType)
                .getStoryPreviewById(storyId);
        if (previewStoryDTO != null) {
            setVisibility(
                    appearanceSettings.csHasFavorite() && previewStoryDTO.hasFavorite(),
                    appearanceSettings.csHasLike() && previewStoryDTO.hasLike(),
                    appearanceSettings.csHasShare() && previewStoryDTO.hasShare(),
                    previewStoryDTO.hasAudio()
            );
            changeLikeStatus(previewStoryDTO.getLike());
            changeFavoriteStatus(previewStoryDTO.getFavorite());
        } else {
            setVisibility(
                    appearanceSettings.csHasFavorite(),
                    appearanceSettings.csHasLike(),
                    appearanceSettings.csHasShare(),
                    false
            );
        }
    }

    @Override
    public BottomPanelVisibilityState visibilityState() {
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

    @Override
    public void shareClick() {
        pageViewModel.shareClick();
    }

    @Override
    public void likeClick() {
        pageViewModel.likeClick();
    }

    @Override
    public void dislikeClick() {
        pageViewModel.dislikeClick();
    }

    @Override
    public void favoriteClick() {
        pageViewModel.favoriteClick();
    }

    @Override
    public void soundClick() {
        pageViewModel.soundClick();
    }

    @Override
    public void changeLikeStatus(int like) {
        this.likeState.postValue(
                new BottomPanelLikeState()
                        .like(like)
        );
    }

    @Override
    public void likeEnabled(boolean enabled) {
        BottomPanelLikeState state = likeState.getValue();
        if (state == null) return;
        this.likeState.postValue(
                state.copy()
                        .likeEnabled(enabled)
        );
    }

    @Override
    public void changeFavoriteStatus(boolean favorite) {
        this.favoriteState.postValue(
                new BottomPanelFavoriteState()
                        .favorite(favorite)
        );
    }

    @Override
    public void favoriteEnabled(boolean enabled) {
        BottomPanelFavoriteState state = this.favoriteState.getValue();
        if (state == null) return;
        this.favoriteState.postValue(
                state.copy()
                        .favoriteEnabled(enabled)
        );
    }

    @Override
    public void shareEnabled(boolean enabled) {
        this.shareEnabledState.postValue(enabled);
    }

    @Override
    public void soundOn(boolean soundOn) {
        this.soundOnState.postValue(soundOn);
    }
}
