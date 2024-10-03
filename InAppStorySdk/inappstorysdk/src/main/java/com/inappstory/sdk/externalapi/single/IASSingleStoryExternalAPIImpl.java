package com.inappstory.sdk.externalapi.single;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public class IASSingleStoryExternalAPIImpl implements IASSingleStory {
    public void showOnce(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryOnceCallback callback
    ) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.singleStoryAPI().showOnce(context, storyId, appearanceManager, callback);
            }
        });
    }

    @Override
    public void show(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryCallback callback,
            final Integer slide
    ) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.singleStoryAPI().show(context, storyId, appearanceManager, callback, slide);
            }
        });
    }

    public void loadCallback(final SingleLoadCallback singleLoadCallback) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.singleStoryAPI().loadCallback(singleLoadCallback);
            }
        });
    }
}
