package com.inappstory.sdk.stories.ui;

import android.content.Context;

import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.StoriesReaderViewModel;

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

    IStoriesReaderViewModel storiesReaderVM;

    public void init(Context context) {
        storiesReaderVM = new StoriesReaderViewModel();
    }
}
