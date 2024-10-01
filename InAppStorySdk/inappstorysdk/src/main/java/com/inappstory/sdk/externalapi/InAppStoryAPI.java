package com.inappstory.sdk.externalapi;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.IASFavorites;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.core.api.IASManager;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.core.api.IASStoryList;
import com.inappstory.sdk.externalapi.callbacks.IASCallbacksExternalAPIImpl;
import com.inappstory.sdk.externalapi.favorites.IASFavoritesExternalAPIImpl;
import com.inappstory.sdk.externalapi.games.IASGamesExternalAPIImpl;
import com.inappstory.sdk.externalapi.iasmanager.IASManagerExternalAPIImpl;
import com.inappstory.sdk.externalapi.onboardings.IASOnboardingsExternalAPIImpl;
import com.inappstory.sdk.externalapi.settings.IASSettingsExternalAPIImpl;
import com.inappstory.sdk.externalapi.single.IASSingleStoryExternalAPIImpl;
import com.inappstory.sdk.externalapi.stackfeed.IASStackFeedExternalAPIImpl;
import com.inappstory.sdk.externalapi.storylist.IASStoryListExternalAPIImpl;
import com.inappstory.sdk.externalapi.subscribers.IAPISubscriber;

public class InAppStoryAPI {
    public IASFavorites favorites = new IASFavoritesExternalAPIImpl();
    public IASCallbacks callbacks = new IASCallbacksExternalAPIImpl();
    public IASGames games = new IASGamesExternalAPIImpl();
    public IASManager inAppStoryManager = new IASManagerExternalAPIImpl();
    public IASDataSettings settings = new IASSettingsExternalAPIImpl();
    public IASSingleStory singleStory = new IASSingleStoryExternalAPIImpl();
    public IASOnboardings onboardings = new IASOnboardingsExternalAPIImpl();
    public IASStackFeed stackFeed = new IASStackFeedExternalAPIImpl();
    public IASStoryList storyList = new IASStoryListExternalAPIImpl();


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
        return new IASSDKVersion(version.first, "1.0.0-rc2", version.second);
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
