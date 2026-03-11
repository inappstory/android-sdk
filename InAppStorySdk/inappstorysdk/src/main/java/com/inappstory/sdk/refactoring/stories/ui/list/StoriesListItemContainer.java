package com.inappstory.sdk.refactoring.stories.ui.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemCoverState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListItemState;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListItemViewModel;
import com.inappstory.sdk.refactoring.stories.ui.views.IStoriesListItem;

import java.util.Objects;

public class StoriesListItemContainer
        extends RecyclerView.ViewHolder
        implements Observer<StoriesListItemState> {
    private StoriesListItemState currentState;
    private StoriesListItemViewModel listItemViewModel;
    private IStoriesListItem storiesListItem;
    private AppearanceManager appearanceManager;


    public StoriesListItemContainer(
            @NonNull View itemView
    ) {
        super(itemView);
    }

    public void attachView(
            StoriesListItemViewModel viewModel,
            IStoriesListItem storiesListItem,
            AppearanceManager appearanceManager
    ) {
        this.listItemViewModel = viewModel;
        this.storiesListItem = storiesListItem;
        this.appearanceManager = appearanceManager;
        viewModel.addSubscriber(this);
    }

    public void detachView() {
        this.storiesListItem = null;
        if (this.listItemViewModel != null)
            this.listItemViewModel.removeSubscriber(this);
        this.listItemViewModel = null;
        this.appearanceManager = null;
    }

    private void stateIsUpdated(StoriesListItemState value) {
        IStoriesListItem storiesListItem = this.storiesListItem;
        if (storiesListItem != null) {
            storiesListItem.setId(
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
    public void onUpdate(StoriesListItemState newValue) {
        if (Objects.equals(newValue, currentState)) return;
        if (newValue == null) {
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
