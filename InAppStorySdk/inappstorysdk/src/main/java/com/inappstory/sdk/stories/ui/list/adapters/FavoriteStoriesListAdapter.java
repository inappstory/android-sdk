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

public class FavoriteStoriesListAdapter extends BaseStoriesListAdapter {
    public FavoriteStoriesListAdapter(
            Context context,
            String listID,
            List<StoriesAdapterStoryData> storiesData,
            AppearanceManager manager,
            IStoriesListCommonItemClick storiesListCommonItemClick,
            IStoriesListDeeplinkItemClick storiesListDeeplinkItemClick,
            IStoriesListGameItemClick storiesListGameItemClick
    ) {
        super(
                context,
                listID,
                storiesData,
                manager,
                false,
                false,
                false,
                storiesListCommonItemClick,
                storiesListDeeplinkItemClick,
                storiesListGameItemClick,
                null,
                null
        );
    }

    @Override
    public void clearAllFavorites() {
        refreshList();
    }

    @Override
    public BaseStoriesListItem getViewHolderItem(View view, int viewType) {
        return new StoriesListItem(view, manager, (viewType % 5) == 2);
    }
}
