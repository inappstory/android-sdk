package com.inappstory.sdk.externalapi.onboardings;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.CancellationToken;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.CancellationTokenImpl;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;

import java.util.List;

public class IASOnboardingsExternalAPIImpl implements IASOnboardingsExternalAPI {
    public CancellationToken show(
            final Context context,
            final String feed,
            final AppearanceManager appearanceManager,
            final List<String> tags,
            final int limit
    ) {
        final CancellationTokenWithStatus token =
                new CancellationTokenImpl("External Onboardings feed: " + feed);
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.cancellationTokenPool().addToken(token);
                core.onboardingsAPI().show(
                        token,
                        context,
                        feed,
                        appearanceManager,
                        tags,
                        limit
                );
            }
        });
        return token;
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
