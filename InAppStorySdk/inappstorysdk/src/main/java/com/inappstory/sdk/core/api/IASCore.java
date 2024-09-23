package com.inappstory.sdk.core.api;

public class IASCore {
    private static IASCore INSTANCE;
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


    public IASCore() {
        synchronized (lock) {
            INSTANCE = this;
        }
    }

    public static IASCore getInstance() {
        synchronized (lock) {
            return INSTANCE;
        }
    }


}
