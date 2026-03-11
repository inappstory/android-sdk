package com.inappstory.sdk.refactoring.stories.ui.list;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListViewModel;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListViewModelsHolder;
import com.inappstory.sdk.refactoring.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.refactoring.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Observer;


public class StoriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Observer<StoriesListState> {
    StoriesListState storiesListState = new StoriesListState();
    StoriesListViewModel viewModel;

    public StoriesListAdapter(
            StoriesListViewModel viewModel,
            IStoriesListItem storiesListItemCreator,
            IGetFavoriteListItem storiesListFavoriteCellCreator
    ) {
        this.viewModel = viewModel;
        viewModel.addSubscriber(this);
    }

    public void destroy() {
        viewModel.removeSubscriber(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == storiesListState.storiesIds().size()) return -1;
        return position;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof StoriesListItemContainer) {

        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == -1) {

        } else {

        }
    }

    @Override
    public int getItemCount() {
        int size = storiesListState.storiesIds().size();
        if (size == 0) return 0;
        return size + (storiesListState.hasFavorite() ? 1 : 0);
    }

    @Override
    public void onUpdate(StoriesListState newValue) {
        if (storiesListState.storiesIds().equals(newValue.storiesIds())) {
            storiesListState = newValue.copy();
            if (newValue.hasFavorite()) {
                notifyItemInserted(storiesListState.storiesIds().size());
            } else {
                notifyItemRemoved(storiesListState.storiesIds().size());
            }
        } else {
            storiesListState = newValue.copy();
            notifyDataSetChanged();
        }
    }
}
