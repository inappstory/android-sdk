package com.inappstory.sdk.externalapi;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.externalapi.callbacks.IASCallbacks;
import com.inappstory.sdk.externalapi.favorites.IASFavorites;
import com.inappstory.sdk.externalapi.games.IASGames;
import com.inappstory.sdk.externalapi.iasmanager.IASManager;
import com.inappstory.sdk.externalapi.onboardings.IASOnboardings;
import com.inappstory.sdk.externalapi.settings.IASSettings;
import com.inappstory.sdk.externalapi.single.IASSingleStory;
import com.inappstory.sdk.externalapi.stackfeed.IASStackFeed;
import com.inappstory.sdk.externalapi.storylist.IASStoryList;
import com.inappstory.sdk.externalapi.subscribers.IAPISubscriber;

public class InAppStoryAPI {
    public IASFavorites favorites = new IASFavorites();
    public IASCallbacks callbacks = new IASCallbacks();
    public IASGames games = new IASGames();
    public IASManager inAppStoryManager = new IASManager();
    public IASSettings settings = new IASSettings();
    public IASSingleStory singleStory = new IASSingleStory();
    public IASOnboardings onboardings = new IASOnboardings();
    public IASStackFeed stackFeed = new IASStackFeed();
    public IASStoryList storyList = new IASStoryList();


    public void addSubscriber(final IAPISubscriber subscriber) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getApiSubscribersManager().addAPISubscriber(subscriber);
            }
        });
    }

    public void removeSubscriber(final String uniqueKey) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getApiSubscribersManager().removeAPISubscriber(uniqueKey);
            }
        });
    }

    public void removeSubscriber(final IAPISubscriber subscriber) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getApiSubscribersManager().removeAPISubscriber(subscriber);
            }
        });
    }

    public void init(Context context) {
        InAppStoryManager.initSDK(context);
    }

    public IASSDKVersion getVersion() {
        Pair<String, Integer> version = InAppStoryManager.getLibraryVersion();
        return new IASSDKVersion(version.first, "1.0.0-rc1", version.second);
    }

    public void clearCache() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) throws Exception {
                manager.clearCache();
            }
        });
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getApiSubscribersManager().clearCache();
            }
        });
    }
}
