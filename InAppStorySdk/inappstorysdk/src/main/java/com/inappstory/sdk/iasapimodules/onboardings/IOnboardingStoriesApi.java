package com.inappstory.sdk.iasapimodules.onboardings;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;

import java.util.List;

public interface IOnboardingStoriesApi {
    void showOnboardingStories(
            Context context,
            AppearanceManager manager
    );

    void showOnboardingStories(
            List<String> tags,
            Context context,
            AppearanceManager manager
    );

    void showOnboardingStories(
            String feed,
            Context context,
            AppearanceManager manager
    );

    void showOnboardingStories(
            String feed,
            List<String> tags,
            Context context,
            AppearanceManager manager
    );

    void showOnboardingStories(
            int limit,
            Context context,
            AppearanceManager manager
    );

    void showOnboardingStories(
            int limit,
            List<String> tags,
            Context context,
            AppearanceManager manager
    );

    void showOnboardingStories(
            int limit,
            String feed,
            Context context,
            AppearanceManager manager
    );

    void showOnboardingStories(
            int limit,
            String feed,
            List<String> tags,
            Context context,
            AppearanceManager manager
    );
}
