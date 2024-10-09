package com.inappstory.sdk.core;

import android.content.Context;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.IASContentLoader;
import com.inappstory.sdk.core.api.IASContentPreload;
import com.inappstory.sdk.core.api.IASExternalUtilsAPI;
import com.inappstory.sdk.core.api.IASFavorites;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.core.api.IASLogs;
import com.inappstory.sdk.core.api.IASManager;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASSessionAssetsHolder;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.core.api.IASStatistic;
import com.inappstory.sdk.core.api.IASStoryList;
import com.inappstory.sdk.core.api.IASStoriesOpenedCache;
import com.inappstory.sdk.core.api.impl.IASCallbacksImpl;
import com.inappstory.sdk.core.api.impl.IASContentLoaderImpl;
import com.inappstory.sdk.core.api.impl.IASContentPreloadImpl;
import com.inappstory.sdk.core.api.impl.IASExternalUtilsAPIImpl;
import com.inappstory.sdk.core.api.impl.IASFavoritesImpl;
import com.inappstory.sdk.core.api.impl.IASGamesImpl;
import com.inappstory.sdk.core.api.impl.IASLogsImpl;
import com.inappstory.sdk.core.api.impl.IASManagerImpl;
import com.inappstory.sdk.core.api.impl.IASOnboardingsImpl;
import com.inappstory.sdk.core.api.impl.IASSettingsImpl;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.api.impl.IASStackFeedImpl;
import com.inappstory.sdk.core.api.impl.IASStatisticImpl;
import com.inappstory.sdk.core.api.impl.IASStoriesOpenedCacheImpl;
import com.inappstory.sdk.core.dataholders.IStoriesListVMHolder;
import com.inappstory.sdk.core.dataholders.StoriesListVMHolder;
import com.inappstory.sdk.core.ui.screens.ScreensManager;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.IVibrateUtils;
import com.inappstory.sdk.utils.VibrateUtils;

public class IASCoreImpl implements IASCore {
    private final IASCallbacks callbacks;
    private final IASFavorites favorites;
    private final IASGames games;
    private final IASManager manager;
    private final IASOnboardings onboardings;
    private final IASDataSettings settings;
    private final IASSingleStory singleStory;
    private final IASStackFeed stackFeed;
    private final IStoriesListVMHolder storiesListVMHolder;
    private final ScreensManager screensManager;
    private final SessionManager sessionManager;
    private final IASStoriesOpenedCache storyListCache;
    private final IASStatistic statistic;
    private final IVibrateUtils vibrateUtils;
    private final IASContentPreload contentPreload;
    private final IASExternalUtilsAPI externalUtilsAPI;
    private final IASContentLoader contentLoader;
    private final IASLogs iasLogs;
    private final Context context;
    private final InAppStoryService inAppStoryService;
    private final NetworkClient networkClient;
    private final KeyValueStorage keyValueStorage;
    private final SharedPreferencesAPI sharedPreferencesAPI;

    public IASCoreImpl(Context context) {
        this.context = context;
        this.sharedPreferencesAPI = new SharedPreferencesAPI(this);
        keyValueStorage = new KeyValueStorage(this);
        networkClient = new NetworkClient(this);
        callbacks = new IASCallbacksImpl(this);
        favorites = new IASFavoritesImpl(this);
        games = new IASGamesImpl(this);
        manager = new IASManagerImpl(this);
        onboardings = new IASOnboardingsImpl(this);
        settings = new IASSettingsImpl(this);
        singleStory = new IASSingleStoryImpl(this);
        stackFeed = new IASStackFeedImpl(this);
        storiesListVMHolder = new StoriesListVMHolder(this);
        screensManager = new ScreensManager(this);
        sessionManager = new SessionManager(this);
        storyListCache = new IASStoriesOpenedCacheImpl(this);
        statistic = new IASStatisticImpl(this);
        vibrateUtils = new VibrateUtils();
        contentPreload = new IASContentPreloadImpl(this);
        externalUtilsAPI = new IASExternalUtilsAPIImpl();
        contentLoader = new IASContentLoaderImpl(this);
        iasLogs = new IASLogsImpl(this);
        inAppStoryService = new InAppStoryService(this);
        externalUtilsAPI.init();
    }


    @Override
    public Context appContext() {
        return context;
    }

    @Override
    public IASCallbacks callbacksAPI() {
        return callbacks;
    }

    @Override
    public IASFavorites favoritesAPI() {
        return favorites;
    }

    @Override
    public IASGames gamesAPI() {
        return games;
    }

    @Override
    public IASManager managerAPI() {
        return manager;
    }

    @Override
    public IASOnboardings onboardingsAPI() {
        return onboardings;
    }

    @Override
    public IASDataSettings settingsAPI() {
        return settings;
    }

    @Override
    public IASSingleStory singleStoryAPI() {
        return singleStory;
    }

    @Override
    public IASStackFeed stackFeedAPI() {
        return stackFeed;
    }

    @Override
    public IASStoryList storyListAPI() {
        return null;
    }

    @Override
    public IStoriesListVMHolder storiesListVMHolder() {
        return storiesListVMHolder;
    }

    @Override
    public IASStoriesOpenedCache storyListCache() {
        return storyListCache;
    }

    @Override
    public ScreensManager screensManager() {
        return screensManager;
    }

    @Override
    public SessionManager sessionManager() {
        return sessionManager;
    }

    @Override
    public IASStatistic statistic() {
        return statistic;
    }

    @Override
    public IVibrateUtils vibrateUtils() {
        return vibrateUtils;
    }

    @Override
    public IASContentPreload contentPreload() {
        return contentPreload;
    }

    @Override
    public IASSessionAssetsHolder sessionAssets() {
        return null;
    }

    @Override
    public IASExternalUtilsAPI externalUtilsAPI() {
        return externalUtilsAPI;
    }

    @Override
    public IASContentLoader contentLoader() {
        return contentLoader;
    }

    @Override
    public NetworkClient network() {
        return networkClient;
    }

    @Override
    public IASLogs logs() {
        return iasLogs;
    }

    @Override
    public KeyValueStorage keyValueStorage() {
        return keyValueStorage;
    }

    @Override
    public SharedPreferencesAPI sharedPreferencesAPI() {
        return sharedPreferencesAPI;
    }

    @Override
    public InAppStoryService inAppStoryService() {
        return inAppStoryService;
    }
}
