package com.inappstory.sdk.core.api;

public class IASCoreImpl {
    private static IASCoreImpl INSTANCE;
    private static final Object lock = new Object();
    private IASCallbacks callbacks;
    private IASFavorites favorites;
    private IASGames games;
    private IASManager manager;
    private IASOnboardings onboardings;
    private IASSettings settings;
    private IASSingleStory singleStory;
    private IASStackFeed stackFeed;
    private IASStoryList storyList;


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


}
