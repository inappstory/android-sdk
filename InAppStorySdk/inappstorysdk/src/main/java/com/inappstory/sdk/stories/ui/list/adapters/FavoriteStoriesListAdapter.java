package com.inappstory.sdk.stories.ui.list.adapters;

import android.content.Context;
import android.view.View;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.callbacks.OnFavoriteItemClick;
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

import java.util.List;
import java.util.ListIterator;

public final class FavoriteStoriesListAdapter extends BaseStoriesListAdapter implements IFavoriteListUpdate {
    public FavoriteStoriesListAdapter(
            Context context,
            String listID,
            AppearanceManager manager,
            IStoriesListCommonItemClick storiesListCommonItemClick,
            IStoriesListDeeplinkItemClick storiesListDeeplinkItemClick,
            IStoriesListGameItemClick storiesListGameItemClick
    ) {
        super(
                context,
                listID,
                null,
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

    @Override
    public void favorite(StoriesAdapterStoryData data) {
        if (storiesData.contains(data)) return;
        storiesData.add(0, data);
    }

    @Override
    public void removeFromFavorite(int storyId) {
        ListIterator<StoriesAdapterStoryData> iterator = storiesData.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == storyId) {
                iterator.remove();
            }
        }
    }
}
