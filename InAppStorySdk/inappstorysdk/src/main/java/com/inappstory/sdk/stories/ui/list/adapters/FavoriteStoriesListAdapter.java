package com.inappstory.sdk.stories.ui.list.adapters;

import android.content.Context;
import android.view.View;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IFavoriteListUpdatedCallback;
import com.inappstory.sdk.stories.ui.list.IFavoriteListUpdate;
import com.inappstory.sdk.stories.ui.list.items.BaseStoriesListItem;
import com.inappstory.sdk.stories.ui.list.items.story.StoriesListItem;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListCommonItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListDeeplinkItemClick;
import com.inappstory.sdk.stories.uidomain.list.items.story.IStoriesListGameItemClick;

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
        storiesRepository.addFavoriteListUpdatedCallback(favoriteListUpdatedCallback);
    }

    private final IFavoriteListUpdatedCallback favoriteListUpdatedCallback =
            new IFavoriteListUpdatedCallback() {
                @Override
                public void onUpdate() {
                    updateStoriesData(storiesRepository.getCachedFavorites());
                    refreshList();
                }
            };

    @Override
    public void clearAllFavorites() {
        refreshList();
    }

    @Override
    public BaseStoriesListItem getViewHolderItem(View view, int viewType) {
        return new StoriesListItem(view, manager, (viewType % 5) == 2);
    }

    @Override
    public void favorite(IPreviewStoryDTO data) {
        if (storiesData.contains(data)) return;
        storiesData.add(0, data);
    }

    @Override
    public void removeFromFavorite(int storyId) {
        ListIterator<IPreviewStoryDTO> iterator = storiesData.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == storyId) {
                iterator.remove();
            }
        }
    }
}
