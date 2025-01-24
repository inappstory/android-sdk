package com.inappstory.sdk.stories.ui.ugclist;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.ui.list.ListManager;

class UgcStoriesListManager implements ListManager {

    UgcStoriesList list;
    private final IASCore core;

    public void clear() {
        list = null;
    }

    public UgcStoriesListManager(IASCore core) {
      //  this.list = list;
        this.core = core;
        handler = new Handler(Looper.getMainLooper());
        checkCurrentSession();
    }

    void checkCurrentSession() {
        currentSessionId = core.sessionManager().getSession().getSessionId();
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
        IListItemContent st = core.contentHolder().listsContent().getByIdAndType(
                storyId, ContentType.UGC
        );
        if (st == null) return;
        st.setOpened(true);
        core.storyListCache().saveStoryOpened(st.id(), ContentType.UGC);
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
                list.refresh();
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
