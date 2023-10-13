package com.inappstory.sdk.stories.uidomain.list.listnotify;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.IStoriesListNotifyHandler;
import com.inappstory.sdk.stories.ui.list.adapters.IStoriesListAdapter;

public class StoriesListNotify implements IStoriesListNotify {
    private IStoriesListNotifyHandler storiesListNotifyHandler;
    private Story.StoryType storyType;

    private String listUniqueId;

    public StoriesListNotify(
            String listUniqueId,
            Story.StoryType storyType
    ) {
        this.listUniqueId = listUniqueId;
        this.storyType = storyType;
    }


    public void unsubscribe() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.removeStoriesListNotify(this);
        storiesListNotifyHandler = null;
    }

    @Override
    public String getListUID() {
        return listUniqueId;
    }

    @Override
    public void subscribe() {
        InAppStoryService.checkAndAddStoriesListNotify(this);
    }

    @Override
    public void bindList(IStoriesListNotifyHandler storiesListAdapter) {
        this.storiesListNotifyHandler = storiesListAdapter;
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
    public void changeStory(
            final int storyId,
            final Story.StoryType storyType) {
        checkHandler();
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListNotifyHandler == null) return;
                if (StoriesListNotify.this.storyType != storyType) return;
                storiesListNotifyHandler.changeStory(storyId);
            }
        });
    }

    @Override
    public void scrollToLastOpenedStory() {
        checkHandler();
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListNotifyHandler == null) return;
                storiesListNotifyHandler.scrollToLastOpenedStory();
            }
        });
    }

    @Override
    public void closeReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListNotifyHandler == null) return;
                storiesListNotifyHandler.closeReader();
            }
        });
    }

    @Override
    public void openReader() {
        post(new Runnable() {
            @Override
            public void run() {
                if (storiesListNotifyHandler == null) return;
                storiesListNotifyHandler.openReader();
            }
        });
    }
}
