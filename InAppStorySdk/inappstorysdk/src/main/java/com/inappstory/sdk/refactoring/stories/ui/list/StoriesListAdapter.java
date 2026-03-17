package com.inappstory.sdk.refactoring.stories.ui.list;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListItemViewModel;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.StoriesListItemViewModelCreator;
import com.inappstory.sdk.refactoring.stories.ui.list.viewmodels.BaseStoriesListViewModel;
import com.inappstory.sdk.refactoring.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.refactoring.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Observer<StoriesListState> {
    StoriesListState storiesListState = new StoriesListState();
    BaseStoriesListViewModel viewModel;
    private final IASCore core;
    private final AppearanceManager appearanceManager;
    private IStoriesListItem storiesListItemCreator;
    private IGetFavoriteListItem storiesListFavoriteCellCreator;
    private List<String> storiesIds = new ArrayList<>();

    public StoriesListAdapter(
            IASCore core,
            AppearanceManager appearanceManager,
            BaseStoriesListViewModel viewModel,
            IStoriesListItem storiesListItemCreator,
            IGetFavoriteListItem storiesListFavoriteCellCreator
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
        int pWidth = parent.getWidth();
        pWidth = pWidth > 0 ? pWidth : Sizes.getScreenSize(parent.getContext()).x;
        if (viewType == -1) {
            if (storiesListFavoriteCellCreator == null)
                storiesListFavoriteCellCreator = new StoriesListDefaultFavoriteCell(
                        appearanceManager,
                        parent.getContext(),
                        parent.getLayoutDirection(),
                        pWidth
                );
            return new StoriesListFavoriteCellContainer(v, parent, storiesListFavoriteCellCreator);
        } else {
            if (storiesListItemCreator == null)
                storiesListItemCreator = new StoriesListDefaultItem(
                        appearanceManager,
                        new StoriesListDefaultItemPresenter(),
                        parent.getContext(),
                        parent.getLayoutDirection(),
                        pWidth
                );
            return new StoriesListItemContainer(v, parent, storiesListItemCreator);
        }
    }

    public void loadStories() {
        if (viewModel != null)
            viewModel.loadStories();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

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
    public int getItemCount() {
        int size = storiesIds.size();
        if (size == 0) return 0;
        return size + (storiesListState.hasFavorite() ? 1 : 0);
    }

    @Override
    public void onUpdate(StoriesListState newValue) {
        if (Objects.equals(storiesListState.storiesIds(), newValue.storiesIds())) {
            storiesListState = newValue.copy();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (newValue.hasFavorite()) {
                        notifyItemInserted(storiesListState.storiesIds().size());
                    } else {
                        notifyItemRemoved(storiesListState.storiesIds().size());
                    }
                }
            });

        } else {
            storiesListState = newValue.copy();
            storiesIds.clear();
            storiesIds.addAll(newValue.storiesIds());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        }
    }
}
