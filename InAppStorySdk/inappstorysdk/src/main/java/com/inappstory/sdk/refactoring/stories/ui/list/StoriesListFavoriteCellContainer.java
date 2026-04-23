package com.inappstory.sdk.refactoring.stories.ui.list;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.R;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListFavoriteCellItemState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListFavoriteCellState;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListFavoriteCellViewModel;
import com.inappstory.sdk.refactoring.stories.ui.contracts.IGetFavoriteListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoriesListFavoriteCellContainer
        extends RecyclerView.ViewHolder
        implements Observer<StoriesListFavoriteCellState> {
    private StoriesListFavoriteCellState currentState;
    private StoriesListFavoriteCellViewModel favoriteCellViewModel;
    private IGetFavoriteListItem favoriteCellItem;

    public ViewGroup getParent() {
        return parent;
    }

    ViewGroup parent = null;

    public StoriesListFavoriteCellContainer(
            @NonNull View itemView,
            ViewGroup parent,
            IGetFavoriteListItem favoriteCellItem
    ) {
        super(itemView);
        this.favoriteCellItem = favoriteCellItem;
        this.parent = parent;
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        vg.addView(getDefaultFavoriteCell());
    }

    private View getDefaultFavoriteCell() {
        IGetFavoriteListItem favoriteCellItem = this.favoriteCellItem;
        if (favoriteCellItem == null) return null;
        return favoriteCellItem.getFavoriteItem();
    }

    public void attachView(
            StoriesListFavoriteCellViewModel viewModel,
            IGetFavoriteListItem favoriteCellItem
    ) {
        this.favoriteCellViewModel = viewModel;
        this.favoriteCellItem = favoriteCellItem;
        viewModel.addSubscriber(this);
    }

    public void detachView() {
        this.favoriteCellItem = null;
        if (this.favoriteCellViewModel != null)
            this.favoriteCellViewModel.removeSubscriber(this);
        this.favoriteCellViewModel = null;
        this.currentState = null;
    }

    private boolean viewCanBeUsed() {
        ViewGroup parent = this.parent;
        if (parent == null) return false;
        if (!parent.isAttachedToWindow()) return false;
        Context context = parent.getContext();
        if (context == null)
            return false;
        if (context instanceof Activity) {
            return !((Activity) context).isFinishing() && !((Activity) context).isDestroyed();
        }
        return true;
    }

    private void stateIsUpdated(StoriesListFavoriteCellState value) {
        IGetFavoriteListItem favoriteListItem = this.favoriteCellItem;
        if (favoriteListItem == null) return;
        if (!viewCanBeUsed()) return;
        List<Integer> backgroundColors = new ArrayList<>();
        List<String> images = new ArrayList<>();
        for (StoriesListFavoriteCellItemState cover : value.covers()) {
            backgroundColors.add(cover.backgroundColor());
            images.add(cover.filePath());
        }
        favoriteListItem.setImages(
                itemView,
                images,
                backgroundColors,
                value.covers().size()
        );
    }

    private void stateIsCreated(StoriesListFavoriteCellState value) {
        IGetFavoriteListItem favoriteListItem = this.favoriteCellItem;
        if (!viewCanBeUsed()) return;
        List<Integer> backgroundColors = new ArrayList<>();
        for (StoriesListFavoriteCellItemState cover : value.covers()) {
            backgroundColors.add(cover.backgroundColor());
        }
        if (favoriteListItem != null) {
            favoriteListItem.bindFavoriteItem(
                    itemView,
                    backgroundColors,
                    value.covers().size()
            );
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
            stateIsCreated(newValue);
        } else {
            stateIsUpdated(newValue);
        }
        currentState = newValue;
    }
}
