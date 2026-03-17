package com.inappstory.sdk.refactoring.stories.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListItemViewModel;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListItemViewModelCreator;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.BaseStoriesListViewModel;
import com.inappstory.sdk.refactoring.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.refactoring.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.List;


public class StoriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Observer<StoriesListState> {
    StoriesListState storiesListState = new StoriesListState();
    BaseStoriesListViewModel viewModel;
    private final IASCore core;
    private final AppearanceManager appearanceManager;
    private final @NonNull IStoriesListItem storiesListItemCreator;
    private final @NonNull IGetFavoriteListItem storiesListFavoriteCellCreator;

    public StoriesListAdapter(
            IASCore core,
            AppearanceManager appearanceManager,
            BaseStoriesListViewModel viewModel,
            @NonNull IStoriesListItem storiesListItemCreator,
            @NonNull IGetFavoriteListItem storiesListFavoriteCellCreator
    ) {
        this.viewModel = viewModel;
        this.storiesListItemCreator = storiesListItemCreator;
        this.storiesListFavoriteCellCreator = storiesListFavoriteCellCreator;
        this.core = core;
        this.appearanceManager = appearanceManager;
        viewModel.addSubscriber(this);
    }

    public void destroy() {
        viewModel.removeSubscriber(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cs_story_list_custom_item,
                parent,
                false
        );
        if (viewType == -1) {
            return new StoriesListFavoriteCellContainer(v, parent, storiesListFavoriteCellCreator);
        } else {
            return new StoriesListItemContainer(v, parent, storiesListItemCreator);
        }
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
            int position = holder.getAbsoluteAdapterPosition();
            List<String> storiesIds = storiesListState.storiesIds();
            if (storiesIds != null && position >= 0 && position < storiesIds.size()) {
                final String storyId = storiesIds.get(position);
                ((StoriesListItemContainer) holder).attachView(
                        core.storiesListViewModels().getOrCreateStoriesListItemViewModel(
                                storyId,
                                new StoriesListItemViewModelCreator() {
                                    @Override
                                    public StoriesListItemViewModel create() {
                                        return new StoriesListItemViewModel(
                                                core,
                                                storyId
                                        );
                                    }
                                }
                        ),
                        storiesListItemCreator,
                        appearanceManager
                );
            }

        } else if (holder instanceof StoriesListFavoriteCellContainer) {
            ((StoriesListFavoriteCellContainer) holder).attachView(
                    core.storiesListViewModels().getOrCreateFavoriteCellViewModel(),
                    storiesListFavoriteCellCreator
            );
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof StoriesListItemContainer) {
            ((StoriesListItemContainer) holder).detachView();
        } else if (holder instanceof StoriesListFavoriteCellContainer) {
            ((StoriesListFavoriteCellContainer) holder).detachView();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == -1) {

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
