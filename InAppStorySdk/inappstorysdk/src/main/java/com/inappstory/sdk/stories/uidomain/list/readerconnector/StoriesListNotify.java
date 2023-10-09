package com.inappstory.sdk.stories.uidomain.list.readerconnector;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.IStoriesListAdapter;
import com.inappstory.sdk.stories.ui.list.StoriesAdapter;
import com.inappstory.sdk.stories.ui.list.StoriesList;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;
import java.util.UUID;

public class StoriesListNotify implements IStoriesListNotify {
    private IStoriesListAdapter storiesListAdapter;
    private int coverQuality;

    private Story.StoryType storyType;

    private String listUniqueId;

    public StoriesListNotify(
            String listUniqueId,
            Story.StoryType storyType,
            int coverQuality
    ) {
        this.coverQuality = coverQuality;
        this.listUniqueId = listUniqueId;
        this.storyType = storyType;
    }


    public void unsubscribe() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.removeListSubscriber(this);
        storiesListAdapter = null;
    }

    @Override
    public void subscribe() {
        InAppStoryService.checkAndAddListSubscriber(this);
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


    public void changeStory(
            final int storyId,
            final Story.StoryType storyType,
            final String listID) {
        checkHandler();
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                if (!listUniqueId.equals(listID)) return;
                if (StoriesListNotify.this.storyType != storyType) return;
                storiesListAdapter.changeStoryEvent(storyId);
            }
        });
    }

    @Override
    public void openStory(int storyId, Story.StoryType storyType, String listID) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (StoriesListNotify.this.storyType != storyType) return;
        Story st = service.getDownloadManager().getStoryById(storyId, storyType);
        if (st == null) return;
        st.isOpened = true;
        st.saveStoryOpened(storyType);

    }

    @Override
    public void closeReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                storiesListAdapter.closeReader();
            }
        });
    }

    @Override
    public void openReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListAdapter == null) return;
                storiesListAdapter.openReader();
            }
        });
    }

    @Override
    public void changeUserId() {
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
    public void storyFavorite(final int id, Story.StoryType storyType, final boolean favStatus, final boolean isEmpty) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (storiesListAdapter == null) return;
        if (StoriesListNotify.this.storyType != storyType) return;
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
                storiesListAdapter.favStory(
                        new StoriesAdapterStoryData(story, coverQuality),
                        favStatus,
                        favImages,
                        isEmpty
                );
            }
        });
    }
}
