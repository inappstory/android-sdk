package com.inappstory.sdk.stories.uidomain.list.listnotify;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.IFavoriteCellUpdate;
import com.inappstory.sdk.stories.ui.list.IFavoriteListUpdate;
import com.inappstory.sdk.stories.ui.list.adapters.IStoriesListAdapter;
import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

import java.util.List;

public class AllStoriesListsNotify implements IAllStoriesListsNotify {
    private IStoriesListAdapter storiesListAdapter;

    private Story.StoryType storyType;

    private ChangeUserIdListNotify changeUserIdListNotify;


    public AllStoriesListsNotify(
            Story.StoryType storyType,
            ChangeUserIdListNotify changeUserIdListNotify
    ) {
        this.storyType = storyType;
        this.changeUserIdListNotify = changeUserIdListNotify;
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
    public void bindListAdapter(IStoriesListAdapter storiesListAdapter) {
        this.storiesListAdapter = storiesListAdapter;
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
        if (AllStoriesListsNotify.this.storyType != storyType) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        final Story st = service.openStory(storyId, storyType);
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                storiesListAdapter.notify(new PreviewStoryDTO(st));
            }
        });
    }

    @Override
    public void changeUserId() {
        post(new Runnable() {
            @Override
            public void run() {
                if (changeUserIdListNotify == null) return;
                changeUserIdListNotify.onChange();
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
    public void storyAddToFavoriteItemNotify(final PreviewStoryDTO data) {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                if (storiesListAdapter instanceof IFavoriteListUpdate) {
                    ((IFavoriteListUpdate) storiesListAdapter).favorite(data);

                }
            }
        });
    }

    @Override
    public void storyRemoveFromFavoriteItemNotify(final int storyId) {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                if (storiesListAdapter instanceof IFavoriteListUpdate) {
                    ((IFavoriteListUpdate) storiesListAdapter).removeFromFavorite(storyId);
                }
            }
        });
    }
}
