package com.inappstory.sdk.refactoring.stories.ui.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListFavoriteCellState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemCoverState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemState;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListFavoriteCellViewModel;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListItemViewModel;
import com.inappstory.sdk.refactoring.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.refactoring.stories.ui.views.IStoriesListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoriesListFavoriteCellContainer
        extends RecyclerView.ViewHolder
        implements Observer<StoriesListFavoriteCellState> {
    private StoriesListFavoriteCellState currentState;
    private StoriesListFavoriteCellViewModel favoriteCellViewModel;
    private IGetFavoriteListItem favoriteCellItem;
    private AppearanceManager appearanceManager;


    public StoriesListFavoriteCellContainer(
            @NonNull View itemView
    ) {
        super(itemView);
    }

    public void attachView(
            StoriesListFavoriteCellViewModel viewModel,
            IGetFavoriteListItem storiesListItem,
            AppearanceManager appearanceManager
    ) {
        this.favoriteCellViewModel = viewModel;
        this.favoriteCellItem = storiesListItem;
        this.appearanceManager = appearanceManager;
        viewModel.addSubscriber(this);
    }

    public void detachView() {
        this.favoriteCellItem = null;
        if (this.favoriteCellViewModel != null)
            this.favoriteCellViewModel.removeSubscriber(this);
        this.favoriteCellViewModel = null;
        this.appearanceManager = null;
        this.currentState = null;
    }

    private void stateIsUpdated(StoriesListFavoriteCellState value) {
        IGetFavoriteListItem favoriteListItem = this.favoriteCellItem;
        List<Integer> backgroundColors = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (StoryCoverDTO cover : value.covers()) {
            if (cover.backgroundColor())
        }
        if (favoriteListItem != null) {
            favoriteListItem.setImages(
                    itemView,
                    value.id()
            );
            storiesListItem.setTitle(
                    itemView,
                    value.title(),
                    ColorUtils.parseColorRGBA(value.titleColor())
            );
            storiesListItem.setHasAudio(itemView, value.hasAudio());
            storiesListItem.setOpened(itemView, value.isOpened());
        }
    }

    private void updateImageCover(StoriesListItemCoverState coverState) {
        IStoriesListItem storiesListItem = this.storiesListItem;
        if (storiesListItem != null) {
            storiesListItem.setImage(itemView, coverState.imagePath(), ColorUtils.parseColorRGBA(
                    coverState.backgroundColor()
            ));
        }
    }

    private void updateVideoCover(StoriesListItemCoverState coverState) {
        IStoriesListItem storiesListItem = this.storiesListItem;
        if (storiesListItem != null) {
            storiesListItem.setVideo(itemView, coverState.videoPath());
        }
    }

    private void updateOpenedStatus(boolean isOpened) {
        IStoriesListItem storiesListItem = this.storiesListItem;
        if (storiesListItem != null) {
            storiesListItem.setOpened(itemView, isOpened);
        }
    }

    @Override
    public void onUpdate(StoriesListFavoriteCellState newValue) {
        if (Objects.equals(newValue, currentState)) return;
        if (newValue == null) {
            currentState = null;
            return;
        }
        if (currentState == null) {
            stateIsUpdated(newValue);
            listItemViewModel.initCover(appearanceManager.csCoverQuality());
            listItemViewModel.loadCoverResources(appearanceManager.csCoverQuality());
        } else {
            if (currentState.isOpened() != newValue.isOpened()) {
                updateOpenedStatus(newValue.isOpened());
            }
            StoriesListItemCoverState newCoverState = newValue.coverState();
            if (newCoverState != null) {
                if (!Objects.equals(currentState.coverState(), newCoverState)) {
                    if (currentState.coverState() == null) {
                        updateImageCover(newCoverState);
                        if (newCoverState.videoPath() != null) {
                            updateVideoCover(newCoverState);
                        }
                    } else {
                        if (!Objects.equals(currentState.coverState().imagePath(),
                                newCoverState.imagePath())) {
                            updateImageCover(newCoverState);
                        }
                        if (!Objects.equals(currentState.coverState().videoPath(),
                                newCoverState.videoPath())) {
                            updateVideoCover(newCoverState);
                        }
                    }
                }
            }
        }
        currentState = newValue;
    }
}
