package com.inappstory.sdk.externalapi;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASFavorites;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.core.api.IASManager;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.core.api.IASStoryList;
import com.inappstory.sdk.externalapi.callbacks.IASCallbacksExternalAPI;
import com.inappstory.sdk.externalapi.callbacks.IASCallbacksExternalAPIImpl;
import com.inappstory.sdk.externalapi.favorites.IASFavoritesExternalAPIImpl;
import com.inappstory.sdk.externalapi.games.IASGamesExternalAPIImpl;
import com.inappstory.sdk.externalapi.iasmanager.IASManagerExternalAPIImpl;
import com.inappstory.sdk.externalapi.inappmessage.IASInAppMessageExternalAPI;
import com.inappstory.sdk.externalapi.inappmessage.IASInAppMessageExternalAPIImpl;
import com.inappstory.sdk.externalapi.logger.IASLogger;
import com.inappstory.sdk.externalapi.onboardings.IASOnboardingsExternalAPI;
import com.inappstory.sdk.externalapi.onboardings.IASOnboardingsExternalAPIImpl;
import com.inappstory.sdk.externalapi.settings.IASSettingsExternalAPIImpl;
import com.inappstory.sdk.externalapi.single.IASSingleStoryExternalAPI;
import com.inappstory.sdk.externalapi.single.IASSingleStoryExternalAPIImpl;
import com.inappstory.sdk.externalapi.stackfeed.IASStackFeedExternalAPIImpl;
import com.inappstory.sdk.externalapi.storylist.IASStoryListExternalAPIImpl;
import com.inappstory.sdk.externalapi.subscribers.IAPISubscriber;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;

public class InAppStoryAPI {
    public IASFavorites favorites = new IASFavoritesExternalAPIImpl();
    public IASCallbacksExternalAPI callbacks = new IASCallbacksExternalAPIImpl();
    public IASGames games = new IASGamesExternalAPIImpl();
    public IASManager inAppStoryManager = new IASManagerExternalAPIImpl();
    public IASDataSettings settings = new IASSettingsExternalAPIImpl();
    public IASSingleStoryExternalAPI singleStory = new IASSingleStoryExternalAPIImpl();
    public IASOnboardingsExternalAPI onboardings = new IASOnboardingsExternalAPIImpl();
    public IASStackFeed stackFeed = new IASStackFeedExternalAPIImpl();
    public IASStoryList storyList = new IASStoryListExternalAPIImpl();
    public IASInAppMessageExternalAPI inAppMessage = new IASInAppMessageExternalAPIImpl();
    public IASLogger logger;

    public void setExternalPlatform(final ExternalPlatforms externalPlatform) {
        this.externalPlatform = externalPlatform;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().agentPrefix(externalPlatform.getPrefix());
            }
        });
    }

    private ExternalPlatforms externalPlatform = ExternalPlatforms.NONE;

    public void addSubscriber(final IAPISubscriber subscriber) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppStoryService().getApiSubscribersManager().addAPISubscriber(subscriber);
            }
        });
    }

    public void closeReaders(final ForceCloseReaderCallback callback) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().forceCloseAllReaders(callback);
            }
        });
    }

    public void removeSubscriber(final String uniqueKey) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppStoryService().getApiSubscribersManager().removeAPISubscriber(uniqueKey);
            }
        });
    }

    public void removeSubscriber(final IAPISubscriber subscriber) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppStoryService().getApiSubscribersManager().removeAPISubscriber(subscriber);
            }
        });
    }

    public void init(Context context) {
        InAppStoryManager.initSDK(context);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().agentPrefix(externalPlatform.getPrefix());
            }
        });
        InAppStoryManager.logger = new InAppStoryManager.IASLogger() {
            @Override
            public void showELog(String tag, String message) {
                if (logger != null) {
                    logger.errorLog("TAG: " + tag + "; Message: " + message);
                }
            }

            @Override
            public void showDLog(String tag, String message) {
                if (logger != null) {
                    logger.debugLog("TAG: " + tag + "; Message: " + message);
                }
            }
        };
    }

    public void logger(IASLogger logger) {
        this.logger = logger;
    }


    public IASSDKVersion getVersion() {
        Pair<String, Integer> version = InAppStoryManager.getLibraryVersion();
        return new IASSDKVersion(version.first, "1.0.1", version.second);
    }


    public void clearCache() {
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.contentLoader().clearCache();
                core.inAppStoryService().getApiSubscribersManager().clearCache();
            }
        });
    }
}
