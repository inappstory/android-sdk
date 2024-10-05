package com.inappstory.sdk;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.inappstory.iasutilsconnector.UtilModulesHolder;
import com.inappstory.iasutilsconnector.json.IJsonParser;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.IASCoreImpl;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.ui.screens.holder.GetScreenCallback;
import com.inappstory.sdk.lrudiskcache.CacheSize;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.network.utils.HostFromSecretKey;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.api.models.logs.WebConsoleLog;
import com.inappstory.sdk.stories.callbacks.ExceptionCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenGameReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenInAppMessageReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.stackfeed.IStackFeedActions;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;
import com.inappstory.sdk.stories.stackfeed.IStackStoryData;
import com.inappstory.sdk.stories.stackfeed.StackStoryObserver;
import com.inappstory.sdk.stories.stackfeed.StackStoryUpdatedCallback;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


/**
 * Main class for work with SDK.
 * Need to initialize it first with {@link Builder} before other interactions.
 * Singleton class, can be available with {@link #getInstance()}.
 * Can be reinitialized.
 */
public class InAppStoryManager {

    private static InAppStoryManager INSTANCE;

    private final IASCore core = new IASCoreImpl();

    public IASCore iasCore() {
        return core;
    }

    public static void useCore(UseIASCoreCallback callback) {
        synchronized (lock) {
            if (INSTANCE == null) {
                callback.error();
            } else {
                callback.use(INSTANCE.iasCore());
            }
        }
    }


