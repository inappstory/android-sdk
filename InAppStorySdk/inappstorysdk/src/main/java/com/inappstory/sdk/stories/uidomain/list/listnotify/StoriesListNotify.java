package com.inappstory.sdk.stories.uidomain.list.listnotify;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.IStoriesListAdapter;
import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public class StoriesListNotify implements IStoriesListNotify {
    private IStoriesListAdapter storiesListAdapter;
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
        storiesListAdapter = null;
    }

    @Override
    public void subscribe() {
        InAppStoryService.checkAndAddStoriesListNotify(this);
    }

    @Override
    public void bindListAdapter(IStoriesListAdapter storiesListAdapter) {
        this.storiesListAdapter = storiesListAdapter;
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
}
