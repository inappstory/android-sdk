package com.inappstory.sdk.externalapi.onboardings;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;

import java.util.List;

public class IASOnboardingsExternalAPIImpl implements IASOnboardings {
    public void show(
            final Context context,
            final String feed,
            final AppearanceManager appearanceManager,
            final List<String> tags,
            final int limit
    ) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.onboardingsAPI().show(
                        context,
                        feed,
                        appearanceManager,
                        tags,
                        limit
                );
            }
        });
    }

    public void loadCallback(final OnboardingLoadCallback onboardingLoadCallback) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.onboardingsAPI().loadCallback(onboardingLoadCallback);
            }
        });
    }
}
