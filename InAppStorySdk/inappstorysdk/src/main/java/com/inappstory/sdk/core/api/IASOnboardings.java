package com.inappstory.sdk.core.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;

import java.util.List;
import java.util.Map;

public interface IASOnboardings {
    void show(
            CancellationTokenWithStatus cancellationToken,
            Context context,
            String feed,
            AppearanceManager appearanceManager,
            List<String> tags,
            int limit
    );

    void loadCallback(
            OnboardingLoadCallback onboardingLoadCallback
    );
}
