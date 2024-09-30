package com.inappstory.sdk.core;

import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.IASFavorites;
import com.inappstory.sdk.core.api.IASGames;
import com.inappstory.sdk.core.api.IASManager;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.core.api.IASSettings;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.core.api.IASStoryList;
import com.inappstory.sdk.core.api.impl.IASCallbacksImpl;
import com.inappstory.sdk.core.api.impl.IASFavoritesImpl;
import com.inappstory.sdk.core.api.impl.IASGamesImpl;
import com.inappstory.sdk.core.api.impl.IASManagerImpl;
import com.inappstory.sdk.core.api.impl.IASOnboardingsImpl;
import com.inappstory.sdk.core.api.impl.IASSettingsImpl;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.api.impl.IASStackFeedImpl;
import com.inappstory.sdk.core.dataholders.IStoriesListVMHolder;
import com.inappstory.sdk.core.dataholders.StoriesListVMHolder;
import com.inappstory.sdk.core.ui.screens.ScreensManager;

public class IASCoreImpl implements IASCore {
    private static IASCoreImpl INSTANCE;
    private static final Object lock = new Object();
    private final IASCallbacks callbacks = new IASCallbacksImpl(this);
    private final IASFavorites favorites = new IASFavoritesImpl(this);
    private final IASGames games = new IASGamesImpl(this);
    private final IASManager manager = new IASManagerImpl(this);
    private final IASOnboardings onboardings = new IASOnboardingsImpl(this);
    private final IASSettings settings = new IASSettingsImpl(this);
    private final IASSingleStory singleStory = new IASSingleStoryImpl(this);
    private final IASStackFeed stackFeed = new IASStackFeedImpl(this);
    private IASStoryList storyList;
    private final IStoriesListVMHolder storiesListVMHolder = new StoriesListVMHolder();
    private final ScreensManager screensManager = new ScreensManager();


    public IASCoreImpl() {
        synchronized (lock) {
            INSTANCE = this;
        }
    }

    public static IASCoreImpl getInstance() {
        synchronized (lock) {
            return INSTANCE;
        }
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
    public IASSettings settingsAPI() {
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
        return storyList;
    }

    @Override
    public IStoriesListVMHolder storiesListVMHolder() {
        return storiesListVMHolder;
    }

    @Override
    public ScreensManager screensManager() {
        return screensManager;
    }
}
