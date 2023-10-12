package com.inappstory.sdk.stories.uidomain.list.listnotify;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.IFavoriteCellUpdate;
import com.inappstory.sdk.stories.ui.list.IFavoriteListUpdate;
import com.inappstory.sdk.stories.ui.list.adapters.IStoriesListAdapter;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public class AllStoriesListsNotify implements IAllStoriesListsNotify {
    private IStoriesListAdapter storiesListAdapter;
    private int coverQuality;

    private Story.StoryType storyType;


    public AllStoriesListsNotify(
            Story.StoryType storyType
    ) {
        this.storyType = storyType;
    }


    public void unsubscribe() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.removeAllStoriesListsNotify(this);
        storiesListAdapter = null;
    }

    @Override
    public void subscribe() {
        InAppStoryService.checkAndAddAllStoriesListsNotify(this);
    }

    @Override
    public void bindListAdapter(IStoriesListAdapter storiesListAdapter, int coverQuality) {
        this.storiesListAdapter = storiesListAdapter;
        this.coverQuality = coverQuality;
    }

    private void checkHandler() {
        if (handler == null)
            handler = new Handler(Looper.getMainLooper());
    }

    Handler handler = new Handler(Looper.getMainLooper());

    private void post(Runnable runnable) {
        checkHandler();
        handler.post(runnable);
    }


    @Override
    public void openStory(int storyId, Story.StoryType storyType) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (AllStoriesListsNotify.this.storyType != storyType) return;
        Story st = service.getDownloadManager().getStoryById(storyId, storyType);
        if (st == null) return;
        st.isOpened = true;
        st.saveStoryOpened(storyType);
    }

    @Override
    public void refreshList() {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                storiesListAdapter.refreshList();
            }
        });
    }

    @Override
    public void clearAllFavorites() {

        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                storiesListAdapter.clearAllFavorites();
            }
        });
    }

    @Override
    public void storyFavoriteCellNotify(
            final List<FavoriteImage> favoriteImages,
            Story.StoryType storyType,
            final boolean favStatus,
            final boolean isEmpty
    ) {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                if (storiesListAdapter instanceof IFavoriteCellUpdate) {
                    ((IFavoriteCellUpdate) storiesListAdapter).update(
                            favoriteImages,
                            isEmpty
                    );
                }
            }
        });
    }

    @Override
    public void storyAddToFavoriteItemNotify(
            final StoriesAdapterStoryData data,
            final boolean favStatus
    ) {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                if (storiesListAdapter instanceof IFavoriteListUpdate) {
                    if (favStatus)
                        ((IFavoriteListUpdate) storiesListAdapter).favorite(data);
                    else
                        ((IFavoriteListUpdate) storiesListAdapter).removeFromFavorite(data);
                }
            }
        });
    }
}
