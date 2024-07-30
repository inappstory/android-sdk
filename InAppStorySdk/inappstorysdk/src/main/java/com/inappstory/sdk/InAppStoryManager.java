package com.inappstory.sdk;

import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.iasutilsconnector.UtilModulesHolder;
import com.inappstory.iasutilsconnector.json.IJsonParser;
import com.inappstory.sdk.iasapimodules.IInAppStoryManager;
import com.inappstory.sdk.iasapimodules.cached.CachedListsApi;
import com.inappstory.sdk.iasapimodules.cached.ICachedListsApi;
import com.inappstory.sdk.iasapimodules.favorites.FavoritesApi;
import com.inappstory.sdk.iasapimodules.favorites.IFavoritesApi;
import com.inappstory.sdk.iasapimodules.games.GamesApi;
import com.inappstory.sdk.iasapimodules.games.IGamesApi;
import com.inappstory.sdk.iasapimodules.onboardings.IOnboardingStoriesApi;
import com.inappstory.sdk.iasapimodules.onboardings.OnboardingStoriesApi;
import com.inappstory.sdk.iasapimodules.settings.SettingsApi;
import com.inappstory.sdk.iasapimodules.single.ISingleStoryApi;
import com.inappstory.sdk.iasapimodules.single.SingleStoryApi;
import com.inappstory.sdk.iasapimodules.stack.IStackFeedApi;
import com.inappstory.sdk.iasapimodules.stack.StackFeedApi;
import com.inappstory.sdk.iasapimodules.utils.IUtilsApi;
import com.inappstory.sdk.iasapimodules.utils.UtilsApi;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.packages.core.IASCore;
import com.inappstory.sdk.packages.core.IIASCore;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InAppStoryManager implements IInAppStoryManager {

    public static final String IAS_DEBUG_API = "IAS debug api";
    public final static String IAS_ERROR_TAG = "InAppStory_SDK_error";

    private static InAppStoryManager INSTANCE;

    private static final Object lock = new Object();
    private final SettingsApi settingsApi = new SettingsApi();
    private final ICachedListsApi cachedListsApi = new CachedListsApi();
    private final IFavoritesApi favoritesApi = new FavoritesApi();
    private final IGamesApi gamesApi = new GamesApi();
    private final IOnboardingStoriesApi onboardingStoriesApi =
            new OnboardingStoriesApi(settingsApi);
    private final ISingleStoryApi singleStoryApi =
            new SingleStoryApi(settingsApi);
    private final IStackFeedApi stackFeedApi =
            new StackFeedApi(settingsApi);
    private final IUtilsApi utilsApi = new UtilsApi();

    public static InAppStoryManager getInstance() {
        synchronized (lock) {
            return INSTANCE;
        }
    }

    private InAppStoryManager(Context context) {

    }

    public static Pair<String, Integer> getLibraryVersion() {
        return new Pair<>(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }

    public static void useInstance(@NonNull UseManagerInstanceCallback callback) {
        InAppStoryManager manager = getInstance();
        try {
            if (manager != null) {
                callback.use(manager);
            } else {
                callback.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initSDK(@NonNull Context context) {
        initSDK(context, false);
    }

    public static void initSDK(@NonNull Context context, boolean skipCheck) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean calledFromApplication = skipCheck;
        if (!skipCheck)
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                try {
                    if (Application.class.isAssignableFrom(Class.forName(stackTraceElement.getClassName()))) {
                        calledFromApplication = true;
                        break;
                    }
                    if (ContentProvider.class.isAssignableFrom(Class.forName(stackTraceElement.getClassName()))) {
                        calledFromApplication = true;
                        break;
                    }
                } catch (ClassNotFoundException e) {

                }
            }
        if (!(context instanceof Application)) calledFromApplication = false;
        if (!calledFromApplication) {
            IASCore.getInstance()
                    .getLogger()
                    .showELog(
                            IAS_ERROR_TAG,
                            "Method must be called from Application class and context has to be an applicationContext"
                    );
            return;
        }
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = new InAppStoryManager(context);
            }
        }
        INSTANCE.createServiceThread(context);
        INSTANCE.utilModulesHolder = UtilModulesHolder.INSTANCE;
        INSTANCE.utilModulesHolder.setJsonParser(new IJsonParser() {
            @Override
            public <T> T fromJson(String json, Class<T> typeOfT) {
                return JsonParser.fromJson(json, typeOfT);
            }
        });
    }

    IIASCore core;

    public void setLogger(IASLogger logger) {
        IASCore.getInstance().setLogger(logger);
    }

    @Override
    public void clearCachedLists() {
        cachedListsApi.clearCachedLists();
    }

    @Override
    public void clearCachedList(String id) {
        cachedListsApi.clearCachedList(id);
    }

    @Override
    public void removeAllFavorites() {
        favoritesApi.removeAllFavorites();
    }

    @Override
    public void removeFromFavorite(int storyId) {
        favoritesApi.removeFromFavorite(storyId);
    }

    @Override
    public void preloadGames() {
        gamesApi.preloadGames();
    }

    @Override
    public void closeGame() {
        gamesApi.closeGame();
    }

    @Override
    public void openGame(String gameId, @NonNull Context context) {
        gamesApi.openGame(gameId, context);
    }

    @Override
    public void showOnboardingStories(Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(context, manager);
    }

    @Override
    public void showOnboardingStories(List<String> tags, Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(tags, context, manager);
    }

    @Override
    public void showOnboardingStories(String feed, Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(feed, context, manager);
    }

    @Override
    public void showOnboardingStories(String feed, List<String> tags, Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(feed, tags, context, manager);
    }

    @Override
    public void showOnboardingStories(int limit, Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(limit, context, manager);
    }

    @Override
    public void showOnboardingStories(int limit, List<String> tags, Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(limit, tags, context, manager);
    }

    @Override
    public void showOnboardingStories(int limit, String feed, Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(limit, feed, context, manager);
    }

    @Override
    public void showOnboardingStories(int limit, String feed, List<String> tags, Context context, AppearanceManager manager) {
        onboardingStoriesApi.showOnboardingStories(limit, feed, tags, context, manager);
    }

    @Override
    public void setLang(@NonNull Locale lang) {
        settingsApi.setLang(lang);
    }

    @Override
    public void setUserId(@NonNull String userId) {
        settingsApi.setUserId(userId);
    }

    @Override
    public void setTags(ArrayList<String> tags) {
        settingsApi.setTags(tags);
    }

    @Override
    public void addTags(ArrayList<String> tagsToAdd) {
        settingsApi.addTags(tagsToAdd);
    }

    @Override
    public void removeTags(ArrayList<String> tagsToRemove) {
        settingsApi.removeTags(tagsToRemove);
    }

    @Override
    public void setPlaceholders(@NonNull Map<String, String> newPlaceholders) {
        settingsApi.setPlaceholders(newPlaceholders);
    }

    @Override
    public void setPlaceholder(String key, String value) {
        settingsApi.setPlaceholder(key, value);
    }

    @Override
    public void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> placeholders) {
        settingsApi.setImagePlaceholders(placeholders);
    }

    @Override
    public void setImagePlaceholder(@NonNull String key, ImagePlaceholderValue value) {
        settingsApi.setImagePlaceholder(key, value);
    }

    @Override
    public void showStory(String storyId, Context context, AppearanceManager manager) {
        singleStoryApi.showStory(storyId, context, manager);
    }

    @Override
    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback) {
        singleStoryApi.showStory(storyId, context, manager, callback);
    }

    @Override
    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback, Integer slide) {
        singleStoryApi.showStory(storyId, context, manager, callback, slide);
    }

    @Override
    public void showStoryOnce(String storyId, Context context, AppearanceManager manager, IShowStoryOnceCallback callback) {
        singleStoryApi.showStoryOnce(storyId, context, manager, callback);
    }

    @Override
    public void getStackFeed(String feed, String uniqueStackId, List<String> tags, AppearanceManager appearanceManager, IStackFeedResult stackFeedResult) {
        stackFeedApi.getStackFeed(feed, uniqueStackId, tags, appearanceManager, stackFeedResult);
    }

    @Override
    public void clearCache() {
        utilsApi.clearCache();
    }
}
