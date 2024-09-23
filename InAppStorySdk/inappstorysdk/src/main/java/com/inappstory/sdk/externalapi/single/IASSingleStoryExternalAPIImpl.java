package com.inappstory.sdk.externalapi.single;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
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

        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.showStoryOnce(storyId, context, appearanceManager, callback);
            }
        });
    }

    public void show(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryCallback callback,
            final Integer slide
    ) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.showStory(storyId, context, appearanceManager, callback, slide);
            }
        });
    }

    public void loadCallback(SingleLoadCallback singleLoadCallback) {
        CallbackManager.getInstance().setSingleLoadCallback(singleLoadCallback);
    }
}
