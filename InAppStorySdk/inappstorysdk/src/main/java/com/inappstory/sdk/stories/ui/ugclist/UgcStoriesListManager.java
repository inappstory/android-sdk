package com.inappstory.sdk.stories.ui.ugclist;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.ListManager;

class UgcStoriesListManager implements ListManager {

    UgcStoriesList list;

    public void clear() {
        list = null;
    }

    public UgcStoriesListManager() {
      //  this.list = list;
        handler = new Handler(Looper.getMainLooper());
        checkCurrentSession();
    }

    void checkCurrentSession() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                currentSessionId = service.getSession().getSessionId();
            }
        });
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
        if (InAppStoryService.isNull()) {
            return;
        }
        Story st = InAppStoryService.getInstance().getStoryDownloadManager().getStoryById(storyId, Story.StoryType.UGC);
        if (st == null) return;
        st.isOpened = true;
        st.saveStoryOpened(Story.StoryType.UGC);
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

    public void userIdChanged() {
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                list.refreshList();
            }
        });
    }

    String currentSessionId;

    @Override
    public void sessionIsOpened(String currentSessionId) {
        this.currentSessionId = currentSessionId;
    }

    public void clearAllFavorites() {

    }

    //StoryFavoriteEvent
    public void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty) {

    }
}
