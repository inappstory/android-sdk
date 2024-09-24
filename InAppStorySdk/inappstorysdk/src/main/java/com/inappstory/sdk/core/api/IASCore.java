package com.inappstory.sdk.core.api;

public interface IASCore {
    IASCallbacks callbacksAPI();
    IASFavorites favoritesAPI();
    IASGames gamesAPI();
    IASManager managerAPI();
    IASOnboardings onboardingsAPI();
    IASSettings settingsAPI();
    IASSingleStory singleStoryAPI();
    IASStackFeed stackFeedAPI();
    IASStoryList storyListAPI();
}
