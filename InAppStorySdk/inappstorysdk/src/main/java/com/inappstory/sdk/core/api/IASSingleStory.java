package com.inappstory.sdk.core.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public interface IASSingleStory {
    void showOnce(
            CancellationTokenWithStatus cancellationToken,
            Context context,
            String storyId,
            AppearanceManager appearanceManager,
            IShowStoryOnceCallback callback
    );

    void show(
            CancellationTokenWithStatus cancellationToken,
            Context context,
            String storyId,
            AppearanceManager appearanceManager,
            IShowStoryCallback callback,
            Integer slide
    );

    void loadCallback(SingleLoadCallback singleLoadCallback);
}
