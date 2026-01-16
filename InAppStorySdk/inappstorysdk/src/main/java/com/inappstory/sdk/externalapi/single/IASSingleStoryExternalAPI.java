package com.inappstory.sdk.externalapi.single;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.CancellationToken;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public interface IASSingleStoryExternalAPI {
    CancellationToken showOnce(
            Context context,
            String storyId,
            AppearanceManager appearanceManager,
            IShowStoryOnceCallback callback
    );

    CancellationToken show(
            Context context,
            String storyId,
            AppearanceManager appearanceManager,
            IShowStoryCallback callback,
            Integer slide
    );

    void loadCallback(SingleLoadCallback singleLoadCallback);
}
