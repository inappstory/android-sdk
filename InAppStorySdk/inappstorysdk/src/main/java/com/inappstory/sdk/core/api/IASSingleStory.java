package com.inappstory.sdk.core.api;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public interface IASSingleStory {
    void showOnce(
            Context context,
            String storyId,
            AppearanceManager appearanceManager,
            IShowStoryOnceCallback callback
    );

    void show(
            Context context,
            String storyId,
            AppearanceManager appearanceManager,
            IShowStoryCallback callback,
            Integer slide,
            boolean openedFromReader
    );

    void loadCallback(SingleLoadCallback singleLoadCallback);
}
