package com.inappstory.sdk.stories.ui.list.adapters;

import android.content.Context;
import android.view.View;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.ui.list.items.favorite.StoriesListFavoriteItem;
import com.inappstory.sdk.stories.ui.list.items.story.StoriesListItem;
import com.inappstory.sdk.stories.ui.list.items.ugceditor.StoriesListUgcEditorItem;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListCommonItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListDeeplinkItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListGameItemClick;
import com.inappstory.sdk.ugc.list.OnUGCItemClick;

import java.util.List;

public class CommonStoriesListAdapter extends BaseStoriesListAdapter {
    public CommonStoriesListAdapter(
            Context context,
            String listID,
            List<StoriesAdapterStoryData> storiesData,
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
                storiesData,
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
        int shift = 0;
        if (useUGC) shift = 1;
        if (getCurrentStories().isEmpty()) return;
        notifyItemChanged(getCurrentStories().size() - 1 + shift);
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
}
