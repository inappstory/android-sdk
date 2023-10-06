package com.inappstory.sdk.stories.ui.ugclist;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.StoriesList;
import com.inappstory.sdk.stories.uidomain.list.readerconnector.IStoriesListNotify;

class UgcStoriesStoriesListNotify implements IStoriesListNotify {

    UgcStoriesList list;

    public void unsubscribe() {
        list = null;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void bindList(StoriesList list) {

    }

    public UgcStoriesStoriesListNotify() {
        //  this.list = list;
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

    @Override
    public void changeStory(final int storyId, final String listID) {
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

    @Override
    public void openStory(int storyId, String listID) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        Story st = service.getDownloadManager().getStoryById(storyId, Story.StoryType.UGC);
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

    }

    @Override
    public void storyFavorite(final int id, final boolean favStatus, final boolean isEmpty) {

    }
}
