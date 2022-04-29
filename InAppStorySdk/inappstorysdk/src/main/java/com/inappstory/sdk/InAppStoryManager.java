package com.inappstory.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.lrudiskcache.CacheSize;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.logs.ApiLogRequest;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.api.models.logs.WebConsoleLog;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameCallback;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.callbacks.AppClickCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.ExceptionCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.callbacks.UrlClickCallback;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.OnboardingLoad;
import com.inappstory.sdk.stories.outerevents.OnboardingLoadError;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.StoriesReaderSettings;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.SessionManager;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_200;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;


/**
 * Main class for work with SDK.
 * Need to initialize it first with {@link Builder} before other interactions.
 * Singleton class, can be available with {@link #getInstance()}.
 * Can be reinitialized.
 */
public class InAppStoryManager {

    private static InAppStoryManager INSTANCE;

    public static boolean testGenerated = false;

    public static boolean isNull() {
        return INSTANCE == null;
    }

    public static void setInstance(InAppStoryManager manager) {
        INSTANCE = manager;
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
        if (logger != null) logger.showELog(tag, message);
    }

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
        if (ImageLoader.getInstance() != null) {
            ImageLoader.getInstance().clearCache();
        }
        InAppStoryService.getInstance().getDownloadManager().clearCache();
    }

    /**
     * use to force close story reader
     */
    public static void closeStoryReader() {
        closeStoryReader(CloseStory.CUSTOM);
    }

    /**
     * use to force close story reader
     */
    public static void closeStoryReader(final int action) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ScreensManager.getInstance().closeStoryReader(action);
                ScreensManager.getInstance().hideGoods();
                ScreensManager.getInstance().closeGameReader();
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
    public void setGameCallback(GameCallback gameCallback) {
        CallbackManager.getInstance().setGameCallback(gameCallback);
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

        synchronized (tagsLock) {
            this.tags = tags;
        }
    }

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
            for (String tag : newTags) {
                addTag(tag);
            }
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
            String inKey = "%" + key + "%";
            if (value == null) {
                if (defaultPlaceholders.containsKey(inKey)) {
                    placeholders.put(inKey, defaultPlaceholders.get(inKey));
                } else {
                    placeholders.remove(inKey);
                }
            } else {
                placeholders.put(inKey, value);
            }
        }
    }

    /**
     * use to customize default strings in stories runtime.
     *
     * @param placeholders (placeholders) - key-value map (key - what we replace, value - replacement result)
     */
    public void setPlaceholders(@NonNull Map<String, String> placeholders) {
        getPlaceholders().clear();
        for (String placeholderKey : placeholders.keySet()) {
            setPlaceholder(placeholderKey, placeholders.get(placeholderKey));
        }
    }

    ArrayList<String> tags;

    public Object placeholdersLock = new Object();

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

    Map<String, String> placeholders = new HashMap<>();

    public Map<String, String> getDefaultPlaceholders() {
        synchronized (placeholdersLock) {
            if (defaultPlaceholders == null) defaultPlaceholders = new HashMap<>();
            if (placeholders == null) placeholders = new HashMap<>();
            return defaultPlaceholders;
        }
    }

    Map<String, String> defaultPlaceholders = new HashMap<>();

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
        if (InAppStoryService.isNull()) return;
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                favoriteOrRemoveStory(storyId, false);
            }

            @Override
            public void onError() {

            }
        });
    }

    public void removeAllFavorites() {
        if (InAppStoryService.isNull()) return;
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                favoriteRemoveAll();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void favoriteRemoveAll() {
        if (InAppStoryService.isNull()) return;
        final String favUID = ProfilingManager.getInstance().addTask("api_favorite_remove_all");
        NetworkClient.getApi().removeAllFavorites().enqueue(new NetworkCallback<Response>() {
            @Override
            public void onSuccess(Response response) {
                ProfilingManager.getInstance().setReady(favUID);
                if (InAppStoryService.isNotNull()) {
                    InAppStoryService.getInstance().getDownloadManager().clearAllFavoriteStatus();
                    InAppStoryService.getInstance().getFavoriteImages().clear();
                    InAppStoryService.getInstance().getListReaderConnector().clearAllFavorites();
                }

                if (ScreensManager.getInstance().currentScreen != null) {
                    ScreensManager.getInstance().currentScreen.removeAllStoriesFromFavorite();
                }
            }

            @Override
            public void onError(int code, String message) {
                ProfilingManager.getInstance().setReady(favUID);
                super.onError(code, message);
            }

            @Override
            public void onTimeout() {
                super.onTimeout();
                ProfilingManager.getInstance().setReady(favUID);
            }

            @Override
            public Type getType() {
                return null;
            }
        });
    }


    private void favoriteOrRemoveStory(final int storyId, final boolean favorite) {
        if (InAppStoryService.isNull()) return;
        final String favUID = ProfilingManager.getInstance().addTask("api_favorite");
        NetworkClient.getApi().storyFavorite(Integer.toString(storyId), favorite ? 1 : 0).enqueue(
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        ProfilingManager.getInstance().setReady(favUID);
                        if (InAppStoryService.isNotNull()) {
                            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
                            if (story != null)
                                story.favorite = favorite;
                            InAppStoryService.getInstance().getListReaderConnector().storyFavorite(storyId, favorite);
                        }
                        if (ScreensManager.getInstance().currentScreen != null) {
                            ScreensManager.getInstance().currentScreen.removeStoryFromFavorite(storyId);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        ProfilingManager.getInstance().setReady(favUID);
                        super.onError(code, message);
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        ProfilingManager.getInstance().setReady(favUID);
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    private InAppStoryManager(final Builder builder) throws DataException {
        if (builder.apiKey == null &&
                builder.context.getResources().getString(R.string.csApiKey).isEmpty()) {
            throw new DataException("'apiKey' can't be null or empty. Set 'csApiKey' in 'constants.xml' or use 'builder.apiKey(<api_key>)'", new Throwable("config is not valid"));
        }
        if (builder.userId == null || builder.userId.length() > 255) {
            throw new DataException("'userId' can't be null or longer than 255 characters. Use 'builder.userId(<user_id>)'", new Throwable("config is not valid"));
        }
        long freeSpace = builder.context.getCacheDir().getFreeSpace();
        if (freeSpace < MB_5 + MB_10 + MB_10) {
            throw new DataException("there is no free space on device", new Throwable("initialization error"));
        }

        KeyValueStorage.setContext(builder.context);
        SharedPreferencesAPI.setContext(builder.context);
        createServiceThread(builder.context, builder.userId);
        if (InAppStoryService.isNotNull()) {
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
            InAppStoryService.getInstance().getFastCache().setCacheSize(fastCacheSize);
            InAppStoryService.getInstance().getCommonCache().setCacheSize(commonCacheSize);
        }
        initManager(builder.context,
                builder.sandbox ? TEST_DOMAIN
                        : PRODUCT_DOMAIN,
                builder.apiKey != null ? builder.apiKey : builder.context
                        .getResources().getString(R.string.csApiKey),
                builder.testKey != null ? builder.testKey : null,
                builder.userId,
                builder.tags != null ? builder.tags : null,
                builder.placeholders != null ? builder.placeholders : null);
        new ExceptionManager().sendSavedException();
    }

    public static void generateException() {
        if (InAppStoryService.getInstance() != null) {
            InAppStoryService.getInstance().genException = true;
        }
    }

    private void setUserIdInner(String userId) throws DataException {
        if (InAppStoryService.isNull()) return;
        if (userId == null)
            throw new DataException("'userId' can't be null, you can set '' instead", new Throwable("InAppStoryManager data is not valid"));
        if (userId.length() < 255) {
            if (this.userId.equals(userId)) return;
            localOpensKey = null;
            this.userId = userId;
            if (InAppStoryService.getInstance().getFavoriteImages() != null)
                InAppStoryService.getInstance().getFavoriteImages().clear();
            InAppStoryService.getInstance().getDownloadManager().refreshLocals();
            closeStoryReader(CloseStory.AUTO);
            SessionManager.getInstance().closeSession(sendStatistic, true);
            OldStatisticManager.getInstance().eventCount = 0;
            InAppStoryService.getInstance().getDownloadManager().cleanTasks(false);
            InAppStoryService.getInstance().setUserId(userId);
        } else {
            throw new DataException("'userId' can't be longer than 255 characters", new Throwable("InAppStoryManager data is not valid"));
        }
    }

    //Test

    /**
     * use to change user id in runtime
     *
     * @param userId (userId)
     * @throws DataException 'userId' can't be longer than 255 characters
     */
    public void setUserId(String userId) throws DataException {
        setUserIdInner(userId);
    }

    private String userId;

    public String getUserId() {
        return userId;
    }

    private void clearCachedList(String id) {
        if (InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().listStoriesIds.remove(id);
        }
    }

    private void clearCachedLists() {
        if (InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().listStoriesIds.clear();
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

    private void initManager(Context context,
                             String cmsUrl,
                             String apiKey,
                             String testKey,
                             String userId,
                             ArrayList<String> tags,
                             Map<String, String> placeholders) {
        this.context = context;
        soundOn = !context.getResources().getBoolean(R.bool.defaultMuted);

        synchronized (tagsLock) {
            this.tags = tags;
        }
        if (placeholders != null)
            setPlaceholders(placeholders);
        this.API_KEY = apiKey;
        this.TEST_KEY = testKey;
        NetworkClient.setContext(context);
        this.userId = userId;
        if (INSTANCE != null) {
            localDestroy();
        }

        OldStatisticManager.getInstance().statistic = new ArrayList<>();
        INSTANCE = this;
        ApiSettings
                .getInstance()
                .cacheDirPath(context.getCacheDir().getAbsolutePath())
                .apiKey(this.API_KEY)
                .testKey(this.TEST_KEY)
                .setWebUrl(cmsUrl)
                .cmsUrl(cmsUrl);
        if (InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().getDownloadManager().initDownloaders();
        }
    }

    public static void logout() {
        if (INSTANCE != null) {
            if (InAppStoryService.isNotNull()) {
                InAppStoryService.getInstance().listStoriesIds.clear();
                InAppStoryService.getInstance().getListSubscribers().clear();
                InAppStoryService.getInstance().getDownloadManager().cleanTasks();
                InAppStoryService.getInstance().logout();
            }
        }
    }

    public static void destroy() {
        logout();
    }

    private static void localDestroy() {
        logout();
        INSTANCE = null;
    }


    private String localOpensKey;

    public String getLocalOpensKey() {
        if (localOpensKey == null && userId != null) {
            localOpensKey = "opened" + userId;
        }
        return localOpensKey;
    }

    /**
     * @return current instance of {@link InAppStoryManager}
     */
    public static InAppStoryManager getInstance() {
        return INSTANCE;
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

    private void showLoadedOnboardings(final List<Story> response, final Context outerContext, final AppearanceManager manager, final String feed) {

        if (response == null || response.size() == 0) {
            CsEventBus.getDefault().post(new OnboardingLoad(0, feed));
            if (CallbackManager.getInstance().getOnboardingLoadCallback() != null) {
                CallbackManager.getInstance().getOnboardingLoadCallback().onboardingLoad(0, feed);
            }
            return;
        }

        if (InAppStoryService.isNull()) return;
        if (ScreensManager.created == -1) {
            InAppStoryManager.closeStoryReader(CloseStory.AUTO);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoadedOnboardings(response, outerContext, manager, feed);
                    ScreensManager.created = 0;
                }
            }, 350);
            return;
        } else if (System.currentTimeMillis() - ScreensManager.created < 1000) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoadedOnboardings(response, outerContext, manager, feed);
                    ScreensManager.created = 0;
                }
            }, 350);
            return;
        }

        ArrayList<Story> stories = new ArrayList<Story>();
        ArrayList<Integer> storiesIds = new ArrayList<>();
        stories.addAll(response);
        for (Story story : response) {
            storiesIds.add(story.id);
        }
        InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(stories);
        InAppStoryService.getInstance().getDownloadManager().putStories(
                InAppStoryService.getInstance().getDownloadManager().getStories());
        ScreensManager.getInstance().openStoriesReader(outerContext, null, manager, storiesIds, 0, ShowStory.ONBOARDING);
        CsEventBus.getDefault().post(new OnboardingLoad(response.size(), feed));
        if (CallbackManager.getInstance().getOnboardingLoadCallback() != null) {
            CallbackManager.getInstance().getOnboardingLoadCallback().onboardingLoad(response.size(), feed);
        }
    }

    private void showOnboardingStoriesInner(final String feed, final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        if (InAppStoryService.isNull()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStoriesInner(feed, tags, outerContext, manager);
                }
            }, 1000);
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
                if (feed != null) {
                    NetworkClient.getApi().getOnboardingFeed(feed, localTags == null ? getTagsString() :
                            localTags).enqueue(new LoadFeedCallback() {
                        @Override
                        public void onSuccess(Feed response) {
                            if (InAppStoryManager.isNull()) return;
                            ProfilingManager.getInstance().setReady(onboardUID);
                            List<Story> notOpened = new ArrayList<>();
                            Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
                            if (opens == null) opens = new HashSet<>();
                            if (response.stories != null) {
                                for (Story story : response.stories) {
                                    boolean add = true;
                                    for (String opened : opens) {
                                        if (Integer.toString(story.id).equals(opened)) {
                                            add = false;
                                        }
                                    }
                                    if (add) notOpened.add(story);
                                }
                            }
                            showLoadedOnboardings(notOpened, outerContext, manager, feed);
                        }

                        @Override
                        public void onError(int code, String message) {
                            ProfilingManager.getInstance().setReady(onboardUID);
                            loadOnboardingError(feed);
                        }

                        @Override
                        public void onTimeout() {
                            ProfilingManager.getInstance().setReady(onboardUID);
                            loadOnboardingError(feed);
                        }
                    });
                } else {
                    NetworkClient.getApi().onboardingStories(localTags == null ? getTagsString() :
                            localTags).enqueue(new NetworkCallback<List<Story>>() {
                        @Override
                        public void onSuccess(List<Story> response) {
                            if (InAppStoryManager.isNull()) return;
                            ProfilingManager.getInstance().setReady(onboardUID);
                            List<Story> notOpened = new ArrayList<>();
                            Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
                            if (opens == null) opens = new HashSet<>();
                            for (Story story : response) {
                                boolean add = true;
                                for (String opened : opens) {
                                    if (Integer.toString(story.id).equals(opened)) {
                                        add = false;
                                    }
                                }
                                if (add) notOpened.add(story);
                            }
                            showLoadedOnboardings(notOpened, outerContext, manager, feed);
                        }

                        @Override
                        public Type getType() {
                            return new StoryListType();
                        }

                        @Override
                        public void onError(int code, String message) {
                            ProfilingManager.getInstance().setReady(onboardUID);
                            loadOnboardingError(null);
                        }

                        @Override
                        public void onTimeout() {
                            ProfilingManager.getInstance().setReady(onboardUID);
                            loadOnboardingError(null);
                        }
                    });
                }
            }

            @Override
            public void onError() {
                loadOnboardingError(feed);
            }

        });
    }

    private void loadOnboardingError(String feed) {
        CsEventBus.getDefault().post(new OnboardingLoadError(feed));
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().loadOnboardingError(feed);
        }
        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_ONBOARD, feed));
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
        showOnboardingStoriesInner(feed, tags, outerContext, manager);
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
        showOnboardingStoriesInner(ONBOARDING_FEED, tags, outerContext, manager);
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


    public static String ONBOARDING_FEED = "onboarding";

    private String lastSingleOpen = null;

    private void showStoryInner(final String storyId, final Context context, final AppearanceManager manager, final IShowStoryCallback callback, final Integer slide) {
        if (InAppStoryService.isNull()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStoryInner(storyId, context, manager, callback, slide);
                }
            }, 1000);
            return;
        }

        if (lastSingleOpen != null &&
                lastSingleOpen.equals(storyId)) return;
        lastSingleOpen = storyId;


        InAppStoryService.getInstance().getDownloadManager().getFullStoryByStringId(new GetStoryByIdCallback() {
            @Override
            public void getStory(Story story) {
                if (story != null) {

                    if (ScreensManager.created == -1) {
                        InAppStoryManager.closeStoryReader(CloseStory.AUTO);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lastSingleOpen = null;
                                showStoryInner(storyId, context, manager, callback, slide);
                                // StoriesActivity.destroyed = 0;
                            }
                        }, 500);
                        return;
                    } else if (System.currentTimeMillis() - ScreensManager.created < 1000) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showStoryInner(storyId, context, manager, callback, slide);
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
                    if (story.deeplink != null) {
                        lastSingleOpen = null;
                        OldStatisticManager.getInstance().addDeeplinkClickStatistic(story.id);

                        StatisticManager.getInstance().sendDeeplinkStory(story.id, story.deeplink);
                        if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                            CallbackManager.getInstance().getUrlClickCallback().onUrlClick(story.deeplink);
                        } else {

                            if (!InAppStoryService.isConnected()) {

                                if (CallbackManager.getInstance().getErrorCallback() != null) {
                                    CallbackManager.getInstance().getErrorCallback().noConnection();
                                }
                                CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.LINK));
                                return;
                            }
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(story.deeplink));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            } catch (Exception e) {
                            }
                        }
                        return;
                    }
                    if (story.isHideInReader()) {
                        if (CallbackManager.getInstance().getErrorCallback() != null) {
                            CallbackManager.getInstance().getErrorCallback().emptyLinkError();
                        }
                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.EMPTY_LINK));
                        return;
                    }
                    InAppStoryService.getInstance().getDownloadManager().putStories(
                            InAppStoryService.getInstance().getDownloadManager().getStories());
                    ArrayList<Integer> stIds = new ArrayList<>();
                    stIds.add(story.id);
                    ScreensManager.getInstance().openStoriesReader(context, null, manager, stIds, 0, ShowStory.SINGLE, slide);
                    new Handler().postDelayed(new Runnable() {
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

        }, storyId);
    }

    private void showStoryInner(final String storyId, final Context context, final AppearanceManager manager, final IShowStoryCallback callback) {
        showStoryInner(storyId, context, manager, callback, null);
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
        showStoryInner(storyId, context, manager, callback);
    }

    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback, Integer slide) {
        showStoryInner(storyId, context, manager, callback, slide);
    }

    /**
     * use to show single story in reader by id
     *
     * @param storyId (storyId)
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showStory(String storyId, Context context, AppearanceManager manager) {
        showStoryInner(storyId, context, manager, null);
    }

    public void showStoryWithSlide(String storyId, Context context, Integer slide, String managerSettings) {
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
        showStoryInner(storyId, context, appearanceManager, null, slide);
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

        public Builder() {
        }

        public Builder context(Context context) throws DataException {
            if (context == null)
                throw new DataException("Context must not be null", new Throwable("InAppStoryManager.Builder data is not valid"));
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
        public Builder userId(String userId) throws DataException {
            if (userId != null && userId.length() < 255) {
                Builder.this.userId = userId;
            } else {
                throw new DataException("'userId' can't be null or longer than 255 characters",
                        new Throwable("InAppStoryManager.Builder data is not valid"));
            }
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
         * main method to create {@link InAppStoryManager} instance.
         *
         * @return {@link InAppStoryManager}
         * @throws DataException 'context' can't be null
         */
        public InAppStoryManager create() throws DataException {
            if (Builder.this.context == null) {
                throw new DataException("'context' can't be null", new Throwable("InAppStoryManager.Builder data is not valid"));
            }
            return new InAppStoryManager(Builder.this);
        }
    }
}
