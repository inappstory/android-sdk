package com.inappstory.sdk.stories.outercallbacks.common.onboarding;

public interface OnboardingLoadCallback {
    void onboardingLoadSuccess(int count, String feed);
    void onboardingLoadError(String feed, String reason);
}
