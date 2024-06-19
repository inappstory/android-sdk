package com.inappstory.sdk.externalapi.onboardings;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.OldInAppStoryManager;
import com.inappstory.sdk.UseOldManagerInstanceCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;

import java.util.List;

public class IASOnboardings {
    public void show(
            final Context context,
            final String feed,
            final AppearanceManager appearanceManager,
            final List<String> tags,
            final int limit
    ) {
        OldInAppStoryManager.useInstance(new UseOldManagerInstanceCallback() {
            @Override
            public void use(@NonNull OldInAppStoryManager manager) throws Exception {
                manager.showOnboardingStories(limit, feed, tags, context, appearanceManager);
            }
        });
    }

    public void loadCallback(OnboardingLoadCallback onboardingLoadCallback) {
        CallbackManager.getInstance().setOnboardingLoadCallback(onboardingLoadCallback);
    }
}
