package com.inappstory.sdk.externalapi.single;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.CancellationToken;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.CancellationTokenImpl;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public class IASSingleStoryExternalAPIImpl implements IASSingleStoryExternalAPI {
    @Override
    public CancellationToken showOnce(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryOnceCallback callback
    ) {
        final CancellationTokenWithStatus token = new CancellationTokenImpl("External Single once id: " + storyId);
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.cancellationTokenPool().addToken(token);
                core.singleStoryAPI().showOnce(token, context, storyId, appearanceManager, callback);
            }
        });
        return token;
    }

    @Override
    public CancellationToken show(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryCallback callback,
            final Integer slide
    ) {
        final CancellationTokenWithStatus token = new CancellationTokenImpl("External Single id: " + storyId);
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.cancellationTokenPool().addToken(token);
                core.singleStoryAPI().show(token, context, storyId, appearanceManager, callback, slide);
            }
        });
        return token;
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