    public static NetworkClient getNetworkClient() {
        synchronized (lock) {
            if (INSTANCE == null) return null;
            return INSTANCE.networkClient;
        }
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

    public static boolean isNull() {
        synchronized (lock) {
            return INSTANCE == null;
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
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.sessionManager().getSession().assetsIsCleared();
            }
        });
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                service.getStoryDownloadManager().clearCache();
            }
        });
    }
    //Test

    /**
     * use to clear downloaded files and in-app cache without manager
     */
    public void clearCache(Context context) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.sessionManager().getSession().assetsIsCleared();
            }
        });
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                service.getStoryDownloadManager().clearCache();
            }
        });
    }

    /**
     * use to close story reader
     */
    public static void closeStoryReader() {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager()
                        .getStoryScreenHolder()
                        .closeScreenWithAction(CloseStory.CUSTOM);
            }
        });
    }

    /**
     * use to close story reader
     *
     * @param forceClose               (forceClose) - close reader immediately without animation
     * @param forceCloseReaderCallback (forceCloseReaderCallback) - triggers after reader is closed and only if {@code forceClose == true}
     */
    public static void closeStoryReader(final boolean forceClose, final ForceCloseReaderCallback forceCloseReaderCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                if (forceClose) {
                    core.screensManager()
                            .forceCloseAllReaders(forceCloseReaderCallback);
                } else {
                    core.screensManager().getStoryScreenHolder()
                            .closeScreenWithAction(CloseStory.CUSTOM);
                }
            }
        });
    }


    public void openGame(final String gameId, @NonNull Context context) {
        core.gamesAPI().open(context, gameId);
    }

    public static boolean isStoryReaderOpened() {
        return getInstance() != null && getInstance()
                .core
                .screensManager()
                .getStoryScreenHolder()
                .isOpened();
    }

    public static boolean isGameReaderOpened() {
        return getInstance() != null && getInstance()
                .core
                .screensManager()
                .getGameScreenHolder()
                .isOpened();
    }

    public static boolean isIAMReaderOpened() {
        return getInstance() != null && getInstance()
                .core
                .screensManager()
                .getIAMScreenHolder()
                .isOpened();
    }

    public void closeGame() {
        core.gamesAPI().close();
    }

    /**
     * use to force close story reader
     */

    /**
     * use to set callback on different errors
     */
    public void setErrorCallback(ErrorCallback errorCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.ERROR, errorCallback);
    }

    /**
     * use to set callback on share click
     */
    public void setClickOnShareStoryCallback(ClickOnShareStoryCallback clickOnShareStoryCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.CLICK_SHARE, clickOnShareStoryCallback);
    }


    /**
     * use to set callback on game start/close/finish
     */
    public void setGameReaderCallback(GameReaderCallback gameReaderCallback) {
        core.gamesAPI().callback(gameReaderCallback);
    }

    /**
     * use to set callback on onboardings load
     */
    public void setOnboardingLoadCallback(OnboardingLoadCallback onboardingLoadCallback) {
        core.onboardingsAPI().loadCallback(onboardingLoadCallback);
    }

    /**
     * use to set callback on click on buttons in stories (with info)
     */
    public void setCallToActionCallback(CallToActionCallback callToActionCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.CALL_TO_ACTION, callToActionCallback);
    }

    /**
     * use to set callback on click on widgets in stories (with info)
     */
    public void setStoryWidgetCallback(StoryWidgetCallback storyWidgetCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.STORY_WIDGET, storyWidgetCallback);
    }


    /**
     * use to set callback on stories reader closing
     */
    public void setCloseStoryCallback(CloseStoryCallback closeStoryCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.CLOSE_STORY, closeStoryCallback);
    }

    /**
     * use to set callback on favorite action
     */
    public void setFavoriteStoryCallback(FavoriteStoryCallback favoriteStoryCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.FAVORITE, favoriteStoryCallback);
    }

    /**
     * use to set callback on like/dislike action
     */
    public void setLikeDislikeStoryCallback(LikeDislikeStoryCallback likeDislikeStoryCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.LIKE_DISLIKE, likeDislikeStoryCallback);
    }

    /**
     * use to set callback on slide shown in reader
     */
    public void setShowSlideCallback(ShowSlideCallback showSlideCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.SHOW_SLIDE, showSlideCallback);
    }

    /**
     * use to set callback on story shown in reader
     */
    public void setShowStoryCallback(ShowStoryCallback showStoryCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.SHOW_STORY, showStoryCallback);
    }

    /**
     * use to set callback on single story loading
     */
    public void setSingleLoadCallback(SingleLoadCallback singleLoadCallback) {
        core.singleStoryAPI().loadCallback(singleLoadCallback);
    }


    /**
     * use to customize share functional
     */
    public void setShareCallback(ShareCallback shareCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.SHARE_ADDITIONAL, shareCallback);
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
        core.settingsAPI().setTags(tags);
    }


    private final static int TAG_LIMIT = 4000;

    private final Object tagsLock = new Object();

    /**
     * use to customize tags in runtime. Adds tags to array.
     *
     * @param newTags (newTags) - list of additional tags
     */

    public void addTags(ArrayList<String> newTags) {
        core.settingsAPI().addTags(newTags);
    }

    /**
     * use to customize tags in runtime. Removes tags from array.
     *
     * @param removedTags (removedTags) - list of removing tags
     */

    public void removeTags(ArrayList<String> removedTags) {
        core.settingsAPI().removeTags(removedTags);
    }

    /**
     * use to customize default string in stories runtime.
     *
     * @param key   (key) - what we replace
     * @param value (value) - replacement result
     */
    public void setPlaceholder(String key, String value) {
        core.settingsAPI().setPlaceholder(key, value);
    }

    /**
     * use to customize default strings in stories runtime.
     *
     * @param newPlaceholders (newPlaceholders) - key-value map (key - what we replace, value - replacement result)
     */
    public void setPlaceholders(@NonNull Map<String, String> newPlaceholders) {
        core.settingsAPI().setPlaceholders(newPlaceholders);
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
        //forceCloseAndClearCache();
    }

    private boolean setNewImagePlaceholder(@NonNull String key, ImagePlaceholderValue value) {
        if (Objects.equals(imagePlaceholders.get(key), value)) {
            return false;
        }
        imagePlaceholders.put(key, value);
        return true;
    }


    void setDefaultImagePlaceholders(@NonNull Map<String, ImagePlaceholderValue> placeholders) {
        synchronized (placeholdersLock) {
            if (defaultImagePlaceholders == null) defaultImagePlaceholders = new HashMap<>();
            defaultImagePlaceholders.clear();
            defaultImagePlaceholders.putAll(placeholders);
        }
    }


    public void setImagePlaceholder(@NonNull String key, ImagePlaceholderValue value) {
        core.settingsAPI().setImagePlaceholder(key, value);
    }

    Map<String, String> placeholders = new HashMap<>();
    Map<String, ImagePlaceholderValue> imagePlaceholders = new HashMap<>();

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


    public UtilModulesHolder utilModulesHolder;


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
        if (!calledFromApplication)
            showELog(IAS_ERROR_TAG, "Method must be called from Application class and context has to be an applicationContext");
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

    public static void initSDK(@NonNull Context context) {
        initSDK(context, false);
    }

    InAppStoryService service;

    Thread serviceThread;

    private InAppStoryManager(Context context) {
        this.context = context;
        KeyValueStorage.setContext(context);
        SharedPreferencesAPI.setContext(context);
        this.soundOn = !context.getResources().getBoolean(R.bool.defaultMuted);
    }


    void createServiceThread(final Context context) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.onDestroy();
            }
        });
        if (serviceThread != null) {
            serviceThread.interrupt();
            serviceThread = null;
        }
        serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                service = new InAppStoryService(core);
                service.onCreate(context, CacheSize.MEDIUM, exceptionCache);
                Looper.loop();
            }
        });
        serviceThread.start();
    }

    void setExceptionCache(ExceptionCache exceptionCache) {
        this.exceptionCache = exceptionCache;
    }

    private ExceptionCache exceptionCache;

    public void removeFromFavorite(final int storyId) {
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(String sessionId) {
                favoriteOrRemoveStory(storyId, false);
            }

            @Override
            public void onError() {

            }
        });
    }

    public void removeAllFavorites() {
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(String sessionId) {
                favoriteRemoveAll();
            }

            @Override
            public void onError() {

            }
        });
    }

    NetworkClient networkClient;

    private void favoriteRemoveAll() {
        if (networkClient == null) return;
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {

                final String favUID = core.statistic().profiling().addTask("api_favorite_remove_all");
                networkClient.enqueue(
                        networkClient.getApi().removeAllFavorites(),
                        new NetworkCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {
                                core.statistic().profiling().setReady(favUID);
                                service.getStoryDownloadManager()
                                        .clearAllFavoriteStatus(Story.StoryType.COMMON);
                                service.getStoryDownloadManager()
                                        .clearAllFavoriteStatus(Story.StoryType.UGC);
                                service.getFavoriteImages().clear();
                                service.getListReaderConnector().clearAllFavorites();
                                core.screensManager().getStoryScreenHolder()
                                        .useCurrentReader(
                                                new GetScreenCallback<BaseStoryScreen>() {
                                                    @Override
                                                    public void get(BaseStoryScreen screen) {
                                                        screen.removeAllStoriesFromFavorite();
                                                    }
                                                });
                            }

                            @Override
                            public void onError(int code, String message) {
                                core.statistic().profiling().setReady(favUID);
                                super.onError(code, message);
                            }

                            @Override
                            public void timeoutError() {
                                super.timeoutError();
                                core.statistic().profiling().setReady(favUID);
                            }

                            @Override
                            public Type getType() {
                                return null;
                            }
                        });
            }
        });

    }


    private void favoriteOrRemoveStory(final int storyId, final boolean favorite) {
        if (networkClient == null) return;
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
                final String favUID = core.statistic().profiling().addTask("api_favorite");
                networkClient.enqueue(
                        networkClient.getApi().storyFavorite(Integer.toString(storyId), favorite ? 1 : 0),
                        new NetworkCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {
                                core.statistic().profiling().setReady(favUID);
                                Story story = service.getStoryDownloadManager()
                                        .getStoryById(storyId, Story.StoryType.COMMON);
                                if (story != null)
                                    story.favorite = favorite;
                                service.getListReaderConnector().storyFavorite(storyId, favorite);
                                core
                                        .screensManager()
                                        .getStoryScreenHolder()
                                        .useCurrentReader(
                                                new GetScreenCallback<BaseStoryScreen>() {
                                                    @Override
                                                    public void get(BaseStoryScreen screen) {
                                                        screen.removeStoryFromFavorite(storyId);
                                                    }
                                                }
                                        );
                            }

                            @Override
                            public void onError(int code, String message) {
                                core.statistic().profiling().setReady(favUID);
                                super.onError(code, message);
                            }

                            @Override
                            public void timeoutError() {
                                super.timeoutError();
                                core.statistic().profiling().setReady(favUID);
                            }

                            @Override
                            public Type getType() {
                                return null;
                            }
                        });
            }
        });

    }

    private boolean isSandbox = false;

    public final static String IAS_ERROR_TAG = "InAppStory_SDK_error";


    private void build(final Builder builder) {
        Context context = this.context;
        Integer errorStringId = null;
        if (context == null) {
            errorStringId = R.string.ias_context_is_null;
        } else if (builder.apiKey == null && context.getResources().getString(R.string.csApiKey).isEmpty()) {
            errorStringId = R.string.ias_api_key_error;
        } else if (StringsUtils.getBytesLength(builder.userId) > 255) {
            errorStringId = R.string.ias_builder_user_length_error;
        } else if (builder.tags != null && StringsUtils.getBytesLength(TextUtils.join(",", builder.tags)) > TAG_LIMIT) {
            errorStringId = R.string.ias_builder_tags_length_error;
        }
        if (errorStringId != null) {
            showELog(
                    IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            errorStringId
                    )
            );
            return;
        }
        long freeSpace = context.getCacheDir().getFreeSpace();
        if (freeSpace < MB_5 + MB_10 + MB_10) {
            showELog(IAS_ERROR_TAG, StringsUtils.getErrorStringFromContext(context,
                    R.string.ias_min_free_space_error));
            return;
        }
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService != null) {
            inAppStoryService.setUserId(builder.userId);
            inAppStoryService.setCacheSizes(context);
        }
        String domain = new HostFromSecretKey(
                builder.apiKey
        ).get(builder.sandbox);

        this.isSandbox = builder.sandbox;
        initManager(
                context,
                domain,
                builder.apiKey() != null ? builder.apiKey() : context.getResources().getString(R.string.csApiKey),
                builder.testKey() != null ? builder.testKey() : null,
                builder.userId(),
                builder.locale(),
                builder.gameDemoMode(),
                builder.isDeviceIdEnabled(),
                builder.tags() != null ? builder.tags() : null,
                builder.placeholders() != null ? builder.placeholders() : null,
                builder.imagePlaceholders() != null ? builder.imagePlaceholders() : null
        );
        new ExceptionManager(core).sendSavedException();
    }

    public void preloadGames() {
        core.contentPreload().restartGamePreloader();
    }


    public Locale getCurrentLocale() {
        return currentLocale;
    }

    private Locale currentLocale = Locale.getDefault();

    public void setLang(@NonNull Locale lang) {
        core.settingsAPI().setLang(lang);
    }


    /**
     * use to change user id in runtime
     *
     * @param userId (userId) - can't be longer than 255 characters
     */
    public void setUserId(@NonNull String userId) {
        core.settingsAPI().setUserId(userId);
    }

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void clearCachedList(String id) {
        core.storiesListVMHolder().removeVM(id);
    }

    public void setOpenStoriesReader(@NonNull IOpenStoriesReader openStoriesReader) {
        core.screensManager().setOpenStoriesReader(openStoriesReader);
    }

    public void setOpenInAppMessageReader(@NonNull IOpenInAppMessageReader openInAppMessageReader) {
        core.screensManager().setOpenInAppMessageReader(openInAppMessageReader);
    }

    public void setOpenGameReader(@NonNull IOpenGameReader openGameReader) {
        core.screensManager().setOpenGameReader(openGameReader);
    }

    public void clearCachedLists() {
        core.storiesListVMHolder().clear();
    }

    public boolean isSendStatistic() {
        return sendStatistic;
    }

    private boolean sendStatistic = true;

    public boolean isGameDemoMode() {
        return gameDemoMode;
    }

    private boolean gameDemoMode = false;

    public boolean isDeviceIDEnabled() {
        return isDeviceIDEnabled;
    }

    private boolean isDeviceIDEnabled = true;

    private void initManager(
            Context context,
            String cmsUrl,
            String apiKey,
            String testKey,
            String userId,
            Locale locale,
            boolean gameDemoMode,
            boolean isDeviceIDEnabled,
            ArrayList<String> tags,
            Map<String, String> placeholders,
            Map<String, ImagePlaceholderValue> imagePlaceholders
    ) {
        this.context = context;
        soundOn = !context.getResources().getBoolean(R.bool.defaultMuted);
        this.isDeviceIDEnabled = isDeviceIDEnabled;
        this.currentLocale = locale;
        this.gameDemoMode = gameDemoMode;
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
        logout();
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
    }


    private static final Object lock = new Object();

    public static void logout() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                core.storiesListVMHolder().clear();
                InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                    @Override
                    public void use(@NonNull final InAppStoryService inAppStoryService) throws Exception {
                        inAppStoryService.getListSubscribers().clear();
                        inAppStoryService.getStoryDownloadManager().cleanTasks();
                        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
                            @Override
                            public void use(@NonNull InAppStoryManager manager) throws Exception {
                                core.screensManager().forceCloseAllReaders(
                                        new ForceCloseReaderCallback() {
                                            @Override
                                            public void onComplete() {
                                                inAppStoryService.logout();
                                            }
                                        }
                                );
                            }
                        });
                    }
                });
            }
        });

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


    public void getStackFeed(
            final String feed,
            final String uniqueStackId,
            final List<String> tags,
            final AppearanceManager appearanceManager,
            final IStackFeedResult stackFeedResult
    ) {
        core.stackFeedAPI().get(feed, uniqueStackId, appearanceManager, tags, stackFeedResult);
    }

    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(String feed, List<String> tags, Context outerContext, AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, feed, manager, tags, 1000);
    }


    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(String feed, Context outerContext, final AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, feed, manager, null, 1000);
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(List<String> tags, Context outerContext, AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, null, manager, tags, 1000);
    }

    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(Context outerContext, final AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, null, manager, null, 1000);
    }

    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, String feed, List<String> tags, Context outerContext, AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, feed, manager, tags, limit);
    }


    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, String feed, Context outerContext, final AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, feed, manager, null, limit);
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, List<String> tags, Context outerContext, AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, null, manager, tags, limit);
    }

    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(int limit, Context outerContext, final AppearanceManager manager) {
        core.onboardingsAPI().show(outerContext, null, manager, null, limit);
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
        core.singleStoryAPI().show(context, storyId, manager, callback, 0);
    }

    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback, Integer slide) {
        core.singleStoryAPI().show(context, storyId, manager, callback, slide);
    }


    public void showStoryOnce(String storyId,
                              Context context,
                              AppearanceManager manager,
                              IShowStoryOnceCallback callback
    ) {
        core.singleStoryAPI().showOnce(context, storyId, manager, callback);
    }

    /**
     * use to show single story in reader by id
     *
     * @param storyId (storyId)
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showStory(String storyId, Context context, AppearanceManager manager) {
        core.singleStoryAPI().show(context, storyId, manager, null, 0);
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
            Builder.this.sandbox = sandbox;
            return Builder.this;
        }

        public Builder lang(Locale locale) {
            Builder.this.locale = locale;
            return Builder.this;
        }

        public Builder gameDemoMode(boolean gameDemoMode) {
            Builder.this.gameDemoMode = gameDemoMode;
            return Builder.this;
        }

        public Builder isDeviceIDEnabled(boolean deviceIdEnabled) {
            Builder.this.deviceIdEnabled = deviceIdEnabled;
            return Builder.this;
        }

        /**
         * use to set available space for file caching (slide images, videos, games, etc.)
         *
         * @param cacheSize (cacheSize) - size of available space for cache. Can be set with {@link CacheSize} constants
         *                  {@link com.inappstory.sdk.lrudiskcache.CacheSize#SMALL} - 10mb for stories, 5mb fo story covers
         *                  {@link com.inappstory.sdk.lrudiskcache.CacheSize#MEDIUM} - (by default) 100mb for stories, 10mb fo story covers
         *                  {@link com.inappstory.sdk.lrudiskcache.CacheSize#LARGE} -  200mb for stories, 10mb fo story covers
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
