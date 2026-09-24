package com.inappstory.sdk.core.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

import java.util.List;
import java.util.Map;

public interface IASSingleStory {
    void showOnce(
            CancellationTokenWithStatus cancellationToken,
            Context context,
            String storyId,
            boolean useTargeting,
            List<String> tags,
            AppearanceManager appearanceManager,
            IShowStoryOnceCallback callback
    );

    void show(
            CancellationTokenWithStatus cancellationToken,
            Context context,
            String storyId,
            boolean useTargeting,
            List<String> tags,
            AppearanceManager appearanceManager,
            IShowStoryCallback callback,
            Integer slide
    );

    void loadCallback(SingleLoadCallback singleLoadCallback);
}
