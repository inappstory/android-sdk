package com.inappstory.sdk.externalapi.onboardings;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.CancellationToken;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;

import java.util.List;

public interface IASOnboardingsExternalAPI {
    CancellationToken show(
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
