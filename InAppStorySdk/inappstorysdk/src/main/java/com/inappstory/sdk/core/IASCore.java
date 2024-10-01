package com.inappstory.sdk.core;

import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.IASFavorites;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.core.api.IASManager;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.core.api.IASStoryList;
import com.inappstory.sdk.core.api.IASStoryListCache;
import com.inappstory.sdk.core.dataholders.IStoriesListVMHolder;
import com.inappstory.sdk.core.ui.screens.ScreensManager;
import com.inappstory.sdk.stories.utils.SessionManager;

public interface IASCore {
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
    IASStoryListCache storyListCache();
    ScreensManager screensManager();
    SessionManager sessionManager();
}
