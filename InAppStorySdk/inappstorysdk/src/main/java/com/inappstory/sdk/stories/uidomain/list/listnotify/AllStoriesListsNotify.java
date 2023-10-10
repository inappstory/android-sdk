package com.inappstory.sdk.stories.uidomain.list.listnotify;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.IFavoriteCellUpdate;
import com.inappstory.sdk.stories.ui.list.IFavoriteListUpdate;
import com.inappstory.sdk.stories.ui.list.IStoriesListAdapter;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public class AllStoriesListsNotify implements IAllStoriesListsNotify {
    private IStoriesListAdapter storiesListAdapter;
    private int coverQuality;

    private Story.StoryType storyType;


    public AllStoriesListsNotify(
            Story.StoryType storyType,
            int coverQuality
    ) {
        this.coverQuality = coverQuality;
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
    public void storyFavorite(
            final List<FavoriteImage> favoriteImages,
            Story.StoryType storyType,
            final boolean favStatus,
            final boolean isEmpty
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (AllStoriesListsNotify.this.storyType != storyType) return;
        final Story story = service.getDownloadManager().getStoryById(id, storyType);
        if (story == null) return;
        final List<FavoriteImage> favImages = service.getFavoriteImages();
        if (favStatus) {
            FavoriteImage favoriteImage = new FavoriteImage(
                    id,
                    story.getImage(),
                    story.getBackgroundColor()
            );
            if (!favImages.contains(favoriteImage))
                favImages.add(0, favoriteImage);
        } else {
            for (FavoriteImage favoriteImage : favImages) {
                if (favoriteImage.getId() == id) {
                    favImages.remove(favoriteImage);
                    break;
                }
            }
        }
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                if (storiesListAdapter instanceof IFavoriteCellUpdate) {
                    ((IFavoriteCellUpdate) storiesListAdapter).update(
                            favImages,
                            isEmpty
                    );
                } else if (storiesListAdapter instanceof IFavoriteListUpdate) {
                    StoriesAdapterStoryData storyData = new StoriesAdapterStoryData(
                            story,
                            coverQuality
                    );
                    if (favStatus)
                        ((IFavoriteListUpdate) storiesListAdapter).favorite(storyData);
                    else
                        ((IFavoriteListUpdate) storiesListAdapter).removeFromFavorite(storyData);
                }
            }
        });
    }
}
