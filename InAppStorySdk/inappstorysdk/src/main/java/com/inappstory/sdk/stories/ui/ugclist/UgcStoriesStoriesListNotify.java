package com.inappstory.sdk.stories.ui.ugclist;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.IStoriesListAdapter;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IStoriesListNotify;

class UgcStoriesStoriesListNotify implements IStoriesListNotify {

    UgcStoriesList list;

    public void unsubscribe() {
        list = null;
    }

    @Override
    public String getListUID() {
        return null;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void bindListAdapter(IStoriesListAdapter storiesListAdapter) {

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
    public void changeStory(final int storyId, final Story.StoryType storyType) {
        checkHandler();
        if (storyType != Story.StoryType.UGC) return;
        post(new Runnable() {
            @Override
            public void run() {
                if (list == null) return;
                if (list.getVisibility() != View.VISIBLE) return;
               // list.changeStoryEvent(storyId);
            }
        });
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
}
