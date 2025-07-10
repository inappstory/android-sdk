package com.inappstory.sdk.core;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.api.IASAssetsHolder;
import com.inappstory.sdk.core.api.IASBanners;
import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.IASContentLoader;
import com.inappstory.sdk.core.api.IASContentPreload;
import com.inappstory.sdk.core.api.IASExternalUtilsAPI;
import com.inappstory.sdk.core.api.IASFavorites;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.core.api.IASLimitsHolder;
import com.inappstory.sdk.core.api.IASLogs;
import com.inappstory.sdk.core.api.IASManager;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASProjectSettings;
import com.inappstory.sdk.core.api.IASProjectSettingsInternal;
import com.inappstory.sdk.core.api.IASSessionAssetsHolder;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.core.api.IASStatistic;
import com.inappstory.sdk.core.api.IASStoryList;
import com.inappstory.sdk.core.api.IASStoriesOpenedCache;
import com.inappstory.sdk.core.api.impl.IASAssetsHolderImpl;
import com.inappstory.sdk.core.api.impl.IASBannersImpl;
import com.inappstory.sdk.core.api.impl.IASCallbacksImpl;
import com.inappstory.sdk.core.api.impl.IASContentLoaderImpl;
import com.inappstory.sdk.core.api.impl.IASContentPreloadImpl;
import com.inappstory.sdk.core.api.impl.IASExternalUtilsAPIImpl;
import com.inappstory.sdk.core.api.impl.IASFavoritesImpl;
import com.inappstory.sdk.core.api.impl.IASGamesImpl;
import com.inappstory.sdk.core.api.impl.IASInAppMessageImpl;
import com.inappstory.sdk.core.api.impl.IASLimitsHolderImpl;
import com.inappstory.sdk.core.api.impl.IASLogsImpl;
import com.inappstory.sdk.core.api.impl.IASManagerImpl;
import com.inappstory.sdk.core.api.impl.IASOnboardingsImpl;
import com.inappstory.sdk.core.api.impl.IASProjectSettingsImpl;
import com.inappstory.sdk.core.api.impl.IASSettingsImpl;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.api.impl.IASStackFeedImpl;
import com.inappstory.sdk.core.api.impl.IASStatisticImpl;
import com.inappstory.sdk.core.api.impl.IASStoriesOpenedCacheImpl;
import com.inappstory.sdk.core.dataholders.ContentHolder;
import com.inappstory.sdk.core.dataholders.IContentHolder;
import com.inappstory.sdk.core.dataholders.IStoriesListVMHolder;
import com.inappstory.sdk.core.dataholders.StoriesListVMHolder;
import com.inappstory.sdk.core.ui.screens.ScreensManager;
import com.inappstory.sdk.domain.IWidgetsViewModels;
import com.inappstory.sdk.domain.WidgetsViewModels;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.AcceleratorUtils;
import com.inappstory.sdk.utils.IAcceleratorUtils;
import com.inappstory.sdk.utils.IVibrateUtils;
import com.inappstory.sdk.utils.VibrateUtils;

public class IASCoreImpl implements IASCore {
    private AppearanceManager commonAppearance = null;
    private final IASCallbacks callbacks;
    private final IASFavorites favorites;
    private final IASProjectSettingsInternal projectSettings;
    private final IASGames games;
    private final IASManager manager;
    private final ExceptionManager exceptionManager;
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
    private final IAcceleratorUtils acceleratorUtils;
    private final IASContentPreload contentPreload;
    private final IASExternalUtilsAPI externalUtilsAPI;
    private final IASContentLoader contentLoader;
    private final IASLogs iasLogs;
    private final IWidgetsViewModels widgetsViewModels;
    private final Context context;
    private final InAppStoryService inAppStoryService;
    private final NetworkClient networkClient;
    private final KeyValueStorage keyValueStorage;
    private final SharedPreferencesAPI sharedPreferencesAPI;
    private final IContentHolder contentHolder;
    private final IASInAppMessage inAppMessages;
    private final IASBanners banners;
    private final IASAssetsHolder assetsHolder;
    private final IASLimitsHolder limitsHolder;

    public IASCoreImpl(Context context) {
        this.context = context;
        widgetsViewModels = new WidgetsViewModels(this);
        exceptionManager = new ExceptionManager(this);
        contentHolder = new ContentHolder();
        contentLoader = new IASContentLoaderImpl(this);
        externalUtilsAPI = new IASExternalUtilsAPIImpl();
        sharedPreferencesAPI = new SharedPreferencesAPI(this);
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
        vibrateUtils = new VibrateUtils(this);
        acceleratorUtils = new AcceleratorUtils(this);
        contentPreload = new IASContentPreloadImpl(this);
        iasLogs = new IASLogsImpl(this);
        inAppStoryService = new InAppStoryService(this);
        inAppMessages = new IASInAppMessageImpl(this);
        banners = new IASBannersImpl(this);
        assetsHolder = new IASAssetsHolderImpl(this);
        limitsHolder = new IASLimitsHolderImpl();
        projectSettings = new IASProjectSettingsImpl(this);
        Thread.setDefaultUncaughtExceptionHandler(new IASExceptionHandler(this));
        externalUtilsAPI.init();
    }


    @Override
    public AppearanceManager commonAppearance() {
        return this.commonAppearance;
    }

    @Override
    public void commonAppearance(AppearanceManager appearanceManager) {
        this.commonAppearance = appearanceManager;
    }

    @Override
    public ExceptionManager exceptionManager() {
        return exceptionManager;
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
    public IASProjectSettingsInternal projectSettingsAPI() {
        return projectSettings;
    }

    @Override
    public IASLimitsHolder limitsHolder() {
        return limitsHolder;
    }

    @Override
    public IASInAppMessage inAppMessageAPI() {
        return inAppMessages;
    }

    @Override
    public IASBanners bannersAPI() {
        return banners;
    }

    @Override
    public IWidgetsViewModels widgetViewModels() {
        return widgetsViewModels;
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
    public IAcceleratorUtils acceleratorUtils() {
        return acceleratorUtils;
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
    public IContentHolder contentHolder() {
        return contentHolder;
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

    @Override
    public IASAssetsHolder assetsHolder() {
        return assetsHolder;
    }
}
