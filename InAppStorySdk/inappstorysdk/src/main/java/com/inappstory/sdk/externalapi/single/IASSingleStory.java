package com.inappstory.sdk.externalapi.single;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.UseOldManagerInstanceCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public class IASSingleStory {
    public void showOnce(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryOnceCallback callback
    ) {

        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
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
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.showStory(storyId, context, appearanceManager, callback, slide);
            }
        });
    }

    public void loadCallback(SingleLoadCallback singleLoadCallback) {
        CallbackManager.getInstance().setSingleLoadCallback(singleLoadCallback);
    }
}
