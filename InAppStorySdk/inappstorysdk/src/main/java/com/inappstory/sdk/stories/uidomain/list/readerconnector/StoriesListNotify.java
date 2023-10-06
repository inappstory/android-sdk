package com.inappstory.sdk.stories.uidomain.list.readerconnector;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.StoriesList;

import java.util.List;
import java.util.UUID;

public class StoriesListNotify implements IStoriesListNotify {
    public StoriesList list;

    public void unsubscribe() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.removeListSubscriber(this);
        list = null;
    }

    @Override
    public void subscribe() {
        InAppStoryService.checkAndAddListSubscriber(this);
    }

    @Override
    public void bindList(StoriesList list) {
        this.list = list;
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


    public void changeStory(final int storyId, final String listID) {
        checkHandler();
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                if (!list.getUniqueID().equals(listID)) return;
                if (list.getVisibility() != View.VISIBLE) return;
                list.changeStoryEvent(storyId);
            }
        });
    }

    @Override
    public void openStory(int storyId, String listID) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        Story st = service.getDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
        if (st == null) return;
        st.isOpened = true;
        st.saveStoryOpened(Story.StoryType.COMMON);
    }

    @Override
    public void closeReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.closeReader();
            }
        });
    }

    @Override
    public void openReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.openReader();
            }
        });
    }

    @Override
    public void changeUserId() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.refreshList();
            }
        });
    }

    @Override
    public void clearAllFavorites() {

        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                if (list.getVisibility() != View.VISIBLE) return;
                list.clearAllFavorites();
            }
        });
    }

    @Override
    public void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (list == null) return;
        Story story = service.getDownloadManager().getStoryById(id, Story.StoryType.COMMON);
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
                if (list == null) return;
                if (list.getVisibility() != View.VISIBLE) return;
                list.favStory(id, favStatus, favImages, isEmpty);
            }
        });
    }
}
