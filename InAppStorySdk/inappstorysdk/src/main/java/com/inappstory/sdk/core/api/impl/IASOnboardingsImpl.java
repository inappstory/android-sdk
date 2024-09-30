package com.inappstory.sdk.core.api.impl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;

import java.util.List;

public class IASOnboardingsImpl implements IASOnboardings {
    private final IASCore core;

    public IASOnboardingsImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void show(
            Context context,
            String feed,
            AppearanceManager appearanceManager,
            List<String> tags,
            int limit
    ) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.showOnboardingStories(limit, feed, tags, context, appearanceManager);
            }
        });
    }

    @Override
    public void loadCallback(OnboardingLoadCallback onboardingLoadCallback) {
        IASCallbacksImpl iasCallbacks = (IASCallbacksImpl) core.callbacksAPI();
        iasCallbacks.onboardingLoad(onboardingLoadCallback);
    }
}
