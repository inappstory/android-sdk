package com.inappstory.sdk.stories.outercallbacks.common.onboarding;

import com.inappstory.sdk.core.api.IASCallback;

public interface OnboardingLoadCallback extends IASCallback {
    void onboardingLoadSuccess(int count, String feed);
    void onboardingLoadError(String feed, String reason);
}
