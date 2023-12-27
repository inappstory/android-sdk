package com.inappstory.sdk.stories.ui;

import android.content.Context;

import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.StoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.IBottomPanelViewModel;

public final class IASUICore {
    private static IASUICore INSTANCE;
    private static final Object lock = new Object();

    public static IASUICore getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new IASUICore();
            return INSTANCE;
        }
    }

    public IStoriesReaderViewModel getStoriesReaderVM() {
        return storiesReaderVM;
    }

    public IStoriesReaderPageViewModel getStoriesReaderPageVM(int storyId) {
        if (storiesReaderVM != null) {
            return storiesReaderVM.getPageViewModelByStoryId(storyId);
        }
        return null;
    }

    public IBottomPanelViewModel getStoriesReaderPageBottomPanelVM(int storyId) {
        IStoriesReaderPageViewModel pageViewModel = getStoriesReaderPageVM(storyId);
        if (pageViewModel != null) return pageViewModel.getBottomPanelViewModel();
        return null;
    }

    private IStoriesReaderViewModel storiesReaderVM;

    public void init(Context context) {
        storiesReaderVM = new StoriesReaderViewModel();
    }

    public void clearReaderViewModel() {
        storiesReaderVM.clear();
    }
}
