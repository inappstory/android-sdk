package com.inappstory.sdk.stories.ui.list.adapters;

import android.content.Context;
import android.view.View;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.IFavoriteCellUpdate;
import com.inappstory.sdk.stories.ui.list.IFavoriteListUpdate;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.ui.list.items.favorite.StoriesListFavoriteItem;
import com.inappstory.sdk.stories.ui.list.items.story.StoriesListItem;
import com.inappstory.sdk.stories.ui.list.items.ugceditor.StoriesListUgcEditorItem;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListCommonItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListDeeplinkItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListGameItemClick;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;

import java.util.ArrayList;
import java.util.List;

public final class CommonStoriesListAdapter extends BaseStoriesListAdapter implements IFavoriteCellUpdate {
    public CommonStoriesListAdapter(
            Context context,
            String listID,
            String feed,
            AppearanceManager manager,
            boolean useFavorite,
            boolean useUGC,
            IStoriesListCommonItemClick storiesListCommonItemClick,
            IStoriesListDeeplinkItemClick storiesListDeeplinkItemClick,
            IStoriesListGameItemClick storiesListGameItemClick,
            OnFavoriteItemClick favoriteItemClick,
            OnUGCItemClick ugcItemClick
    ) {
        super(
                context,
                listID,
                feed,
                manager,
                false,
                useFavorite,
                useUGC,
                storiesListCommonItemClick,
                storiesListDeeplinkItemClick,
                storiesListGameItemClick,
                favoriteItemClick,
                ugcItemClick
        );
    }

    @Override
    public void clearAllFavorites() {
        notifyFavoriteItem(true, false);
    }

    private void notifyFavoriteItem(boolean isEmpty, boolean wasEmpty) {
        int shift = 0;
        if (useUGC) shift = 1;
        if (getCurrentStories().isEmpty()) return;
        int position = getCurrentStories().size() + shift;
        if (wasEmpty) {
            if (!isEmpty)
                notifyItemInserted(position);
        } else if (isEmpty) {
            notifyItemRemoved(position);
        } else {
            notifyItemChanged(position);
        }
    }

    @Override
    public BaseStoriesListItem getViewHolderItem(View view, int viewType) {
        if (viewType == -1) {
            return new StoriesListFavoriteItem(view, manager);
        } else if (viewType == -2) {
            return new StoriesListUgcEditorItem(view, manager);
        } else {
            return new StoriesListItem(view, manager, (viewType % 5) == 2);
        }
    }

    private List<FavoriteImage> favoriteImages = new ArrayList<>();

    @Override
    public void update(List<FavoriteImage> images, boolean isEmpty) {
        this.favoriteImages.clear();
        this.favoriteImages.addAll(images);
        notifyFavoriteItem(images.isEmpty(), isEmpty);
    }

    @Override
    public List<FavoriteImage> getFavoriteImages() {
        return favoriteImages;
    }
}
