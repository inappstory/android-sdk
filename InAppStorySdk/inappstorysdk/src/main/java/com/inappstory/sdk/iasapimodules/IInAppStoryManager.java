package com.inappstory.sdk.iasapimodules;

import com.inappstory.sdk.iasapimodules.cached.ICachedListsApi;
import com.inappstory.sdk.iasapimodules.favorites.IFavoritesApi;
import com.inappstory.sdk.iasapimodules.games.IGamesApi;
import com.inappstory.sdk.iasapimodules.onboardings.IOnboardingStoriesApi;
import com.inappstory.sdk.iasapimodules.settings.ISettingsApi;
import com.inappstory.sdk.iasapimodules.single.ISingleStoryApi;
import com.inappstory.sdk.iasapimodules.stack.IStackFeedApi;
import com.inappstory.sdk.iasapimodules.utils.IUtilsApi;

public interface IInAppStoryManager extends
        ICachedListsApi,
        IFavoritesApi,
        IGamesApi,
        IOnboardingStoriesApi,
        ISettingsApi,
        ISingleStoryApi,
        IStackFeedApi,
        IUtilsApi {

}
