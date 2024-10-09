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
import com.inappstory.sdk.core.dataholders.IStoriesListVMHolder;
import com.inappstory.sdk.core.ui.screens.ScreensManager;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.IVibrateUtils;

public interface IASCore {
    Context appContext();
    IASCallbacks callbacksAPI();
    IASFavorites favoritesAPI();
    IASGames gamesAPI();
    IASManager managerAPI();
    IASOnboardings onboardingsAPI();
    IASDataSettings settingsAPI();
    IASSingleStory singleStoryAPI();
    IASStackFeed stackFeedAPI();
    IASStoryList storyListAPI();
    IStoriesListVMHolder storiesListVMHolder();
    IASStoriesOpenedCache storyListCache();
    ScreensManager screensManager();
    SessionManager sessionManager();
    IASStatistic statistic();
    IVibrateUtils vibrateUtils();
    IASContentPreload contentPreload();
    IASSessionAssetsHolder sessionAssets();
    IASExternalUtilsAPI externalUtilsAPI();
    IASContentLoader contentLoader();
    NetworkClient network();
    IASLogs logs();
    KeyValueStorage keyValueStorage();
    SharedPreferencesAPI sharedPreferencesAPI();
    InAppStoryService inAppStoryService();
}
