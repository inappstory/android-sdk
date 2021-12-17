package com.inappstory.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.lrudiskcache.CacheSize;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StatisticSession;
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
import com.inappstory.sdk.stories.events.ChangeUserIdEvent;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.OnboardingLoad;
import com.inappstory.sdk.stories.outerevents.OnboardingLoadError;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
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


    public static boolean disableStatistic = true;


    /**
     * @return {@link ArrayList} of tags
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    //Test

    /**
     * use to clear downloaded files and in-app cache
     */
    public void clearCache() {
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().getDownloadManager().clearCache();
    }

    /**
     * use to force close story reader
     */
    public static void closeStoryReader() {
        CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM));
    }

    /**
     * use to customize click on buttons in reader
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
        if (tags == null) return null;
        return TextUtils.join(",", tags);
    }

    /**
     * use to customize tags in runtime. Replace tags array.
     *
     * @param tags (tags)
     */
    //Test
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     * use to customize tags in runtime. Adds tags to array.
     *
     * @param newTags (newTags) - list of additional tags
     */
    //Test
    public void addTags(ArrayList<String> newTags) {
        if (newTags == null || newTags.isEmpty()) return;
        if (tags == null) tags = new ArrayList<>();
        for (String tag : newTags) {
            addTag(tag);
        }
    }

    /**
     * use to customize tags in runtime. Removes tags from array.
     *
     * @param removedTags (removedTags) - list of removing tags
     */
    //Test
    public void removeTags(ArrayList<String> removedTags) {
        if (tags == null || removedTags == null || removedTags.isEmpty()) return;
        for (String tag : removedTags) {
            removeTag(tag);
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

    /**
     * use to customize default strings in stories runtime.
     *
     * @param placeholders (placeholders) - key-value map (key - what we replace, value - replacement result)
     */
    public void setPlaceholders(@NonNull Map<String, String> placeholders) {

        for (String placeholderKey : placeholders.keySet()) {
            setPlaceholder(placeholderKey, placeholders.get(placeholderKey));
        }
    }

    ArrayList<String> tags;

    /**
     * Returns map with all default strings replacements
     */
    public Map<String, String> getPlaceholders() {

        if (defaultPlaceholders == null) defaultPlaceholders = new HashMap<>();
        if (placeholders == null) placeholders = new HashMap<>();
        return placeholders;
    }

    Map<String, String> placeholders = new HashMap<>();

    public Map<String, String> getDefaultPlaceholders() {

        if (defaultPlaceholders == null) defaultPlaceholders = new HashMap<>();
        if (placeholders == null) placeholders = new HashMap<>();
        return defaultPlaceholders;
    }

    Map<String, String> defaultPlaceholders = new HashMap<>();

    public boolean closeOnOverscroll() {
        return closeOnOverscroll;
    }

    public boolean closeOnSwipe() {
        return closeOnSwipe;
    }

    boolean closeOnOverscroll = true;
    boolean closeOnSwipe = true;

    private static final String TEST_DOMAIN = "https://api.test.inappstory.com/";
    private static final String PRODUCT_DOMAIN = "https://api.inappstory.com/";

    public String getApiKey() {
        return API_KEY;
    }

    public String getTestKey() {
        return TEST_KEY;
    }

    String API_KEY = "";

    public void setTestKey(String testKey) {
        this.TEST_KEY = testKey;
    }

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
        serviceThread.setUncaughtExceptionHandler(new InAppStoryService.DefaultExceptionHandler());
        serviceThread.start();
    }

    void setExceptionCache(ExceptionCache exceptionCache) {
        this.exceptionCache = exceptionCache;
    }

    private ExceptionCache exceptionCache;


    private InAppStoryManager(final Builder builder) throws DataException {
        KeyValueStorage.setContext(builder.context);
        SharedPreferencesAPI.setContext(builder.context);
        createServiceThread(builder.context, builder.userId);
        if (builder.apiKey == null &&
                (builder.context.getResources().getString(R.string.csApiKey).isEmpty() || builder.context.getResources().getString(R.string.csApiKey).equals("1"))) {
            throw new DataException("'apiKey' can't be empty. Set 'csApiKey' in 'constants.xml' or put use 'builder.apiKey()'", new Throwable("config is not valid"));
        }
        long freeSpace = builder.context.getCacheDir().getFreeSpace();
        if (freeSpace < MB_5 + MB_10 + MB_10) {
            throw new DataException("there is no free space on device", new Throwable("initialization error"));
        }
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
                (builder.userId != null && !builder.userId.isEmpty()) ? builder.userId :
                        "",
                builder.tags != null ? builder.tags : null,
                builder.placeholders != null ? builder.placeholders : null,
                builder.sendStatistic);

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
            CsEventBus.getDefault().post(new ChangeUserIdEvent());
            SessionManager.getInstance().closeSession(sendStatistic, true);
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


    public void setActionBarColor(int actionBarColor) {
        this.actionBarColor = actionBarColor;
    }

    public int actionBarColor = -1;

    public boolean sendStatistic = true;

    private void initManager(Context context,
                             String cmsUrl,
                             String apiKey,
                             String testKey,
                             String userId,
                             ArrayList<String> tags,
                             Map<String, String> placeholders,
                             boolean sendStatistic) {
        this.context = context;
        soundOn = !context.getResources().getBoolean(R.bool.defaultMuted);
        this.tags = tags;
        if (placeholders != null)
            setPlaceholders(placeholders);
        this.sendStatistic = sendStatistic;
        this.API_KEY = apiKey;
        this.TEST_KEY = testKey;
        NetworkClient.setContext(context);
        this.userId = userId;
        if (INSTANCE != null) {
            destroy();
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

    public static void destroy() {
        if (INSTANCE != null) {
            if (InAppStoryService.isNotNull()) {
                InAppStoryService.getInstance().getDownloadManager().cleanTasks();
                InAppStoryService.getInstance().logout();
                InAppStoryService.getInstance().onDestroy();
            }
            INSTANCE = null;
        }
    }

    private String localOpensKey;

    public String getLocalOpensKey() {
        if (localOpensKey == null && userId != null) {
            localOpensKey = "opened" + userId;
        }
        return localOpensKey != null ? localOpensKey : "opened";
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

    public boolean soundOn = false;

    public void setOnboardLoadedListener(OnboardingLoadedListener onboardLoadedListener) {
        this.onboardLoadedListener = onboardLoadedListener;
    }

    private OnboardingLoadedListener onboardLoadedListener;

    public OnboardingLoadedListener getSingleLoadedListener() {
        return singleLoadedListener;
    }

    public void setSingleLoadedListener(OnboardingLoadedListener singleLoadedListener) {
        this.singleLoadedListener = singleLoadedListener;
    }

    private OnboardingLoadedListener singleLoadedListener;

    private void showLoadedOnboardings(List<Story> response, Context outerContext, final AppearanceManager manager) {
        if (response == null || response.size() == 0) {
            CsEventBus.getDefault().post(new OnboardingLoad(0));
            if (onboardLoadedListener != null) {
                onboardLoadedListener.onEmpty();
            }
            return;
        }
        ArrayList<Story> stories = new ArrayList<Story>();
        ArrayList<Integer> storiesIds = new ArrayList<>();
        stories.addAll(response);
        for (Story story : response) {
            storiesIds.add(story.id);
        }
        if (InAppStoryService.isNotNull()) {
            InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(stories);
            InAppStoryService.getInstance().getDownloadManager().putStories(
                    InAppStoryService.getInstance().getDownloadManager().getStories());
        }
        ScreensManager.getInstance().openStoriesReader(outerContext, manager, storiesIds, 0, ShowStory.ONBOARDING);
        CsEventBus.getDefault().post(new OnboardingLoad(response.size()));
        if (onboardLoadedListener != null) {
            onboardLoadedListener.onLoad();
        }
    }

    private void showOnboardingStoriesInner(final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        if (InAppStoryService.isNull()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStoriesInner(tags, outerContext, manager);
                }
            }, 1000);
            return;
        }
        if (StoriesActivity.destroyed == -1) {

            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStoriesInner(tags, outerContext, manager);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        } else if (System.currentTimeMillis() - StoriesActivity.destroyed < 1000) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStoriesInner(tags, outerContext, manager);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
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
                NetworkClient.getApi().onboardingStories(StatisticSession.getInstance().id, localTags == null ? getTagsString() : localTags,
                        getApiKey()).enqueue(new NetworkCallback<List<Story>>() {
                    @Override
                    public void onSuccess(List<Story> response) {
                        List<Story> notOpened = new ArrayList<>();
                        Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
                        for (Story story : response) {
                            boolean add = true;
                            for (String opened : opens) {
                                if (Integer.toString(story.id).equals(opened)) {
                                    add = false;
                                }
                            }
                            if (add) notOpened.add(story);
                        }
                        showLoadedOnboardings(notOpened, outerContext, manager);
                    }

                    @Override
                    public Type getType() {
                        return new StoryListType();
                    }

                    @Override
                    public void onError(int code, String message) {

                        CsEventBus.getDefault().post(new OnboardingLoadError());
                        if (onboardLoadedListener != null) {
                            onboardLoadedListener.onError();
                        }
                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_ONBOARD));
                    }
                });
            }

            @Override
            public void onError() {
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_ONBOARD));
            }

        });
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(List<String> tags, Context outerContext, AppearanceManager manager) {
        showOnboardingStoriesInner(tags, outerContext, manager);
    }


    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(Context context, final AppearanceManager manager) {
        showOnboardingStories(getTags(), context, manager);
    }

    private String lastSingleOpen = null;

    private void showStoryInner(final String storyId, final Context context, final AppearanceManager manager, final IShowStoryCallback callback) {
        if (InAppStoryService.isNull()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStoryInner(storyId, context, manager, callback);
                }
            }, 1000);
            return;
        }

        if (lastSingleOpen != null &&
                lastSingleOpen.equals(storyId)) return;
        lastSingleOpen = storyId;

        if (StoriesActivity.destroyed == -1) {
            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lastSingleOpen = null;
                    showStoryInner(storyId, context, manager, callback);
                    // StoriesActivity.destroyed = 0;
                }
            }, 500);
            return;
        } else if (System.currentTimeMillis() - StoriesActivity.destroyed < 1000) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStoryInner(storyId, context, manager, callback);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        }
        InAppStoryService.getInstance().getDownloadManager().getFullStoryByStringId(new GetStoryByIdCallback() {
            @Override
            public void getStory(Story story) {
                if (story != null) {
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
                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.EMPTY_LINK));
                        return;
                    }
                    InAppStoryService.getInstance().getDownloadManager().putStories(
                            InAppStoryService.getInstance().getDownloadManager().getStories());
                    ArrayList<Integer> stIds = new ArrayList<>();
                    stIds.add(story.id);
                    ScreensManager.getInstance().openStoriesReader(context, manager, stIds, 0, ShowStory.SINGLE);
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
        boolean sendStatistic = true;

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

        public Builder sendStatistic(boolean sendStatistic) {
            Builder.this.sendStatistic = sendStatistic;
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
            if (userId.length() < 255) {
                Builder.this.userId = userId;
            } else {
                throw new DataException("'userId' can't be longer than 255 characters", new Throwable("InAppStoryManager.Builder data is not valid"));
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
