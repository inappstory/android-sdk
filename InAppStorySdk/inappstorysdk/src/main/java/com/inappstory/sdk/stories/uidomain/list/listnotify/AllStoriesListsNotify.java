package com.inappstory.sdk.stories.uidomain.list.listnotify;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.list.adapters.IStoriesListAdapter;

public class AllStoriesListsNotify implements IAllStoriesListsNotify {
    private IStoriesListAdapter storiesListAdapter;

    private Story.StoryType storyType;

    private ChangeUserIdListNotify changeUserIdListNotify;


    public AllStoriesListsNotify(
            Story.StoryType storyType,
            ChangeUserIdListNotify changeUserIdListNotify
    ) {
        this.storyType = storyType;
        this.changeUserIdListNotify = changeUserIdListNotify;
    }


    public void unsubscribe() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.removeAllStoriesListsNotify(this);
        storiesListAdapter = null;
    }

    @Override
    public void subscribe() {
        InAppStoryService.checkAndAddAllStoriesListsNotify(this);
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


    @Override
    public void changeUserId() {
        post(new Runnable() {
            @Override
            public void run() {
                if (changeUserIdListNotify == null) return;
                changeUserIdListNotify.onChange();
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
}
