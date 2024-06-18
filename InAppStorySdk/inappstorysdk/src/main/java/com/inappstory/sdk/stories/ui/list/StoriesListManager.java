package com.inappstory.sdk.stories.ui.list;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.List;

public class StoriesListManager implements ListManager {
    StoriesList list;
    String currentSessionId;

    void checkCurrentSession() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                currentSessionId = service.getSession().getSessionId();
            }
        });
    }

    public void clear() {
        list = null;
    }

    public StoriesListManager() {
      //  this.list = list;
        handler = new Handler(Looper.getMainLooper());
        checkCurrentSession();
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


    public void changeStory(final int storyId, final String listID) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                Story st = service.getStoryDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
                if (st == null) return;
                st.isOpened = true;
                st.saveStoryOpened(Story.StoryType.COMMON);
                checkHandler();
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null) return;
                        if (list.getVisibility() != View.VISIBLE) return;
                        list.changeStoryEvent(storyId, listID);
                    }
                });
            }
        });
    }

    //CloseReaderEvent
    public void readerIsClosed() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.closeReader();
            }
        });
    }

    public void readerIsOpened() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.openReader();
            }
        });
    }

    public void userIdChanged() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.refreshList();
            }
        });
    }

    @Override
    public void sessionIsOpened(String currentSessionId) {
        this.currentSessionId = currentSessionId;
    }

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

    //StoryFavoriteEvent
    public void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty) {
        post(new Runnable() {
            @Override
            public void run() {
                InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                    @Override
                    public void use(@NonNull InAppStoryService service) throws Exception {
                        List<FavoriteImage> favImages = service.getFavoriteImages();
                        Story story = service.getStoryDownloadManager().getStoryById(id, Story.StoryType.COMMON);
                        if (story == null) return;
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
        });
    }
}
