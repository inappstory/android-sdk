package com.inappstory.sdk;

import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

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
import com.inappstory.sdk.lrudiskcache.CacheSize;
import com.inappstory.sdk.packages.core.IASCore;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InAppStoryManager implements IInAppStoryManager {


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
            IASCore.logMessage(
                    "Method must be called from Application class and context has to be an applicationContext"
            );
            return;
        }
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = new InAppStoryManager(context);
            }
        }
        //INSTANCE.createServiceThread(context);
    }

    private void build(Builder builder) {

    }

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

    public static class Builder {

        public boolean sandbox() {
            return sandbox;
        }

        public String userId() {
            return userId;
        }

        public String apiKey() {
            return apiKey;
        }

        public Locale locale() {
            return locale;
        }

        public boolean gameDemoMode() {
            return gameDemoMode;
        }

        public boolean isDeviceIdEnabled() {
            return deviceIdEnabled;
        }

        public String testKey() {
            return testKey;
        }

        public ArrayList<String> tags() {
            return tags;
        }

        public int getCacheSize() {
            return cacheSize;
        }

        public Map<String, String> placeholders() {
            return placeholders;
        }

        public Map<String, ImagePlaceholderValue> imagePlaceholders() {
            return imagePlaceholders;
        }

        boolean sandbox;
        boolean gameDemoMode;
        boolean deviceIdEnabled = true;

        int cacheSize;
        String userId;
        String apiKey;
        String testKey;
        Locale locale = Locale.getDefault();
        ArrayList<String> tags;
        Map<String, String> placeholders;
        Map<String, ImagePlaceholderValue> imagePlaceholders;

        public Builder() {
        }

        @Deprecated
        public Builder sandbox(boolean sandbox) {
            this.sandbox = sandbox;
            return this;
        }

        public Builder lang(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder gameDemoMode(boolean gameDemoMode) {
            this.gameDemoMode = gameDemoMode;
            return this;
        }

        public Builder isDeviceIDEnabled(boolean deviceIdEnabled) {
            this.deviceIdEnabled = deviceIdEnabled;
            return this;
        }

        /**
         * use to set available space for file caching (slide images, videos, games, etc.)
         *
         * @param cacheSize (cacheSize) - size of available space for cache. Can be set with {@link CacheSize} constants
         *                  {@link com.inappstory.sdk.lrudiskcache.CacheSize#SMALL} - 10mb for stories, 5mb fo story covers
         *                  {@link com.inappstory.sdk.lrudiskcache.CacheSize#MEDIUM} - (by default) 100mb for stories, 10mb fo story covers
         *                  {@link com.inappstory.sdk.lrudiskcache.CacheSize#LARGE} -  200mb for stories, 10mb fo story covers
         * @return {@link OldInAppStoryManager.Builder}
         */
        public Builder cacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        /**
         * use to set api key in runtime (or as alternate to csApiKey string constant)
         *
         * @param apiKey (apiKey) value for api key
         *               false by default
         * @return {@link OldInAppStoryManager.Builder}
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder testKey(String testKey) {
            this.testKey = testKey;
            return this;
        }

        /**
         * use to set user id.
         *
         * @param userId (userId) value for user id. Can't be longer than 255 characters.
         * @return {@link OldInAppStoryManager.Builder}
         */
        public Builder userId(@NonNull String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * @param tags (tags) tags for targeting stories
         * @return {@link OldInAppStoryManager.Builder}
         */
        public Builder tags(String... tags) {
            this.tags = new ArrayList<>();
            for (int i = 0; i < tags.length; i++) {
                this.tags.add(tags[i]);
            }
            return this;
        }

        /**
         * @param tags (tags) tags for targeting stories
         * @return {@link OldInAppStoryManager.Builder}
         */
        public Builder tags(ArrayList<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * @param placeholders (placeholders) placeholders for default values in stories
         * @return {@link OldInAppStoryManager.Builder}
         */
        public Builder placeholders(Map<String, String> placeholders) {
            this.placeholders = placeholders;
            return this;
        }

        /**
         * @param placeholders (placeholders) - placeholders for default values in stories
         * @return {@link OldInAppStoryManager.Builder}
         */
        public Builder imagePlaceholders(Map<String, ImagePlaceholderValue> placeholders) {
            this.imagePlaceholders = placeholders;
            return this;
        }

        /**
         * main method to create {@link OldInAppStoryManager} instance.
         *
         * @return {@link OldInAppStoryManager}
         */
        public InAppStoryManager create() {
            synchronized (lock) {
                if (INSTANCE == null) {
                    IASCore.logMessage("Method InAppStoryManager.init must be called from Application class");
                    return null;
                }
            }
            INSTANCE.build(Builder.this);
            return INSTANCE;
        }
    }
}
