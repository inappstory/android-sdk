package com.inappstory.sdk;

import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_200;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.inappstory.sdk.core.lrudiskcache.CacheSize;
import com.inappstory.sdk.core.network.ApiSettings;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.JsonParser;
import com.inappstory.sdk.core.network.utils.HostFromSecretKey;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.api.models.logs.WebConsoleLog;
import com.inappstory.sdk.stories.callbacks.AppClickCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.ExceptionCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.callbacks.UrlClickCallback;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.StoriesReaderSettings;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Main class for work with SDK.
 * Need to initialize it first with {@link Builder} before other interactions.
 * Singleton class, can be available with {@link #getInstance()}.
 * Can be reinitialized.
 */
public class InAppStoryManager {

    private static InAppStoryManager INSTANCE;

    public static NetworkClient getNetworkClient() {
        if (InAppStoryManager.getInstance() == null) return null;
        return InAppStoryManager.getInstance().networkClient;
    }

    public static boolean testGenerated = false;

    public static boolean isNull() {
        synchronized (lock) {
            return INSTANCE == null;
        }
    }

    public static void setInstance(InAppStoryManager manager) {
        synchronized (lock) {
            INSTANCE = manager;
        }
    }

    public Context getContext() {
        return context;
    }

    Context context;


    static final String DEBUG_API = "IAS debug api";


    @SuppressLint(DEBUG_API)
    public static void debugSDKCalls(String methodName, String args) {
        Log.d("InAppStory_SDKCalls", System.currentTimeMillis()
                + " "
                + methodName + " " + args);
    }

    @SuppressLint(DEBUG_API)
    public static IASLogger logger;

    @SuppressLint(DEBUG_API)
    public interface IASLogger {
        void showELog(String tag, String message);

        void showDLog(String tag, String message);
    }

    @SuppressLint(DEBUG_API)
    public static IAS_QA_Log iasQaLog;

    @SuppressLint(DEBUG_API)
    public static void showELog(String tag, String message) {
        IASLogger currentLogger = logger != null ? logger : defaultLogger;
        if (currentLogger != null) currentLogger.showELog(tag, message);
    }

    private static final IASLogger defaultLogger = new IASLogger() {
        @Override
        public void showELog(String tag, String message) {
            Log.e(tag, message);
        }

        @Override
        public void showDLog(String tag, String message) {
            Log.d(tag, message);
        }
    };

    @SuppressLint(DEBUG_API)
    public static void showDLog(String tag, String message) {
        if (logger != null) logger.showDLog(tag, message);
    }

    @SuppressLint(DEBUG_API)
    public static void sendApiRequestLog(ApiLogRequest log) {
        if (iasQaLog != null) iasQaLog.getApiRequestLog(log);
    }

    @SuppressLint(DEBUG_API)
    public static void sendApiResponseLog(ApiLogResponse log) {
        if (iasQaLog != null)
            iasQaLog.getApiResponseLog(log);
    }

    @SuppressLint(DEBUG_API)
    public static void sendApiRequestResponseLog(ApiLogRequest logRequest,
                                                 ApiLogResponse logResponse) {
        if (iasQaLog != null)
            iasQaLog.getApiRequestResponseLog(logRequest, logResponse);
    }

    @SuppressLint(DEBUG_API)
    public static void sendExceptionLog(ExceptionLog log) {
        if (iasQaLog != null) iasQaLog.getExceptionLog(log);
    }

    @SuppressLint(DEBUG_API)
    public static void sendWebConsoleLog(WebConsoleLog log) {
        if (iasQaLog != null) iasQaLog.getWebConsoleLog(log);
    }

    /**
     * use set custom callback in case of uncaught exceptions.
     *
     * @param callback (callback). Has {@link ExceptionCallback} type
     */
    public void setCallback(ExceptionCallback callback) {
        this.exceptionCallback = callback;
    }

    public ExceptionCallback getExceptionCallback() {
        return exceptionCallback;
    }

    private ExceptionCallback exceptionCallback;


    /**
     * @return {@link ArrayList} of tags
     */
    public ArrayList<String> getTags() {
        synchronized (tagsLock) {
            return tags;
        }
    }

    //Test

    /**
     * use to clear downloaded files and in-app cache
     */
    public void clearCache() {
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().getDownloadManager().clearCache();
    }
    //Test

    /**
     * use to clear downloaded files and in-app cache without manager
     */
    public void clearCache(Context context) {
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().getDownloadManager().clearCache();
    }

    /**
     * use to force close story reader
     */
    public static void closeStoryReader() {
        closeStoryReader(CloseReader.CUSTOM, StatisticManager.CUSTOM);
    }

    @Deprecated
    public void openGame(String gameId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null && context != null) {
            service.openGameReaderWithGC(context, null, gameId);
        }
    }

    public void openGame(String gameId, @NonNull Context context) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            service.openGameReaderWithGC(context, null, gameId);
        }
    }

    public void closeGame() {
        ScreensManager.getInstance().closeGameReader();
    }

    /**
     * use to force close story reader
     */
    public static void closeStoryReader(final CloseReader action, final String cause) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ScreensManager.getInstance().closeStoryReader(action, cause);
                ScreensManager.getInstance().hideGoods();
                ScreensManager.getInstance().closeGameReader();
                ScreensManager.getInstance().closeUGCEditor();
            }
        });
    }

    /**
     * use to set callback on different errors
     */
    public void setErrorCallback(ErrorCallback errorCallback) {
        CallbackManager.getInstance().setErrorCallback(errorCallback);
    }

    /**
     * use to set callback on share click
     */
    public void setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback) {
        CallbackManager.getInstance().setClickOnShareStoryCallback(clickOnShareStoryCallback);
    }


    /**
     * use to set callback on game start/close/finish
     */
    public void setGameReaderCallback(GameReaderCallback gameReaderCallback) {
        CallbackManager.getInstance().setGameReaderCallback(gameReaderCallback);
    }

    /**
     * use to set callback on onboardings load
     */
    public void setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback) {
        CallbackManager.getInstance().setOnboardingLoadCallback(onboardingLoadCallback);
    }

    /**
     * use to set callback on click on buttons in stories (with info)
     */
    public void setCallToActionCallback(CallToActionCallback callToActionCallback) {
        CallbackManager.getInstance().setCallToActionCallback(callToActionCallback);
    }

    /**
     * use to set callback on click on widgets in stories (with info)
     */
    public void setStoryWidgetCallback(StoryWidgetCallback storyWidgetCallback) {
        CallbackManager.getInstance().setStoryWidgetCallback(storyWidgetCallback);
    }


    /**
     * use to set callback on stories reader closing
     */
    public void setCloseStoryCallback(CloseStoryCallback closeStoryCallback) {
        CallbackManager.getInstance().setCloseStoryCallback(closeStoryCallback);
    }

    /**
     * use to set callback on favorite action
     */
    public void setFavoriteStoryCallback(FavoriteStoryCallback favoriteStoryCallback) {
        CallbackManager.getInstance().setFavoriteStoryCallback(favoriteStoryCallback);
    }

    /**
     * use to set callback on like/dislike action
     */
    public void setLikeDislikeStoryCallback(LikeDislikeStoryCallback likeDislikeStoryCallback) {
        CallbackManager.getInstance().setLikeDislikeStoryCallback(likeDislikeStoryCallback);
    }

    /**
     * use to set callback on slide shown in reader
     */
    public void setShowSlideCallback(ShowSlideCallback showSlideCallback) {
        CallbackManager.getInstance().setShowSlideCallback(showSlideCallback);
    }

    /**
     * use to set callback on story shown in reader
     */
    public void setShowStoryCallback(ShowStoryCallback showStoryCallback) {
        CallbackManager.getInstance().setShowStoryCallback(showStoryCallback);
    }

    /**
     * use to set callback on single story loading
     */
    public void setSingleLoadCallback(SingleLoadCallback singleLoadCallback) {
        CallbackManager.getInstance().setSingleLoadCallback(singleLoadCallback);
    }

    /**
     * use to set callback on click on buttons in stories (without additional info)
     */
    @Deprecated
    public void setUrlClickCallback(UrlClickCallback urlClickCallback) {
        CallbackManager.getInstance().setUrlClickCallback(urlClickCallback);
    }


    /**
     * use to customize share functional
     */
    public void setShareCallback(ShareCallback shareCallback) {
        CallbackManager.getInstance().setShareCallback(shareCallback);
    }

    /**
     * use to customize click on non-url buttons in reader
     */
    public void setAppClickCallback(AppClickCallback appClickCallback) {
        CallbackManager.getInstance().setAppClickCallback(appClickCallback);
    }

    //Test

    /**
     * @return {@link String} with tags joined by comma
     */
    public String getTagsString() {
        synchronized (tagsLock) {
            if (tags == null) return null;
            return TextUtils.join(",", tags);
        }
    }

    /**
     * use to customize tags in runtime. Replace tags array.
     *
     * @param tags (tags)
     */

    public void setTags(ArrayList<String> tags) {
        if (tags != null && getBytesLength(TextUtils.join(",", tags)) > TAG_LIMIT) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(context, R.string.ias_setter_tags_length_error));
            return;
        }
        synchronized (tagsLock) {
            this.tags = tags;
            clearCachedLists();
        }
    }


    private final static int TAG_LIMIT = 4000;

    private Object tagsLock = new Object();

    /**
     * use to customize tags in runtime. Adds tags to array.
     *
     * @param newTags (newTags) - list of additional tags
     */

    public void addTags(ArrayList<String> newTags) {
        synchronized (tagsLock) {
            if (newTags == null || newTags.isEmpty()) return;
            if (tags == null) tags = new ArrayList<>();
            String oldTagsString = TextUtils.join(",", tags);
            String newTagsString = TextUtils.join(",", newTags);
            if (getBytesLength(oldTagsString + newTagsString) > TAG_LIMIT - 1) {
                showELog(IAS_ERROR_TAG, getErrorStringFromContext(context, R.string.ias_setter_tags_length_error));
                return;
            }
            for (String tag : newTags) {
                addTag(tag);
            }
            clearCachedLists();
        }
    }

    /**
     * use to customize tags in runtime. Removes tags from array.
     *
     * @param removedTags (removedTags) - list of removing tags
     */

    public void removeTags(ArrayList<String> removedTags) {
        synchronized (tagsLock) {
            if (tags == null || removedTags == null || removedTags.isEmpty()) return;
            for (String tag : removedTags) {
                removeTag(tag);
            }
            clearCachedLists();
        }
    }

    /**
     * use to customize tags in runtime. Adds tag to array.
     *
     * @param tag (tag) - single additional tag
     */
    private void addTag(String tag) {
        if (!tags.contains(tag)) tags.add(tag);
    }

    /**
     * use to customize tags in runtime. Removes tag from array.
     *
     * @param tag (tag) - single removing tags
     */
    private void removeTag(String tag) {
        if (tags.contains(tag)) tags.remove(tag);
    }

    /**
     * use to customize default string in stories runtime.
     *
     * @param key   (key) - what we replace
     * @param value (value) - replacement result
     */
    public void setPlaceholder(String key, String value) {
        synchronized (placeholdersLock) {
            if (defaultPlaceholders == null) defaultPlaceholders = new HashMap<>();
            if (placeholders == null) placeholders = new HashMap<>();
            if (value == null) {
                if (defaultPlaceholders.containsKey(key)) {
                    placeholders.put(key, defaultPlaceholders.get(key));
                } else {
                    placeholders.remove(key);
                }
            } else {
                placeholders.put(key, value);
            }

        }
    }

    /**
     * use to customize default strings in stories runtime.
     *
     * @param newPlaceholders (newPlaceholders) - key-value map (key - what we replace, value - replacement result)
     */
    public void setPlaceholders(@NonNull Map<String, String> newPlaceholders) {
        synchronized (placeholdersLock) {
            if (defaultPlaceholders == null) defaultPlaceholders = new HashMap<>();
            if (this.placeholders == null)
                this.placeholders = new HashMap<>();
            else
                this.placeholders.clear();
            for (String key : newPlaceholders.keySet()) {
                String value = newPlaceholders.get(key);
                if (value == null) {
                    if (defaultPlaceholders.containsKey(key)) {
                        this.placeholders.put(key, defaultPlaceholders.get(key));
                    } else {
                        this.placeholders.remove(key);
                    }
                } else {
                    this.placeholders.put(key, value);
                }
            }
        }
    }

    void setDefaultPlaceholders(@NonNull List<StoryPlaceholder> placeholders) {
        synchronized (placeholdersLock) {
            for (StoryPlaceholder placeholder : placeholders) {
                String key = placeholder.name;
                this.defaultPlaceholders.put(key,
                        placeholder.defaultVal);
                if (!this.placeholders.containsKey(key)) {
                    InAppStoryManager.getInstance().placeholders.put(key,
                            placeholder.defaultVal);
                }
            }
        }
    }

    public Map<String, String> getPlaceholdersCopy() {
        synchronized (placeholdersLock) {
            if (placeholders == null) return new HashMap<>();
            return new HashMap<>(placeholders);
        }
    }

    ArrayList<String> tags;

    private final Object placeholdersLock = new Object();

    /**
     * Returns map with all default strings replacements
     */
    public Map<String, String> getPlaceholders() {
        synchronized (placeholdersLock) {
            if (defaultPlaceholders == null) defaultPlaceholders = new HashMap<>();
            if (placeholders == null) placeholders = new HashMap<>();
            return placeholders;
        }
    }

    public Map<String, ImagePlaceholderValue> getImagePlaceholdersValues() {
        synchronized (placeholdersLock) {
            Map<String, ImagePlaceholderValue> resultPlaceholders = new HashMap<>();
            if (defaultImagePlaceholders == null) defaultImagePlaceholders = new HashMap<>();
            if (imagePlaceholders == null) imagePlaceholders = new HashMap<>();
            resultPlaceholders.putAll(defaultImagePlaceholders);
            resultPlaceholders.putAll(imagePlaceholders);
            return resultPlaceholders;
        }
    }

    public Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> getImagePlaceholdersValuesWithDefaults() {
        synchronized (placeholdersLock) {
            Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> resultPlaceholders = new HashMap<>();
            Map<String, ImagePlaceholderValue> tempPlaceholders = new HashMap<>();
            if (defaultImagePlaceholders == null) defaultImagePlaceholders = new HashMap<>();
            if (imagePlaceholders == null) imagePlaceholders = new HashMap<>();

            tempPlaceholders.putAll(defaultImagePlaceholders);
            tempPlaceholders.putAll(imagePlaceholders);
            for (Map.Entry<String, ImagePlaceholderValue> entry : tempPlaceholders.entrySet()) {
                if (defaultImagePlaceholders.containsKey(entry.getKey())) {
                    resultPlaceholders.put(
                            entry.getKey(),
                            new Pair<>(
                                    entry.getValue(),
                                    //entry.getValue()
                                    defaultImagePlaceholders.get(entry.getKey())
                            )
                    );
                } else {
                    resultPlaceholders.put(
                            entry.getKey(),
                            new Pair<>(
                                    entry.getValue(),
                                    entry.getValue()
                            )
                    );
                }
            }
            return resultPlaceholders;
        }
    }


    public void setImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> placeholders) {
        synchronized (placeholdersLock) {
            imagePlaceholders.clear();
            if (imagePlaceholders == null)
                imagePlaceholders = new HashMap<>();
            else
                imagePlaceholders.clear();
            imagePlaceholders.putAll(placeholders);
        }
    }

    void setDefaultImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> placeholders) {
        synchronized (placeholdersLock) {
            if (defaultImagePlaceholders == null) defaultImagePlaceholders = new HashMap<>();
            defaultImagePlaceholders.clear();
            defaultImagePlaceholders.putAll(placeholders);
        }
    }

    void setDefaultImagePlaceholder(@NonNull String key, @NonNull ImagePlaceholderValue value) {
        synchronized (placeholdersLock) {
            if (defaultImagePlaceholders == null) defaultImagePlaceholders = new HashMap<>();
            defaultImagePlaceholders.put(key, value);
        }
    }


    public void setImagePlaceholder(@NonNull String key, ImagePlaceholderValue value) {
        synchronized (placeholdersLock) {
            if (imagePlaceholders == null) imagePlaceholders = new HashMap<>();
            if (value == null) imagePlaceholders.remove(key);
            else imagePlaceholders.put(key, value);
        }
    }

    Map<String, String> placeholders = new HashMap<>();
    Map<String, ImagePlaceholderValue> imagePlaceholders = new HashMap<>();

    public Map<String, String> getDefaultPlaceholders() {
        synchronized (placeholdersLock) {
            if (defaultPlaceholders == null) defaultPlaceholders = new HashMap<>();
            if (placeholders == null) placeholders = new HashMap<>();
            return defaultPlaceholders;
        }
    }

    Map<String, String> defaultPlaceholders = new HashMap<>();
    Map<String, ImagePlaceholderValue> defaultImagePlaceholders = new HashMap<>();

    private static final String TEST_DOMAIN = "https://api.test.inappstory.com/";
    private static final String PRODUCT_DOMAIN = "https://api.inappstory.ru/";

    public String getApiKey() {
        return API_KEY;
    }

    public String getTestKey() {
        return TEST_KEY;
    }

    String API_KEY = "";

    String TEST_KEY = null;

    public InAppStoryManager() {

    }

    InAppStoryService service;

    Thread serviceThread;

    void createServiceThread(final Context context, final String userId) {
        if (InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().onDestroy();
        }
        if (serviceThread != null) {
            serviceThread.interrupt();
            serviceThread = null;
        }
        serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                service = new InAppStoryService(userId);
                service.onCreate(context, exceptionCache);
                Looper.loop();
            }
        });
        //serviceThread.setUncaughtExceptionHandler(new InAppStoryService.DefaultExceptionHandler());
        serviceThread.start();
    }

    void setExceptionCache(ExceptionCache exceptionCache) {
        this.exceptionCache = exceptionCache;
    }

    private ExceptionCache exceptionCache;

    public void removeFromFavorite(final int storyId) {
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                service.removeStoryFromFavorite(storyId);

            }

            @Override
            public void onError() {

            }
        });
    }

    public void removeAllFavorites() {
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                service.removeAllStoriesFromFavorite();
            }

            @Override
            public void onError() {

            }
        });
    }

    NetworkClient networkClient;

    private boolean isSandbox = false;

    public final static String IAS_ERROR_TAG = "InAppStory_SDK_error";

    private String getErrorStringFromContext(Context context, @StringRes int resourceId) {
        if (context != null)
            return context.getResources().getString(resourceId);
        return "";
    }

    private InAppStoryManager(final Builder builder) {
        if (builder.context == null) {
            showELog(IAS_ERROR_TAG, "InAppStoryManager.Builder data is not valid. 'context' can't be null");
            return;
        }
        if (builder.apiKey == null &&
                builder.context.getResources().getString(R.string.csApiKey).isEmpty()) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(builder.context, R.string.ias_api_key_error));
            return;
        }
        if (getBytesLength(builder.userId) > 255) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(builder.context, R.string.ias_builder_user_length_error));
            return;
        }
        if (builder.tags != null && getBytesLength(TextUtils.join(",", builder.tags)) > TAG_LIMIT) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(builder.context, R.string.ias_builder_tags_length_error));
            return;
        }
        long freeSpace = builder.context.getCacheDir().getFreeSpace();
        if (freeSpace < MB_5 + MB_10 + MB_10) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(builder.context, R.string.ias_min_free_space_error));
            return;
        }

        KeyValueStorage.setContext(builder.context);
        SharedPreferencesAPI.setContext(builder.context);
        createServiceThread(builder.context, builder.userId);
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            long commonCacheSize = MB_100;
            long fastCacheSize = MB_10;
            switch (builder.cacheSize) {
                case CacheSize.SMALL:
                    fastCacheSize = MB_5;
                    commonCacheSize = MB_10;
                    break;
                case CacheSize.LARGE:
                    commonCacheSize = MB_200;
                    break;
            }
            inAppStoryService.getFastCache().setCacheSize(fastCacheSize);
            inAppStoryService.getCommonCache().setCacheSize(commonCacheSize);
        }
        String domain = new HostFromSecretKey(
                builder.apiKey
        ).get(builder.sandbox);

        this.isSandbox = builder.sandbox;
        initManager(
                builder.context,
                domain,
                builder.apiKey != null ? builder.apiKey : builder.context
                        .getResources().getString(R.string.csApiKey),
                builder.testKey != null ? builder.testKey : null,
                builder.userId,
                builder.tags != null ? builder.tags : null,
                builder.placeholders != null ? builder.placeholders : null,
                builder.imagePlaceholders != null ? builder.imagePlaceholders : null
        );
        new ExceptionManager().sendSavedException();
    }

    private static void generateException() {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.genException = true;
        }
    }

    private int getBytesLength(String value) {
        if (value == null) return 0;
        return value.getBytes(StandardCharsets.UTF_8).length;
    }

    private void setUserIdInner(final String userId, boolean firstTry) {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService == null) {
            if (firstTry)
                localHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setUserIdInner(userId, false);
                    }
                }, 1000);
            return;
        }
        if (userId == null || getBytesLength(userId) > 255) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(context, R.string.ias_setter_user_length_error));
            return;
        }
        if (this.userId.equals(userId)) return;
        localOpensKey = null;
        this.userId = userId;
        if (inAppStoryService.getFavoriteImages() != null)
            inAppStoryService.getFavoriteImages().clear();
        inAppStoryService.getDownloadManager().refreshLocals(Story.StoryType.COMMON);
        inAppStoryService.getDownloadManager().refreshLocals(Story.StoryType.UGC);
        closeStoryReader(CloseReader.AUTO, StatisticManager.AUTO);
        SessionManager.getInstance().closeSession(true);
        OldStatisticManager.getInstance().eventCount = 0;
        inAppStoryService.getDownloadManager().cleanTasks(false);
        inAppStoryService.setUserId(userId);
    }


    //Test

    /**
     * use to change user id in runtime
     *
     * @param userId (userId) - can't be longer than 255 characters
     */
    public void setUserId(@NonNull String userId) {
        setUserIdInner(userId, true);
    }

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void clearCachedList(String id) {
        if (id == null) return;
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.cachedListStories.remove(id);
        }
    }


    public void clearCachedLists() {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.cachedListStories.clear();
        }
    }

    public void setActionBarColor(int actionBarColor) {
        this.actionBarColor = actionBarColor;
    }

    public int actionBarColor = -1;

    public boolean isSendStatistic() {
        return sendStatistic;
    }

    private boolean sendStatistic = true;

    private void initManager(
            Context context,
            String cmsUrl,
            String apiKey,
            String testKey,
            String userId,
            ArrayList<String> tags,
            Map<String, String> placeholders,
            Map<String, ImagePlaceholderValue> imagePlaceholders
    ) {
        this.context = context;
        soundOn = !context.getResources().getBoolean(R.bool.defaultMuted);

        synchronized (tagsLock) {
            this.tags = tags;
        }
        if (placeholders != null)
            setPlaceholders(placeholders);
        if (imagePlaceholders != null)
            setImagePlaceholders(imagePlaceholders);
        this.API_KEY = apiKey;
        this.TEST_KEY = testKey;
        this.userId = userId;
        if (!isNull()) {
            localHandler.removeCallbacksAndMessages(null);
            localDestroy();
        }

        OldStatisticManager.getInstance().statistic = new ArrayList<>();
        setInstance(this);
        if (ApiSettings.getInstance().hostIsDifferent(cmsUrl)) {
            if (networkClient != null) {
                networkClient.clear();
                networkClient = null;
            }
        }
        ApiSettings
                .getInstance()
                .cacheDirPath(context.getCacheDir().getAbsolutePath())
                .apiKey(this.API_KEY)
                .testKey(this.TEST_KEY)
                .host(cmsUrl);

        networkClient = new NetworkClient(context, cmsUrl);
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.getDownloadManager().initDownloaders();
        }
    }

    private static final Object lock = new Object();

    public static void logout() {
        if (!isNull()) {
            InAppStoryService inAppStoryService = InAppStoryService.getInstance();
            if (inAppStoryService != null) {
                inAppStoryService.cachedListStories.clear();
                inAppStoryService.clearSubscribers();
                inAppStoryService.getDownloadManager().cleanTasks();
                inAppStoryService.logout();
            }
        }
    }

    @Deprecated
    public static void destroy() {
        logout();
    }

    private static void localDestroy() {

        logout();
        synchronized (lock) {
            INSTANCE = null;
        }
    }


    private String localOpensKey;

    public String getLocalOpensKey(Story.StoryType type) {
        if (localOpensKey == null && userId != null) {
            localOpensKey = "opened" + userId;
        }
        return (type == Story.StoryType.COMMON) ? localOpensKey : type.name() + localOpensKey;
    }

    public String getLocalOpensKey() {
        return getLocalOpensKey(Story.StoryType.COMMON);
    }

    /**
     * @return current instance of {@link InAppStoryManager}
     */
    public static InAppStoryManager getInstance() {
        synchronized (lock) {
            return INSTANCE;
        }
    }

    /**
     * @return {@link Pair} with version name in first argument and version code in second
     */
    public static Pair<String, Integer> getLibraryVersion() {
        return new Pair<>(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }

    private boolean soundOn = false;

    public void soundOn(boolean isSoundOn) {
        this.soundOn = isSoundOn;
    }

    public boolean soundOn() {
        return soundOn;
    }

    private Handler localHandler = new Handler();
    private Object handlerToken = new Object();

    private void showLoadedOnboardings(final List<Story> response, final Context outerContext,
                                       final AppearanceManager manager, final String feed) {
        Story.StoryType storyType = Story.StoryType.COMMON;
        if (response == null || response.size() == 0) {
            if (CallbackManager.getInstance().getOnboardingLoadCallback() != null) {
                CallbackManager.getInstance().getOnboardingLoadCallback().onboardingLoad(
                        0,
                        StringsUtils.getNonNull(feed)
                );
            }
            return;
        }

        if (InAppStoryService.isNull()) return;
        if (ScreensManager.created == -1) {
            InAppStoryManager.closeStoryReader(CloseReader.AUTO, StatisticManager.AUTO);
            localHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoadedOnboardings(response, outerContext, manager, feed);
                    ScreensManager.created = 0;
                }
            }, 350);
            return;
        } else if (System.currentTimeMillis() - ScreensManager.created < 1000) {
            localHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoadedOnboardings(response, outerContext, manager, feed);
                    ScreensManager.created = 0;
                }
            }, 350);
            return;
        }

        ArrayList<Story> stories = new ArrayList<Story>(response);
        ArrayList<Integer> storiesIds = new ArrayList<>();
        for (Story story : response) {
            storiesIds.add(story.id);
        }
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null)
            inAppStoryService.getDownloadManager().uploadingAdditional(stories, storyType);
        ScreensManager.getInstance().openStoriesReader(
                outerContext,
                null,
                manager,
                storiesIds,
                0,
                SourceType.ONBOARDING,
                feed,
                Story.StoryType.COMMON
        );
        if (CallbackManager.getInstance().getOnboardingLoadCallback() != null) {
            CallbackManager.getInstance().getOnboardingLoadCallback().onboardingLoad(
                    response.size(),
                    StringsUtils.getNonNull(feed)
            );
        }
    }

    private void showOnboardingStoriesInner(final Integer limit, final String feed, final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        if (InAppStoryService.isNull()) {
            localHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStoriesInner(limit, feed, tags, outerContext, manager);
                }
            }, 1000);
            return;
        }

        if (tags != null && getBytesLength(TextUtils.join(",", tags)) > TAG_LIMIT) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(context, R.string.ias_setter_user_length_error));
            return;
        }
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                String localTags = null;
                if (tags != null) {
                    localTags = TextUtils.join(",", tags);
                } else if (getTags() != null) {
                    localTags = TextUtils.join(",", getTags());
                }

                final String onboardUID =
                        ProfilingManager.getInstance().addTask("api_onboarding");
                final String localFeed;
                if (feed != null) localFeed = feed;
                else localFeed = ONBOARDING_FEED;
                networkClient.enqueue(
                        networkClient.getApi().getOnboardingFeed(
                                localFeed,
                                limit,
                                localTags == null ? getTagsString() : localTags
                        ),
                        new LoadFeedCallback() {
                            @Override
                            public void onSuccess(Feed response) {
                                if (InAppStoryManager.isNull()) return;
                                ProfilingManager.getInstance().setReady(onboardUID);
                                List<Story> notOpened = new ArrayList<>();
                                Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
                                if (opens == null) opens = new HashSet<>();
                                List<Story> localStories = response.getStories();
                                for (Story story : localStories) {
                                    boolean add = true;
                                    for (String opened : opens) {
                                        if (Integer.toString(story.id).equals(opened)) {
                                            add = false;
                                        }
                                    }
                                    if (add) notOpened.add(story);
                                }
                                showLoadedOnboardings(notOpened, outerContext, manager, localFeed);
                            }

                            @Override
                            public void onError(int code, String message) {
                                ProfilingManager.getInstance().setReady(onboardUID);
                                loadOnboardingError(localFeed);
                            }

                            @Override
                            public void timeoutError() {
                                ProfilingManager.getInstance().setReady(onboardUID);
                                loadOnboardingError(localFeed);
                            }
                        });
            }

            @Override
            public void onError() {
                loadOnboardingError(feed);
            }

        });
    }

    private void loadOnboardingError(String feed) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().loadOnboardingError(StringsUtils.getNonNull(feed));
        }
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(String feed, List<String> tags, Context outerContext, AppearanceManager manager) {
        if (feed == null || feed.isEmpty()) feed = ONBOARDING_FEED;
        showOnboardingStoriesInner(null, feed, tags, outerContext, manager);
    }


    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(String feed, Context context, final AppearanceManager manager) {
        if (feed == null || feed.isEmpty()) feed = ONBOARDING_FEED;
        showOnboardingStories(feed, getTags(), context, manager);
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(List<String> tags, Context outerContext, AppearanceManager manager) {
        showOnboardingStoriesInner(null, ONBOARDING_FEED, tags, outerContext, manager);
    }

    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(Context context, final AppearanceManager manager) {
        showOnboardingStories(ONBOARDING_FEED, getTags(), context, manager);
    }

    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, String feed, List<String> tags, Context outerContext, AppearanceManager manager) {
        if (feed == null || feed.isEmpty()) feed = ONBOARDING_FEED;
        showOnboardingStoriesInner(limit, feed, tags, outerContext, manager);
    }


    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, String feed, Context context, final AppearanceManager manager) {
        if (feed == null || feed.isEmpty()) feed = ONBOARDING_FEED;
        showOnboardingStories(limit, feed, getTags(), context, manager);
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, List<String> tags, Context outerContext, AppearanceManager manager) {
        showOnboardingStoriesInner(limit, ONBOARDING_FEED, tags, outerContext, manager);
    }

    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, Context context, final AppearanceManager manager) {
        showOnboardingStories(limit, ONBOARDING_FEED, getTags(), context, manager);
    }

    public boolean isSandbox() {
        return isSandbox;
    }

    private final static String ONBOARDING_FEED = "onboarding";

    private String lastSingleOpen = null;

    private void showStoryInner(final String storyId,
                                final Context context,
                                final AppearanceManager manager,
                                final IShowStoryCallback callback,
                                final Integer slide,
                                final Story.StoryType type,
                                final SourceType readerSource,
                                final int readerAction) {
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) {
            localHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStoryInner(
                            storyId,
                            context,
                            manager,
                            callback,
                            slide,
                            type,
                            readerSource,
                            readerAction
                    );
                }
            }, 1000);
            return;
        }
        if (this.userId == null || getBytesLength(this.userId) > 255) {
            showELog(IAS_ERROR_TAG, getErrorStringFromContext(context, R.string.ias_setter_user_length_error));
            return;
        }
        if (lastSingleOpen != null &&
                lastSingleOpen.equals(storyId)) return;
        lastSingleOpen = storyId;


        service.getDownloadManager().getFullStoryByStringId(
                new GetStoryByIdCallback() {
                    @Override
                    public void getStory(Story story) {
                        if (story != null) {
                            service.getDownloadManager().addCompletedStoryTask(story,
                                    Story.StoryType.COMMON);
                            if (ScreensManager.created == -1) {
                                InAppStoryManager.closeStoryReader(CloseReader.AUTO, StatisticManager.AUTO);
                                localHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        lastSingleOpen = null;
                                        showStoryInner(
                                                storyId,
                                                context,
                                                manager,
                                                callback,
                                                slide,
                                                type,
                                                readerSource,
                                                readerAction
                                        );
                                        // StoriesActivity.destroyed = 0;
                                    }
                                }, 500);
                                return;
                            } else if (System.currentTimeMillis() - ScreensManager.created < 1000) {
                                localHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showStoryInner(
                                                storyId,
                                                context,
                                                manager,
                                                callback,
                                                slide,
                                                type,
                                                readerSource,
                                                readerAction
                                        );
                                        ScreensManager.created = 0;
                                    }
                                }, 350);
                                return;
                            }


                            try {
                                int c = Integer.parseInt(lastSingleOpen);
                                if (c != story.id)
                                    return;
                            } catch (Exception ignored) {

                            }
                            if (callback != null)
                                callback.onShow();
                            service.getDownloadManager().putStories(
                                    InAppStoryService.getInstance().getDownloadManager().getStories(Story.StoryType.COMMON),
                                    type
                            );
                            ArrayList<Integer> stIds = new ArrayList<>();
                            stIds.add(story.id);
                            ScreensManager.getInstance().openStoriesReader(
                                    context,
                                    null,
                                    manager,
                                    stIds,
                                    0,
                                    readerSource,
                                    readerAction,
                                    slide,
                                    null,
                                    Story.StoryType.COMMON
                            );
                            localHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    lastSingleOpen = null;
                                }
                            }, 1000);
                        } else {
                            if (callback != null)
                                callback.onError();
                            lastSingleOpen = null;
                            return;
                        }
                    }

                    @Override
                    public void loadError(int type) {
                        if (callback != null)
                            callback.onError();
                        lastSingleOpen = null;
                    }

                },
                storyId,
                type,
                readerSource
        );
    }

    private void showStoryInner(final String storyId, final Context context,
                                final AppearanceManager manager,
                                final IShowStoryCallback callback, Story.StoryType type,
                                final SourceType readerSource,
                                final int readerAction) {
        showStoryInner(storyId, context, manager, callback, null, type, readerSource, readerAction);
    }

    /**
     * use to show single story in reader by id
     *
     * @param storyId  (storyId)
     * @param context  (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager  (manager) {@link AppearanceManager} for reader. May be null
     * @param callback (callback) custom action when story is loaded
     */
    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback) {
        showStoryInner(
                storyId,
                context,
                manager,
                callback,
                Story.StoryType.COMMON,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
    }

    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback, Integer slide) {
        showStoryInner(
                storyId,
                context,
                manager,
                callback,
                slide,
                Story.StoryType.COMMON,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
    }

    /**
     * use to show single story in reader by id
     *
     * @param storyId (storyId)
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showStory(String storyId, Context context, AppearanceManager manager) {
        showStoryInner(
                storyId,
                context,
                manager,
                null,
                Story.StoryType.COMMON,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
    }

    public void showStoryCustom(String storyId, Context context, AppearanceManager manager) {
        showStoryInner(
                storyId,
                context,
                manager,
                null,
                Story.StoryType.COMMON,
                SourceType.SINGLE,
                ShowStory.ACTION_CUSTOM
        );
    }

    public void showStoryWithSlide(
            String storyId,
            Context context,
            Integer slide,
            String managerSettings,
            Story.StoryType type,
            final SourceType readerSource,
            final int readerAction
    ) {
        AppearanceManager appearanceManager = new AppearanceManager();
        if (managerSettings != null) {
            StoriesReaderSettings settings = JsonParser.fromJson(managerSettings, StoriesReaderSettings.class);
            appearanceManager.csHasLike(settings.hasLike);
            appearanceManager.csHasFavorite(settings.hasFavorite);
            appearanceManager.csHasShare(settings.hasShare);
            appearanceManager.csClosePosition(settings.closePosition);
            appearanceManager.csCloseOnOverscroll(settings.closeOnOverscroll);
            appearanceManager.csCloseOnSwipe(settings.closeOnSwipe);
            appearanceManager.csIsDraggable(true);
            appearanceManager.csTimerGradientEnable(settings.timerGradientEnable);
            appearanceManager.csStoryReaderAnimation(settings.readerAnimation);
            appearanceManager.csCloseIcon(settings.closeIcon);
            appearanceManager.csDislikeIcon(settings.dislikeIcon);
            appearanceManager.csLikeIcon(settings.likeIcon);
            appearanceManager.csRefreshIcon(settings.refreshIcon);
            appearanceManager.csFavoriteIcon(settings.favoriteIcon);
            appearanceManager.csShareIcon(settings.shareIcon);
            appearanceManager.csSoundIcon(settings.soundIcon);
        }
        showStoryInner(storyId, context, appearanceManager, null, slide, type, readerSource, readerAction);
    }

    public static class Builder {

        Context context;

        public boolean sandbox() {
            return sandbox;
        }

        public String userId() {
            return userId;
        }

        public String apiKey() {
            return apiKey;
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

        boolean sandbox;

        int cacheSize;
        String userId;
        String apiKey;
        String testKey;
        ArrayList<String> tags;
        Map<String, String> placeholders;
        Map<String, ImagePlaceholderValue> imagePlaceholders;

        public Builder() {
        }

        public Builder context(Context context) {
            Builder.this.context = context;
            return Builder.this;
        }

        @Deprecated
        public Builder sandbox(boolean sandbox) {
            Builder.this.sandbox = sandbox;
            return Builder.this;
        }

        /**
         * use to set available space for file caching (slide images, videos, games, etc.)
         *
         * @param cacheSize (cacheSize) - size of available space for cache. Can be set with {@link CacheSize} constants
         *                  {@link CacheSize#SMALL} - 10mb for stories, 5mb fo story covers
         *                  {@link CacheSize#MEDIUM} - (by default) 100mb for stories, 10mb fo story covers
         *                  {@link CacheSize#LARGE} -  200mb for stories, 10mb fo story covers
         * @return {@link Builder}
         */
        public Builder cacheSize(int cacheSize) {
            Builder.this.cacheSize = cacheSize;
            return Builder.this;
        }

        /**
         * use to set api key in runtime (or as alternate to csApiKey string constant)
         *
         * @param apiKey (apiKey) value for api key
         *               false by default
         * @return {@link Builder}
         */
        public Builder apiKey(String apiKey) {
            Builder.this.apiKey = apiKey;
            return Builder.this;
        }

        public Builder testKey(String testKey) {
            Builder.this.testKey = testKey;
            return Builder.this;
        }

        /**
         * use to set user id.
         *
         * @param userId (userId) value for user id. Can't be longer than 255 characters.
         * @return {@link Builder}
         */
        public Builder userId(@NonNull String userId) {
            Builder.this.userId = userId;
            return Builder.this;
        }

        /**
         * @param tags (tags) tags for targeting stories
         * @return {@link Builder}
         */
        public Builder tags(String... tags) {
            Builder.this.tags = new ArrayList<>();
            for (int i = 0; i < tags.length; i++) {
                Builder.this.tags.add(tags[i]);
            }
            return Builder.this;
        }

        /**
         * @param tags (tags) tags for targeting stories
         * @return {@link Builder}
         */
        public Builder tags(ArrayList<String> tags) {
            Builder.this.tags = tags;
            return Builder.this;
        }

        /**
         * @param placeholders (placeholders) placeholders for default values in stories
         * @return {@link Builder}
         */
        public Builder placeholders(Map<String, String> placeholders) {
            Builder.this.placeholders = placeholders;
            return Builder.this;
        }

        /**
         * @param placeholders (placeholders) - placeholders for default values in stories
         * @return {@link Builder}
         */
        public Builder imagePlaceholders(Map<String, ImagePlaceholderValue> placeholders) {
            Builder.this.imagePlaceholders = placeholders;
            return Builder.this;
        }

        /**
         * main method to create {@link InAppStoryManager} instance.
         *
         * @return {@link InAppStoryManager}
         */
        public InAppStoryManager create() {
            return new InAppStoryManager(Builder.this);
        }
    }
}
