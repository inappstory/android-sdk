package com.inappstory.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.cache.OldStoryDownloader;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.callbacks.AppClickCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
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

    public void setTempShareId(String tempShareId) {
        this.tempShareId = tempShareId;
    }

    public void setTempShareStoryId(int tempShareStoryId) {
        this.tempShareStoryId = tempShareStoryId;
    }

    public int getTempShareStoryId() {
        return tempShareStoryId;
    }

    public String getTempShareId() {
        return tempShareId;
    }

    int tempShareStoryId;

    String tempShareId;

    public void setOldTempShareId(String tempShareId) {
        this.oldTempShareId = tempShareId;
    }

    public void setOldTempShareStoryId(int tempShareStoryId) {
        this.oldTempShareStoryId = tempShareStoryId;
    }

    public static boolean disableStatistic = true;

    public int getOldTempShareStoryId() {
        return oldTempShareStoryId;
    }

    public String getOldTempShareId() {
        return oldTempShareId;
    }

    int oldTempShareStoryId;

    String oldTempShareId;

    public ArrayList<String> getTags() {
        return tags;
    }

    //Test
    public void clearCache() {
        InAppStoryService.getInstance().getDownloadManager().clearCache();
    }

    public static void closeStoryReader() {
        CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM));
    }

    public void setUrlClickCallback(UrlClickCallback urlClickCallback) {
        CallbackManager.getInstance().setUrlClickCallback(urlClickCallback);
    }

    public void setShareCallback(ShareCallback shareCallback) {
        CallbackManager.getInstance().setShareCallback(shareCallback);
    }

    public void setAppClickCallback(AppClickCallback appClickCallback) {
        CallbackManager.getInstance().setAppClickCallback(appClickCallback);
    }

    //Test
    public String getTagsString() {
        if (tags == null) return null;
        return TextUtils.join(",", tags);
    }

    //Test
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    //Test
    public void addTags(ArrayList<String> newTags) {
        if (newTags == null || newTags.isEmpty()) return;
        if (tags == null) tags = new ArrayList<>();
        for (String tag : newTags) {
            addTag(tag);
        }
    }

    //Test
    public void removeTags(ArrayList<String> newTags) {
        if (tags == null || newTags == null || newTags.isEmpty()) return;
        for (String tag : newTags) {
            removeTag(tag);
        }
    }

    private void addTag(String tag) {
        if (!tags.contains(tag)) tags.add(tag);
    }

    private void removeTag(String tag) {
        if (tags.contains(tag)) tags.remove(tag);
    }

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

    public void setPlaceholders(@NonNull Map<String, String> placeholders) {

        for (String placeholderKey : placeholders.keySet()) {
            setPlaceholder(placeholderKey, placeholders.get(placeholderKey));
        }
    }

    ArrayList<String> tags;

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

    public boolean hasLike() {
        return hasLike;
    }

    public boolean hasShare() {
        return hasShare;
    }

    public boolean hasFavorite() {
        return hasFavorite;
    }

    public boolean hasLike = false;
    public boolean hasShare = false;
    public boolean hasFavorite = false;

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

    Intent intent;

    Messenger mService = null;

    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };

    public InAppStoryManager() {

    }

    private InAppStoryManager(Builder builder) throws DataException {

        KeyValueStorage.setContext(builder.context);
        SharedPreferencesAPI.setContext(builder.context);
        if (builder.context.getResources().getString(R.string.csApiKey).isEmpty() || builder.context.getResources().getString(R.string.csApiKey).equals("1")) {
            throw new DataException("'csApiKey' can't be empty", new Throwable("config is not valid"));
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
                builder.closeOnOverscroll,
                builder.closeOnSwipe,
                builder.hasFavorite,
                builder.hasLike,
                builder.hasShare,
                builder.sendStatistic);

        if (intent != null) {
            context.unbindService(mConnection);
            mBound = false;
        }
        try {
            intent = new Intent(context, InAppStoryService.class);
            context.startService(intent);
        } catch (IllegalStateException e) {

        }
    }

    private void setUserIdInner(String userId) throws DataException {
        if (InAppStoryService.getInstance() == null) return;
        if (userId == null)
            throw new DataException("'userId' can't be null, you can set '' instead", new Throwable("InAppStoryManager data is not valid"));
        if (userId.length() < 255) {
            if (this.userId.equals(userId)) return;
            localOpensKey = null;
            this.userId = userId;
            if (InAppStoryService.getInstance().getFavoriteImages() != null)
                InAppStoryService.getInstance().getFavoriteImages().clear();
            CsEventBus.getDefault().post(new ChangeUserIdEvent());
            SessionManager.getInstance().closeSession(sendStatistic, true);
        } else {
            throw new DataException("'userId' can't be longer than 255 characters", new Throwable("InAppStoryManager data is not valid"));
        }
    }

    //Test
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

    public boolean sendStatistic;

    private void initManager(Context context,
                             String cmsUrl,
                             String apiKey,
                             String testKey,
                             String userId,
                             ArrayList<String> tags,
                             Map<String, String> placeholders,
                             boolean closeOnOverscroll,
                             boolean closeOnSwipe,
                             boolean hasFavorite,
                             boolean hasLike,
                             boolean hasShare,
                             boolean sendStatistic) {
        this.context = context;
        soundOn = !context.getResources().getBoolean(R.bool.defaultMuted);
        this.tags = tags;
        if (placeholders != null)
            setPlaceholders(placeholders);
        this.sendStatistic = sendStatistic;
        this.closeOnOverscroll = closeOnOverscroll;
        this.closeOnSwipe = closeOnSwipe;
        this.hasFavorite = hasFavorite;
        this.hasLike = hasLike;
        this.hasShare = hasShare;
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
                .cmsKey(this.API_KEY)
                .setWebUrl(cmsUrl)
                .cmsUrl(cmsUrl);

    }

    public static void destroy() {
        if (INSTANCE != null) {
            if (InAppStoryService.getInstance() != null)
                InAppStoryService.getInstance().logout();
            StatisticSession.clear();
            INSTANCE.context = null;
            KeyValueStorage.removeString("managerInstance");
        }
        INSTANCE = null;
        InAppStoryService.getInstance().getDownloadManager().destroy();
    }

    private String localOpensKey;

    public String getLocalOpensKey() {
        if (localOpensKey == null && userId != null) {
            localOpensKey = "opened" + userId;
        }
        return localOpensKey;
    }

    public static InAppStoryManager getInstance() {
        return INSTANCE;
    }

    public static Pair<String, Integer> getLibraryVersion() {
        return new Pair<>(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }

    public Point coordinates = null;

    public boolean soundOn = false;

    public OnboardingLoadedListener onboardLoadedListener;
    public OnboardingLoadedListener singleLoadedListener;

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
        InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(stories);
        InAppStoryService.getInstance().getDownloadManager().loadStories(
                InAppStoryService.getInstance().getDownloadManager().getStories());
        ScreensManager.getInstance().openStoriesReader(outerContext, manager, storiesIds, 0, ShowStory.ONBOARDING);
        CsEventBus.getDefault().post(new OnboardingLoad(response.size()));
        if (onboardLoadedListener != null) {
            onboardLoadedListener.onLoad();
        }
    }

    private void showOnboardingStoriesInner(final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        if (InAppStoryService.getInstance() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStories(tags, outerContext, manager);
                }
            }, 1000);
            return;
        }
        if (StoriesActivity.destroyed == -1) {

            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStories(tags, outerContext, manager);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        } else if (System.currentTimeMillis() - StoriesActivity.destroyed < 1000) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStories(tags, outerContext, manager);
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
                        showLoadedOnboardings(response, outerContext, manager);
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

    public void showOnboardingStories(List<String> tags, Context outerContext, AppearanceManager manager) {
        showOnboardingStoriesInner(tags, outerContext, manager);
    }

    public void showOnboardingStories(Context context, final AppearanceManager manager) {
        showOnboardingStories(getTags(), context, manager);
    }

    private void showStoryInner(final String storyId, final Context context, final AppearanceManager manager, final IShowStoryCallback callback) {
        if (InAppStoryService.getInstance() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStoryInner(storyId, context, manager, callback);
                }
            }, 1000);
            return;
        }
        if (StoriesActivity.destroyed == -1) {
            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStoryInner(storyId, context, manager, callback);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
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
                    if (callback != null)
                        callback.onShow();
                    if (story.deeplink != null) {
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
                    InAppStoryService.getInstance().getDownloadManager().loadStories(
                            InAppStoryService.getInstance().getDownloadManager().getStories());
                    ArrayList<Integer> stIds = new ArrayList<>();
                    stIds.add(story.id);
                    ScreensManager.getInstance().openStoriesReader(context, manager, stIds, 0, ShowStory.SINGLE);
                } else {
                    if (callback != null)
                        callback.onError();
                    return;
                }
            }

            @Override
            public void loadError(int type) {
                if (callback != null)
                    callback.onError();
            }

            @Override
            public void getPartialStory(Story story) {

            }
        }, storyId);
    }

    public void showStory(String storyId, Context context, AppearanceManager manager, IShowStoryCallback callback) {
        showStoryInner(storyId, context, manager, callback);
    }

    public void showStory(String storyId, Context context, AppearanceManager manager) {
        showStoryInner(storyId, context, manager, null);
    }

    public static class Builder {

        Context context;

        public boolean sandbox() {
            return sandbox;
        }

        public boolean closeOnOverscroll() {
            return closeOnOverscroll;
        }

        public boolean closeOnSwipe() {
            return closeOnSwipe;
        }

        public boolean hasLike() {
            return hasLike;
        }

        public boolean hasFavorite() {
            return hasFavorite;
        }

        public boolean hasShare() {
            return hasShare;
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

        public Map<String, String> placeholders() {
            return placeholders;
        }

        boolean sandbox = true;
        boolean closeOnOverscroll = true;
        boolean closeOnSwipe = true;
        boolean hasLike = false;
        boolean sendStatistic = true;
        boolean hasFavorite = false;
        boolean hasShare = false;
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

        public Builder sandbox(boolean sandbox) {
            Builder.this.sandbox = sandbox;
            return Builder.this;
        }


        public Builder closeOnSwipe(boolean closeOnSwipe) {
            Builder.this.closeOnSwipe = closeOnSwipe;
            return Builder.this;
        }

        public Builder closeOnOverscroll(boolean closeOnOverscroll) {
            Builder.this.closeOnOverscroll = closeOnOverscroll;
            return Builder.this;
        }

        public Builder hasFavorite(boolean hasFavorite) {
            Builder.this.hasFavorite = hasFavorite;
            return Builder.this;
        }

        public Builder hasShare(boolean hasShare) {
            Builder.this.hasShare = hasShare;
            return Builder.this;
        }

        public Builder hasLike(boolean hasLike) {
            Builder.this.hasLike = hasLike;
            return Builder.this;
        }

        public Builder sendStatistic(boolean sendStatistic) {
            Builder.this.sendStatistic = sendStatistic;
            return Builder.this;
        }

        public Builder apiKey(String apiKey) {
            Builder.this.apiKey = apiKey;
            return Builder.this;
        }

        public Builder testKey(String testKey) {
            Builder.this.testKey = testKey;
            return Builder.this;
        }

        public Builder userId(String userId) throws DataException {
            if (userId.length() < 255) {
                Builder.this.userId = userId;
            } else {
                throw new DataException("'userId' can't be longer than 255 characters", new Throwable("InAppStoryManager.Builder data is not valid"));
            }
            return Builder.this;
        }

        public Builder tags(String... tags) {
            Builder.this.tags = new ArrayList<>();
            for (int i = 0; i < tags.length; i++) {
                Builder.this.tags.add(tags[i]);
            }
            return Builder.this;
        }

        public Builder tags(ArrayList<String> tags) {
            Builder.this.tags = tags;
            return Builder.this;
        }

        public Builder placeholders(Map<String, String> placeholders) {
            Builder.this.placeholders = placeholders;
            return Builder.this;
        }

        public InAppStoryManager create() throws DataException {
            if (Builder.this.context == null) {
                throw new DataException("'context' can't be null", new Throwable("InAppStoryManager.Builder data is not valid"));
            }
            return new InAppStoryManager(Builder.this);
        }
    }
}
