package com.inappstory.sdk;

import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.IASCoreImpl;
import com.inappstory.sdk.core.IASExceptionHandler;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettings;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.inappmessage.CloseInAppMessageCallback;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.InAppMessageWidgetCallback;
import com.inappstory.sdk.inappmessage.ShowInAppMessageCallback;
import com.inappstory.sdk.lrudiskcache.CacheSize;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.utils.HostFromSecretKey;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
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
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.ui.reader.ForceCloseReaderCallback;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.utils.StringsUtils;
import com.inappstory.sdk.utils.WebViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Main class for work with SDK.
 * Need to initialize it first with {@link Builder} before other interactions.
 * Singleton class, can be available with {@link #getInstance()}.
 * Can be reinitialized.
 */
public class InAppStoryManager implements IASBackPressHandler {

    private static InAppStoryManager INSTANCE;

    private IASCore core;

    public IASCore iasCore() {
        return core;
    }

    public static void handleException(final Throwable e) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.exceptionManager().createExceptionLog(e);
            }
        });
    }

    private final ExecutorService coreThread = Executors.newSingleThreadExecutor();

    public static void useCore(UseIASCoreCallback callback) {
        synchronized (lock) {
            try {
                if (INSTANCE == null || INSTANCE.core == null) {
                    callback.error();
                } else {
                    callback.use(INSTANCE.iasCore());
                }
            } catch (Exception e) {
                showELog(IAS_ERROR_TAG, e.getMessage() + "");
            }
        }
    }

    public static void useCoreInSeparateThread(final UseIASCoreCallback callback) {
        synchronized (lock) {
            try {
                if (INSTANCE == null || INSTANCE.core == null) {
                    callback.error();
                } else {
                    final IASCore core = INSTANCE.core;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            callback.use(core);
                        }
                    };
                    INSTANCE.coreThread.execute(runnable);
                }
            } catch (Exception e) {
                showELog(IAS_ERROR_TAG, e.getMessage() + "");
            }
        }
    }

    public void setCommonAppearanceManager(AppearanceManager appearanceManager) {
        AppearanceManager.setCommonInstance(appearanceManager);
    }

    private static void clearLocalData() {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.storiesListVMHolder().clear();
                core.contentLoader().storyDownloadManager().clearLocalData();
                core.contentLoader().inAppMessageDownloadManager().clearLocalData();
            }
        });
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
            showELog(IAS_ERROR_TAG, e.getMessage() + "");
        }
    }

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
     * @return {@link List} of tags
     */
    public List<String> getTags() {
        if (core == null) return new ArrayList<>();
        return ((IASDataSettingsHolder) core.settingsAPI()).tags();
    }

    //Test

    /**
     * use to clear downloaded files and in-app cache
     */
    public void clearCache() {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.contentLoader().clearCache();
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
                core.screensManager().getIAMScreenHolder()
                        .closeScreen();
            }
        });
    }

    /**
     * use to close story reader
     *
     * @param forceClose               (forceClose) - close reader immediately without animation
     * @param forceCloseReaderCallback (forceCloseReaderCallback) - triggers after reader is closed and only if {@code forceClose == true}
     */
    public static void closeStoryReader(
            final boolean forceClose,
            final ForceCloseReaderCallback forceCloseReaderCallback
    ) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                if (forceClose) {
                    core.screensManager()
                            .forceCloseAllReaders(forceCloseReaderCallback);
                } else {
                    core.screensManager().getStoryScreenHolder()
                            .closeScreenWithAction(CloseStory.CUSTOM);
                    core.screensManager().getIAMScreenHolder()
                            .closeScreen();
                }
            }
        });
    }

    public boolean onBackPressed() {
        if (core == null) return false;
        return core.screensManager().onBackPressed();
    }


    public void openGame(final String gameId, @NonNull final Context context) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.gamesAPI().open(context, gameId);
            }
        });
    }

    public static boolean isStoryReaderOpened() {
        InAppStoryManager inAppStoryManager = getInstance();
        return inAppStoryManager != null
                && inAppStoryManager.core != null
                && inAppStoryManager.core
                .screensManager()
                .getStoryScreenHolder()
                .isOpened();
    }

    public static boolean isGameReaderOpened() {
        InAppStoryManager inAppStoryManager = getInstance();
        return inAppStoryManager != null
                && inAppStoryManager.core != null
                && inAppStoryManager.core
                .screensManager()
                .getGameScreenHolder()
                .isOpened();
    }

    public static boolean isInAppMessageReaderOpened() {
        InAppStoryManager inAppStoryManager = getInstance();
        return inAppStoryManager != null
                && inAppStoryManager.core != null
                && inAppStoryManager.core
                .screensManager()
                .getIAMScreenHolder()
                .isOpened();
    }

    public void closeGame() {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.gamesAPI().close();
            }
        });
    }

    /**
     * use to force close story reader
     */

    /**
     * use to set callback on different errors
     */
    public void setErrorCallback(final ErrorCallback errorCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.callbacksAPI().setCallback(IASCallbackType.ERROR, errorCallback);
            }
        });
    }


    /**
     * use to set callback on different errors
     */
    public void setExceptionCallback(final ExceptionCallback exceptionCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.EXCEPTION, exceptionCallback);
            }
        });
    }

    /**
     * use to set callback on share click
     */
    public void setClickOnShareStoryCallback(final ClickOnShareStoryCallback clickOnShareStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.CLICK_SHARE, clickOnShareStoryCallback);
            }
        });
    }


    /**
     * use to set callback on game start/close/finish
     */
    public void setGameReaderCallback(final GameReaderCallback gameReaderCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.gamesAPI().callback(gameReaderCallback);
            }
        });
    }

    /**
     * use to set callback on onboardings load
     */
    public void setOnboardingLoadCallback(final OnboardingLoadCallback onboardingLoadCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.onboardingsAPI().loadCallback(onboardingLoadCallback);
            }
        });
    }

    /**
     * use to set callback on click on buttons in stories (with info)
     */
    public void setCallToActionCallback(final CallToActionCallback callToActionCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.CALL_TO_ACTION, callToActionCallback);
            }
        });
    }

    /**
     * use to set callback on click on widgets in stories (with info)
     */
    public void setStoryWidgetCallback(final StoryWidgetCallback storyWidgetCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.STORY_WIDGET, storyWidgetCallback);
            }
        });
    }


    /**
     * use to set callback on stories reader closing
     */
    public void setCloseStoryCallback(final CloseStoryCallback closeStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.CLOSE_STORY, closeStoryCallback);
            }
        });
    }

    /**
     * use to set callback on favorite action
     */
    public void setFavoriteStoryCallback(final FavoriteStoryCallback favoriteStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.FAVORITE, favoriteStoryCallback);
            }
        });
    }

    /**
     * use to set callback on like/dislike action
     */
    public void setLikeDislikeStoryCallback(final LikeDislikeStoryCallback likeDislikeStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.LIKE_DISLIKE, likeDislikeStoryCallback);
            }
        });
    }

    /**
     * use to set callback on slide shown in reader
     */
    public void setShowSlideCallback(final ShowSlideCallback showSlideCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.SHOW_SLIDE, showSlideCallback);
            }
        });
    }

    /**
     * use to set callback on story shown in reader
     */
    public void setShowStoryCallback(final ShowStoryCallback showStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.SHOW_STORY, showStoryCallback);
            }
        });
    }

    /**
     * use to set callback on single story loading
     */
    public void setSingleLoadCallback(final SingleLoadCallback singleLoadCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.singleStoryAPI().loadCallback(singleLoadCallback);
            }
        });
    }


    /**
     * use to customize share functional
     */
    public void setShareCallback(final ShareCallback shareCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.SHARE_ADDITIONAL, shareCallback);
            }
        });
    }

    //Test

    /**
     * @return {@link String} with tags joined by comma
     */
    public String getTagsString() {
        if (core == null) return "";
        return TextUtils.join(",", ((IASDataSettingsHolder) core.settingsAPI()).tags());
    }

    /**
     * use to customize tags in runtime. Replace tags array.
     *
     * @param tags (tags)
     */


    public void setTags(final List<String> tags) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setTags(tags);
            }
        });
    }


    private final Object tagsLock = new Object();

    /**
     * use to customize tags in runtime. Adds tags to array.
     *
     * @param newTags (newTags) - list of additional tags
     */

    public void addTags(final List<String> newTags) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().addTags(newTags);
            }
        });

    }

    /**
     * use to customize tags in runtime. Removes tags from array.
     *
     * @param removedTags (removedTags) - list of removing tags
     */

    public void removeTags(final List<String> removedTags) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().removeTags(removedTags);
            }
        });
    }

    /**
     * use to customize default string in stories runtime.
     *
     * @param key   (key) - what we replace
     * @param value (value) - replacement result
     */
    public void setPlaceholder(final String key, final String value) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setPlaceholder(key, value);
            }
        });

    }

    /**
     * use to customize default strings in stories runtime.
     *
     * @param newPlaceholders (newPlaceholders) - key-value map (key - what we replace, value - replacement result)
     */
    public void setPlaceholders(final @NonNull Map<String, String> newPlaceholders) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setPlaceholders(newPlaceholders);
            }
        });

    }

    /**
     * Returns map with all default strings replacements
     */
    public Map<String, String> getPlaceholders() {
        if (core == null) new HashMap<>();
        return ((IASDataSettingsHolder) core.settingsAPI()).placeholders();
    }


    public void setImagePlaceholders(final @NonNull Map<String, ImagePlaceholderValue> placeholders) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setImagePlaceholders(placeholders);
            }
        });

    }

    public void setImagePlaceholder(final @NonNull String key, final ImagePlaceholderValue value) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setImagePlaceholder(key, value);
            }
        });
    }


    public void removeFromFavorite(final int storyId) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.favoritesAPI().removeByStoryId(storyId);
            }
        });

    }

    public void removeAllFavorites() {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.favoritesAPI().removeAll();
            }
        });

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
        if (!calledFromApplication)
            showELog(IAS_ERROR_TAG, "Method must be called from Application class and context has to be an applicationContext");
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = new InAppStoryManager(context);
            }
        }

    }

    public static void initSDK(@NonNull Context context) {
        initSDK(context, false);
    }

    private InAppStoryManager(Context context) {
        try {
            core = new IASCoreImpl(context);
            core.contentHolder().clearAll();
            core.contentLoader().inAppMessageDownloadManager().clearSubscribers();
            core.settingsAPI().isSoundOn(!context.getResources().getBoolean(R.bool.defaultMuted));
            core.inAppStoryService().onCreate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSandbox = false;

    public final static String IAS_ERROR_TAG = "InAppStory_SDK_error";

    private boolean isInitialized = false;
    private boolean isInitProcess = false;

    private final Object initLock = new Object();

    public boolean isInitialized() {
        synchronized (initLock) {
            return isInitialized;
        }
    }

    public boolean isInitializedOrInitProcess() {
        synchronized (initLock) {
            return isInitialized || isInitProcess;
        }
    }

    private void build(final Builder builder) {
        Integer errorStringId = null;
        if (core == null) {
            showELog(
                    IAS_ERROR_TAG,
                    "InAppStoryManager not initialized"
            );
            return;
        }
        Context context = core.appContext();
        if (context == null) {
            errorStringId = R.string.ias_context_is_null;
        } else if (builder.apiKey == null && context.getResources().getString(R.string.csApiKey).isEmpty()) {
            errorStringId = R.string.ias_api_key_error;
        } else if (StringsUtils.getBytesLength(builder.userId) > 255) {
            errorStringId = R.string.ias_builder_user_length_error;
        } else if (builder.tags != null
                &&
                StringsUtils.getBytesLength(TextUtils.join(",", builder.tags)) > TAG_LIMIT) {
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
        synchronized (initLock) {
            if (isInitProcess) {
                showELog(IAS_ERROR_TAG, "Previous init process still not finished");
                return;
            }
            isInitialized = false;
            isInitProcess = true;
        }
        try {
            core.contentLoader().setCacheSizes();
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
                    builder.userSign(),
                    builder.locale(),
                    builder.gameDemoMode(),
                    builder.isDeviceIdEnabled(),
                    builder.tags() != null ? builder.tags() : null,
                    builder.placeholders() != null ? builder.placeholders() : null,
                    builder.imagePlaceholders() != null ? builder.imagePlaceholders() : null
            );
            new ExceptionManager(core).sendSavedException();
            synchronized (initLock) {
                isInitialized = true;
                isInitProcess = false;
            }
        } catch (Exception e) {
            synchronized (initLock) {
                isInitialized = false;
                isInitProcess = false;
            }
        }

    }


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
            String userSign,
            Locale locale,
            boolean gameDemoMode,
            boolean isDeviceIDEnabled,
            ArrayList<String> tags,
            Map<String, String> placeholders,
            Map<String, ImagePlaceholderValue> imagePlaceholders
    ) {
        if (core == null) return;
        IASDataSettings settings = core.settingsAPI();
        settings.setUserId(userId, userSign);
        settings.setLang(locale);
        settings.setTags(tags);
        if (isDeviceIDEnabled) {
            settings.deviceId
                    (
                            Settings.Secure.getString(
                                    context.getContentResolver(),
                                    Settings.Secure.ANDROID_ID
                            )
                    );
        }
        settings.gameDemoMode(gameDemoMode);
        if (placeholders != null)
            settings.setPlaceholders(placeholders);
        if (imagePlaceholders != null)
            settings.setImagePlaceholders(imagePlaceholders);
        logout();
        if (ApiSettings.getInstance().hostIsDifferent(cmsUrl)) {
            core.network().clear();
        }
        ApiSettings
                .getInstance()
                .cacheDirPath(context.getCacheDir().getAbsolutePath())
                .apiKey(apiKey)
                .testKey(testKey)
                .host(cmsUrl);
        core.network().setBaseUrl(cmsUrl);

    }


    private static final Object lock = new Object();

    public void setAppVersion(@NonNull final String version, final int build) {
        if (version.isEmpty() || version.length() > 50) {
            showELog(
                    IAS_ERROR_TAG,
                    "App Version must be no more than 50 symbols and not empty"
            );
            return;
        }
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().setExternalAppVersion(new AppVersion(version, build));
            }
        });
    }

    public static void logout() {
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                final String sessionId = core.sessionManager().getSession().getSessionId();
                core.storiesListVMHolder().clear();
                core.contentLoader().storyDownloadManager().cleanTasks();
                core.screensManager().forceCloseAllReaders(
                        new ForceCloseReaderCallback() {
                            @Override
                            public void onComplete() {
                                if (sessionId == null || sessionId.isEmpty()) return;
                                IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
                                core.statistic().storiesV1(new GetStatisticV1Callback() {
                                    @Override
                                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                                        manager.closeStatisticEvent();
                                    }
                                });
                                core.sessionManager().closeSession(
                                        true,
                                        false,
                                        settingsHolder.lang().toLanguageTag(),
                                        settingsHolder.userId(),
                                        core.sessionManager().getSession().getSessionId()
                                );
                            }
                        }
                );

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


    public void preloadGames() {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.contentPreload().restartGamePreloader();
            }
        });
    }

    public void setLang(@NonNull final Locale lang) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.settingsAPI().setLang(lang);
            }
        });
    }


    /**
     * use to change user id in runtime
     *
     * @param userId (userId) - can't be longer than 255 characters
     */
    public void setUserId(@NonNull final String userId) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.settingsAPI().setUserId(userId);
            }
        });
    }

    public void setUserId(@NonNull final String userId, final String userSign) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.settingsAPI().setUserId(userId, userSign);
            }
        });
    }

    public String getUserId() {
        if (core == null) return null;
        return ((IASDataSettingsHolder) core.settingsAPI()).userId();
    }

    public void clearCachedListById(final String cacheId) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.storiesListVMHolder().removeResultById(cacheId);
            }
        });
    }

    public void clearCachedListByFeed(final String feed) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.storiesListVMHolder().removeResultByFeed(feed);
            }
        });
    }

    public void clearCachedListByIdAndFeed(final String cacheId, final String feed) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.storiesListVMHolder().removeResultByIdAndFeed(cacheId, feed);
            }
        });
    }

    public void setOpenStoriesReader(@NonNull final IOpenStoriesReader openStoriesReader) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.screensManager().setOpenStoriesReader(openStoriesReader);
            }
        });
    }

    public void setOpenInAppMessageReader(@NonNull final IOpenInAppMessageReader openInAppMessageReader) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.screensManager().setOpenInAppMessageReader(openInAppMessageReader);
            }
        });
    }

    public void setOpenGameReader(@NonNull final IOpenGameReader openGameReader) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.screensManager().setOpenGameReader(openGameReader);
            }
        });
    }

    public void clearCachedLists() {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.storiesListVMHolder().clear();
            }
        });
    }

    public void soundOn(final boolean isSoundOn) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.settingsAPI().isSoundOn(isSoundOn);
            }
        });
    }

    public void getStackFeed(
            final String feed,
            final String uniqueStackId,
            final List<String> tags,
            final AppearanceManager appearanceManager,
            final IStackFeedResult stackFeedResult
    ) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.stackFeedAPI().get(feed, uniqueStackId, appearanceManager, tags, stackFeedResult);
            }
        });
    }

    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final String feed, final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.onboardingsAPI().show(outerContext, feed, manager, tags, 1000);
            }
        });
    }


    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final String feed, final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.onboardingsAPI().show(outerContext, feed, manager, null, 1000);
            }
        });
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.onboardingsAPI().show(outerContext, null, manager, tags, 1000);
            }
        });
    }

    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.onboardingsAPI().show(outerContext, null, manager, null, 1000);
            }
        });
    }

    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final int limit, final String feed, final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.onboardingsAPI().show(outerContext, feed, manager, tags, limit);
            }
        });
    }


    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final int limit, final String feed, final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.onboardingsAPI().show(outerContext, feed, manager, null, limit);
            }
        });
    }


    /**
     * Function for loading onboarding stories with custom tags
     *
     * @param tags         (tags)
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final int limit, final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.onboardingsAPI().show(outerContext, null, manager, tags, limit);
            }
        });
    }

    /**
     * function for loading onboarding stories with default tags (set in InAppStoryManager.Builder)
     *
     * @param outerContext (outerContext) any type of context (preferably - activity)
     * @param manager      (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showOnboardingStories(final int limit, final Context outerContext, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.onboardingsAPI().show(outerContext, null, manager, null, limit);
            }
        });
    }


    /**
     * use to show single story in reader by id
     *
     * @param storyId  (storyId)
     * @param context  (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager  (manager) {@link AppearanceManager} for reader. May be null
     * @param callback (callback) custom action when story is loaded
     */
    public void showStory(final String storyId, final Context context, final AppearanceManager manager, final IShowStoryCallback callback) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.singleStoryAPI().show(context, storyId, manager, callback, 0);
            }
        });
    }

    public void showStory(final String storyId, final Context context, final AppearanceManager manager, final IShowStoryCallback callback, final Integer slide) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.singleStoryAPI().show(context, storyId, manager, callback, slide);
            }
        });
    }


    public void showStoryOnce(final String storyId,
                              final Context context,
                              final AppearanceManager manager,
                              final IShowStoryOnceCallback callback
    ) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.singleStoryAPI().showOnce(context, storyId, manager, callback);
            }
        });
    }

    /**
     * use to show single story in reader by id
     *
     * @param storyId (storyId)
     * @param context (context) any type of context (preferably - same as for {@link InAppStoryManager}
     * @param manager (manager) {@link AppearanceManager} for reader. May be null
     */
    public void showStory(final String storyId, final Context context, final AppearanceManager manager) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.singleStoryAPI().show(context, storyId, manager, null, 0);
            }
        });
    }

    public void preloadInAppMessages(
            final List<String> inAppMessageIds,
            final InAppMessageLoadCallback callback
    ) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.inAppMessageAPI().preload(inAppMessageIds, callback);
            }
        });
    }

    public void preloadInAppMessages(
            final InAppMessageLoadCallback callback
    ) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {

                core.inAppMessageAPI().preload(null, callback);
            }
        });
    }

    public void setInAppMessageLoadCallback(final InAppMessageLoadCallback callback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppMessageAPI().callback(callback);
            }
        });

    }

    public void setShowInAppMessageCallback(final ShowInAppMessageCallback callback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.SHOW_IN_APP_MESSAGE, callback);
            }
        });
    }

    public void setCloseInAppMessageCallback(final CloseInAppMessageCallback callback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.CLOSE_IN_APP_MESSAGE, callback);
            }
        });
    }


    public void setInAppMessageWidgetCallback(final InAppMessageWidgetCallback callback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.IN_APP_MESSAGE_WIDGET, callback);
            }
        });
    }

    public void showInAppMessage(
            final InAppMessageOpenSettings openData,
            final FragmentManager fragmentManager,
            final int containerId,
            final InAppMessageScreenActions screenActions
    ) {
        useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppMessageAPI().show(
                        openData,
                        fragmentManager,
                        containerId,
                        screenActions
                );
            }
        });

    }

    public boolean isSandbox() {
        return isSandbox;
    }

    public static class Builder {

        public boolean sandbox() {
            return sandbox;
        }

        public String userId() {
            return userId;
        }

        public String userSign() {
            return userSign;
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
        String userSign;
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
        public Builder userId(@NonNull String userId, String userSign) {
            Builder.this.userId = userId;
            Builder.this.userSign = userSign;
            return Builder.this;
        }

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
            if (INSTANCE.core != null)
                if (!WebViewUtils.isWebViewEnabled(INSTANCE.core)) {
                    showELog(IAS_ERROR_TAG, "Can't find Chromium WebView on a device");
                    return null;
                }
            INSTANCE.build(Builder.this);
            return INSTANCE;
        }
    }
}
