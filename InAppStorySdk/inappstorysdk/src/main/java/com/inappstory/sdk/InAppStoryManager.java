package com.inappstory.sdk;

import static com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache.MB_5;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.lrudiskcache.CacheSize;
import com.inappstory.sdk.core.utils.network.ApiSettings;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.core.utils.network.utils.HostFromSecretKey;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.core.models.ExceptionCache;
import com.inappstory.sdk.core.models.ImagePlaceholderValue;
import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.core.models.StoryPlaceholder;
import com.inappstory.sdk.core.models.logs.ApiLogRequest;
import com.inappstory.sdk.core.models.logs.ApiLogResponse;
import com.inappstory.sdk.core.models.logs.ExceptionLog;
import com.inappstory.sdk.core.models.logs.WebConsoleLog;
import com.inappstory.sdk.stories.callbacks.AppClickCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.ExceptionCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
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
import com.inappstory.sdk.stories.outercallbacks.screen.IOpenStoriesReader;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.core.utils.sharedpref.SharedPreferencesAPI;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.StoriesReaderSettings;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.utils.StringsUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public void setOpenStoriesReader(IOpenStoriesReader openStoriesReader) {
        IASCore.getInstance().setOpenStoriesReader(openStoriesReader);
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
        IASCore.getInstance().downloadManager.clearCache();
    }
    //Test

    /**
     * use to clear downloaded files and in-app cache without manager
     */
    public void clearCache(Context context) {
        IASCore.getInstance().downloadManager.clearCache();
    }

    /**
     * use to force close story reader
     */
    public static void closeStoryReader() {
        closeStoryReader(CloseReader.CUSTOM, StatisticV2Manager.CUSTOM);
    }

    @Deprecated
    public void openGame(String gameId) {
        IASCore.getInstance().gameRepository.openGameReaderWithGC(context, null, gameId);
    }

    public void openGame(String gameId, @NonNull Context context) {
        IASCore.getInstance().gameRepository.openGameReaderWithGC(context, null, gameId);
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
        if (tags != null && StringsUtils.getBytesLength(TextUtils.join(",", tags)) > TAG_LIMIT) {
            showELog(
                    IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            R.string.ias_setter_tags_length_error
                    )
            );
            return;
        }
        synchronized (tagsLock) {
            this.tags = tags;
            clearCachedLists();
        }
    }


    private final static int TAG_LIMIT = IASCore.TAG_LIMIT;

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
            if (StringsUtils.getBytesLength(oldTagsString + newTagsString) > TAG_LIMIT - 1) {
                showELog(
                        IAS_ERROR_TAG,
                        StringsUtils.getErrorStringFromContext(
                                context,
                                R.string.ias_setter_tags_length_error
                        )
                );
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

    public void setDefaultPlaceholders(@NonNull List<StoryPlaceholder> placeholders) {
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
            if (imagePlaceholders == null)
                imagePlaceholders = new HashMap<>();
            else
                imagePlaceholders.clear();
            imagePlaceholders.putAll(placeholders);
        }
    }

    public void setDefaultImagePlaceholder(@NonNull String key, @NonNull ImagePlaceholderValue value) {
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

    public void removeFromFavorite(final int storyId) {
        IASCore.getInstance().getStoriesRepository(StoryType.COMMON)
                .removeFromFavorite(storyId);
    }

    public void removeAllFavorites() {
        IASCore.getInstance().getStoriesRepository(StoryType.COMMON).removeAllFavorites();
    }

    NetworkClient networkClient;

    private boolean isSandbox = false;

    public final static String IAS_ERROR_TAG = "InAppStory_SDK_error";

    public static void initSDK(@NonNull Context context) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean calledFromApplication = false;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            try {
                if (Application.class.isAssignableFrom(Class.forName(stackTraceElement.getClassName()))) {
                    calledFromApplication = true;
                }
            } catch (ClassNotFoundException e) {

            }
        }
        if (!(context instanceof Application)) calledFromApplication = false;
        if (!calledFromApplication)
            showELog(IAS_ERROR_TAG, "Method must be called from Application class and context has to be an applicationContext");
        synchronized (lock) {
            IASCore.getInstance().init(context);
            if (INSTANCE == null) {
                INSTANCE = new InAppStoryManager(context);
            }
        }
    }

    private InAppStoryManager(Context context) {
        if (context == null) {
            showELog(IAS_ERROR_TAG, "InAppStoryManager.Builder data is not valid. 'context' can't be null");
            return;
        }
        KeyValueStorage.setContext(context);
        SharedPreferencesAPI.setContext(context);
        this.context = context;
    }

    private void build(final Builder builder) {
        if (builder.apiKey == null && context.getResources().getString(R.string.csApiKey).isEmpty()) {
            showELog(
                    IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            R.string.ias_api_key_error
                    )
            );
            return;
        }
        if (StringsUtils.getBytesLength(builder.userId) > 255) {
            showELog(
                    IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            R.string.ias_builder_user_length_error
                    )
            );
            return;
        }
        if (builder.tags != null && StringsUtils.getBytesLength(
                TextUtils.join(",", builder.tags)
        ) > TAG_LIMIT) {
            showELog(
                    IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            R.string.ias_builder_tags_length_error
                    )
            );
            return;
        }
        long freeSpace = context.getCacheDir().getFreeSpace();
        if (freeSpace < MB_5 + MB_10 + MB_10) {
            showELog(
                    IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            R.string.ias_min_free_space_error
                    )
            );
            return;
        }


        IASCore.getInstance().initFilesRepository(context, builder.cacheSize);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
        String domain = new HostFromSecretKey(
                builder.apiKey
        ).get(builder.sandbox);

        this.isSandbox = builder.sandbox;
        initManager(
                domain,
                builder.apiKey != null ? builder.apiKey : context
                        .getResources().getString(R.string.csApiKey),
                builder.testKey != null ? builder.testKey : null,
                builder.userId,
                builder.tags != null ? builder.tags : null,
                builder.placeholders != null ? builder.placeholders : null,
                builder.imagePlaceholders != null ? builder.imagePlaceholders : null
        );
        new ExceptionManager().sendSavedException();
    }


    private void setUserIdInner(final String userId) {
        if (userId == null || StringsUtils.getBytesLength(userId) > 255) {
            showELog(
                    IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            R.string.ias_setter_user_length_error
                    )
            );
            return;
        }
        if (this.userId.equals(userId)) return;
        this.userId = userId;
        closeStoryReader(CloseReader.AUTO, StatisticV2Manager.AUTO);
        IASCore.getInstance().setUserId(userId);
    }


    //Test

    /**
     * use to change user id in runtime
     *
     * @param userId (userId) - can't be longer than 255 characters
     */
    public void setUserId(@NonNull String userId) {
        setUserIdInner(userId);
    }

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void clearCachedList(String id) {
        if (id == null) return;
        IASCore.getInstance().getStoriesRepository(StoryType.COMMON).clearCachedList(id);
        IASCore.getInstance().getStoriesRepository(StoryType.UGC).clearCachedList(id);
    }


    public void clearCachedLists() {
        IASCore.getInstance().getStoriesRepository(StoryType.COMMON).clearCachedLists();
        IASCore.getInstance().getStoriesRepository(StoryType.UGC).clearCachedLists();
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
            String cmsUrl,
            String apiKey,
            String testKey,
            String userId,
            ArrayList<String> tags,
            Map<String, String> placeholders,
            Map<String, ImagePlaceholderValue> imagePlaceholders
    ) {
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
        IASCore.getInstance().setUserId(userId);
        ApiSettings
                .getInstance()
                .cacheDirPath(context.getCacheDir().getAbsolutePath())
                .apiKey(this.API_KEY)
                .testKey(this.TEST_KEY)
                .host(cmsUrl);
        if (IASCore.getInstance().getNetworkClient() == null ||
                ApiSettings.getInstance().hostIsDifferent(cmsUrl)) {
            IASCore.getInstance().setNetworkClient(new NetworkClient(context, cmsUrl));
        }
    }

    private static final Object lock = new Object();

    public static void logout() {
        IASCore.getInstance().getStoriesRepository(StoryType.COMMON).clearCachedLists();
        IASCore.getInstance().closeSession();
        IASCore.getInstance().downloadManager.cleanTasks();
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

    private Handler localHandler = new Handler();

    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(String feed, List<String> tags, Context outerContext, AppearanceManager manager) {
        if (feed == null || feed.isEmpty()) feed = ONBOARDING_FEED;
        IASCore.getInstance().showOnboardingStories(
                null,
                feed,
                tags,
                getTags(),
                outerContext,
                manager
        );
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
        IASCore.getInstance().showOnboardingStories(
                null,
                ONBOARDING_FEED,
                tags,
                getTags(),
                outerContext,
                manager
        );
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
        IASCore.getInstance().showOnboardingStories(
                limit,
                feed,
                tags,
                getTags(),
                outerContext,
                manager
        );
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

        IASCore.getInstance().showOnboardingStories(
                limit,
                ONBOARDING_FEED,
                tags,
                getTags(),
                outerContext,
                manager
        );
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

    /**
     * use to show single story in reader by id
     *
     * @param storyId  (storyId)
     * @param context  (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager  (manager) {@link AppearanceManager} for reader. May be null
     * @param callback (callback) custom action when story is loaded
     */
    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback) {
        IASCore.getInstance().showSingleStory(
                storyId,
                false,
                context,
                manager,
                callback,
                null,
                StoryType.COMMON,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
    }


    public void showStory(
            String storyId,
            Context context,
            AppearanceManager manager,
            IShowStoryCallback callback,
            Integer slide
    ) {
        IASCore.getInstance().showSingleStory(
                storyId,
                false,
                context,
                manager,
                callback,
                slide,
                StoryType.COMMON,
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
        IASCore.getInstance().showSingleStory(
                storyId,
                false,
                context,
                manager,
                null,
                null,
                StoryType.COMMON,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
    }

    public void showStoryOnce(String storyId,
                              Context context,
                              AppearanceManager manager,
                              IShowStoryOnceCallback callback
    ) {
        IASCore.getInstance().showSingleStory(
                storyId,
                true,
                context,
                manager,
                callback,
                0,
                StoryType.COMMON,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
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
            synchronized (lock) {
                if (INSTANCE == null) {
                    showELog(IAS_ERROR_TAG, "Method InAppStoryManager.init must be called from Application class");
                    return null;
                }
            }
            INSTANCE.build(Builder.this);
            return INSTANCE;
        }
    }
}
