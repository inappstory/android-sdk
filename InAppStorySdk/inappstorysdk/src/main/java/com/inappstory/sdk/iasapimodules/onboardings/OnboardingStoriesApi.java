package com.inappstory.sdk.iasapimodules.onboardings;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.iasapimodules.NotImplementedYetException;
import com.inappstory.sdk.iasapimodules.settings.ISettingsProviderApi;

import java.util.List;

public class OnboardingStoriesApi implements IOnboardingStoriesApi {
    public OnboardingStoriesApi(ISettingsProviderApi settingsProviderApi) {
        this.settingsProviderApi = settingsProviderApi;
    }

    private final ISettingsProviderApi settingsProviderApi;

    private final static String ONBOARDING_FEED = "onboarding";

    @Override
    public void showOnboardingStories(Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(-1, ONBOARDING_FEED, settingsProviderApi.getTags(), context, manager);
    }

    @Override
    public void showOnboardingStories(List<String> tags, Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(-1, ONBOARDING_FEED, tags, context, manager);
    }

    @Override
    public void showOnboardingStories(String feed, Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(-1, feed, settingsProviderApi.getTags(), context, manager);
    }

    @Override
    public void showOnboardingStories(String feed, List<String> tags, Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(-1, feed, tags, context, manager);
    }


    @Override
    public void showOnboardingStories(int limit, Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(limit, ONBOARDING_FEED, settingsProviderApi.getTags(), context, manager);
    }

    @Override
    public void showOnboardingStories(int limit, List<String> tags, Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(limit, ONBOARDING_FEED, tags, context, manager);
    }

    @Override
    public void showOnboardingStories(int limit, String feed, Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(limit, feed, settingsProviderApi.getTags(), context, manager);
    }

    @Override
    public void showOnboardingStories(int limit, String feed, List<String> tags, Context context, AppearanceManager manager) {
        showOnboardingStoriesInner(limit, feed, tags, context, manager);
    }

    private String checkAndCorrectFeed(String feed) {
        if (feed == null || feed.isEmpty()) return ONBOARDING_FEED;
        return feed;
    }

    private void showOnboardingStoriesInner(
            final int limit,
            String nonCheckedFeed,
            final List<String> tags,
            final Context context,
            final AppearanceManager manager
    ) {
        String feed = checkAndCorrectFeed(nonCheckedFeed);
        if (feed == null)
            return;
        if (!settingsProviderApi.userIdOrDeviceIdIsCorrect())
            return;
        if (!settingsProviderApi.tagsIsCorrect(tags))
            return;
        throw new NotImplementedYetException();
    }
}
