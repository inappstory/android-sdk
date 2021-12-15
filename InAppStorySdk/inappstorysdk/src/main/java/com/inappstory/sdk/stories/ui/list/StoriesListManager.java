package com.inappstory.sdk.stories.ui.list;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.List;

public class StoriesListManager {
    StoriesList list;

    public void clear() {
        list = null;
    }

    public StoriesListManager(StoriesList list) {
        this.list = list;
        handler = new Handler(Looper.getMainLooper());
    }

    private void checkHandler() {
        if (handler == null)
            handler = new Handler(Looper.getMainLooper());
    }

    Handler handler;

    private void post(Runnable runnable) {
        checkHandler();
        handler.post(runnable);
    }

    public void changeStory(final int storyId) {
        if (InAppStoryService.isNull()) return;
        Story st = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (st == null) return;
        st.isOpened = true;
        st.saveStoryOpened();
        checkHandler();
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                if (list.getVisibility() != View.VISIBLE) return;
                list.changeStoryEvent(storyId);
            }
        });
    }

    //CloseReaderEvent
    public void closeReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.closeReader();
            }
        });
    }

    public void openReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.openReader();
            }
        });
    }

    public void changeUserId() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.refreshList();
            }
        });
    }

    //StoryFavoriteEvent
    public void storyFavorite(final int id, final boolean favStatus) {
        post(new Runnable() {
            @Override
            public void run() {
                if (InAppStoryService.isNull()) return;
                List<FavoriteImage> favImages = InAppStoryService.getInstance().getFavoriteImages();
                boolean isEmpty = favImages.isEmpty();
                Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(id);
                if (favStatus) {
                    FavoriteImage favoriteImage = new FavoriteImage(id, story.getImage(), story.getBackgroundColor());
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
                if (list == null) return;
                if (list.getVisibility() != View.VISIBLE) return;
                list.favStory(id, favStatus, favImages, isEmpty);
            }
        });
    }
}
