package com.inappstory.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.StatisticResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.ChangeUserIdEvent;
import com.inappstory.sdk.stories.events.ChangeUserIdForListEvent;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.ui.reader.StoriesDialogFragment;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.Sizes;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.InAppStoryService.checkOpenStatistic;

public class InAppStoryManager {

    private static InAppStoryManager INSTANCE;

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


    public void clearCache() {
        StoryDownloader.clearCache();
    }

    public interface UrlClickCallback {
        void onUrlClick(String url);
    }

    public interface AppClickCallback {
        void onAppClick(String type, String data);
    }

    private UrlClickCallback urlClickCallback;
    private AppClickCallback appClickCallback;

    public static void closeStoryReader() {
        CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CUSTOM));
    }

    public interface ShareCallback {
        void onShare(String url, String title, String description, String id);
    }

    public ShareCallback shareCallback;

    public void setUrlClickCallback(UrlClickCallback urlClickCallback) {
        this.urlClickCallback = urlClickCallback;
    }

    public UrlClickCallback getUrlClickCallback() {
        return urlClickCallback;
    }

    public void setAppClickCallback(AppClickCallback appClickCallback) {
        this.appClickCallback = appClickCallback;
    }

    public AppClickCallback getAppClickCallback() {
        return appClickCallback;
    }


    String getTagsString() {
        if (tags == null) return null;
        return TextUtils.join(",", tags);
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void addTags(ArrayList<String> newTags) {
        if (newTags == null || newTags.isEmpty()) return;
        if (tags == null) tags = new ArrayList<>();
        for (String tag : newTags) {
            addTag(tag);
        }
    }

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


    ArrayList<String> tags;

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

    boolean hasLike = false;
    boolean hasShare = false;
    boolean hasFavorite = false;

    private static final String TEST_DOMAIN = "https://api.test.inappstory.com/";
    private static final String PRODUCT_DOMAIN = "https://api.inappstory.com/";

    public String getApiKey() {
        return API_KEY;
    }

    public String getTestKey() {
        return TEST_KEY;
    }

    String API_KEY = "";
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


    private InAppStoryManager(Builder builder) throws DataException {

        KeyValueStorage.setContext(builder.context);
        SharedPreferencesAPI.setContext(builder.context);
        if (builder.context.getResources().getString(R.string.csApiKey).isEmpty()) {
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
                builder.closeOnOverscroll,
                builder.closeOnSwipe,
                builder.hasFavorite,
                builder.hasLike,
                builder.hasShare);

        if (intent != null) {
            context.unbindService(mConnection);
            mBound = false;
        }
        try {
            intent = new Intent(context, InAppStoryService.class);
            context.startService(intent);
        } catch (IllegalStateException e) {
          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InAppStoryService.getInstance().startForegr();
                    }
                }, 2000);
            } else {

            }*/
        }
    }

    public void setUserId(String userId) throws DataException {
        if (InAppStoryService.getInstance() == null) return;
        if (userId.length() < 255) {
            if (this.userId.equals(userId)) return;
            localOpensKey = null;
            this.userId = userId;
            if (InAppStoryService.getInstance().favoriteImages != null)
                InAppStoryService.getInstance().favoriteImages.clear();
            CsEventBus.getDefault().post(new ChangeUserIdEvent());
            if (StatisticSession.getInstance().id != null) {
                NetworkClient.getApi().statisticsClose(new StatisticSendObject(StatisticSession.getInstance().id,
                        InAppStoryService.getInstance().statistic)).enqueue(new NetworkCallback<StatisticResponse>() {
                    @Override
                    public void onSuccess(StatisticResponse response) {
                        CsEventBus.getDefault().post(new ChangeUserIdForListEvent());
                    }

                    @Override
                    public Type getType() {
                        return StatisticResponse.class;
                    }

                    @Override
                    public void onError(int code, String message) {
                        CsEventBus.getDefault().post(new ChangeUserIdForListEvent());
                    }
                });
            }
            StatisticSession.clear();
        } else {
            throw new DataException("'userId' can't be longer than 255 characters", new Throwable("InAppStoryManager data is not valid"));
        }
    }

    private String userId;

    public String getUserId() {
        return userId;
    }


    public void setActionBarColor(int actionBarColor) {
        this.actionBarColor = actionBarColor;
    }

    public int actionBarColor = -1;

    private void initManager(Context context, String cmsUrl, String apiKey, String testKey, String userId, ArrayList<String> tags,
                             boolean closeOnOverscroll,
                             boolean closeOnSwipe,
                             boolean hasFavorite,
                             boolean hasLike,
                             boolean hasShare) {
        this.context = context;
        this.tags = tags;
        this.closeOnOverscroll = closeOnOverscroll;
        this.closeOnSwipe = closeOnSwipe;
        this.hasFavorite = hasFavorite;
        this.hasLike = hasLike;
        this.hasShare = hasShare;
        this.API_KEY = apiKey;
        this.TEST_KEY = testKey;
        // ApiClient.setContext(context);
        NetworkClient.setContext(context);
        this.userId = userId;
       /* if (actionBarColor == -1) {
            actionBarColor = context.getResources().getColor(R.color.nar_readerActionBarColor);
        }*/
//        EventBus.getDefault().register(this);
        if (INSTANCE != null) {
            destroy();
        }
        if (InAppStoryService.getInstance() != null) {
            InAppStoryService.getInstance().statistic = new ArrayList<>();
        }
        INSTANCE = this;
        ApiSettings
                .getInstance()
                .cacheDirPath(context.getCacheDir().getAbsolutePath())
                .cmsId("1")
                .cmsKey(this.API_KEY)
                .setWebUrl(cmsUrl)
                .cmsUrl(cmsUrl);

    }

    public static void destroy() {
        if (INSTANCE != null) {
            if (InAppStoryService.getInstance() != null)
                InAppStoryService.getInstance().logout();
            INSTANCE.context = null;
            KeyValueStorage.removeString("managerInstance");
            try {
                // EventBus.getDefault().unregister(INSTANCE);
            } catch (Exception e) {

            }
        }
        INSTANCE = null;
        StoryDownloader.destroy();
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

    public interface OnboardingLoadedListener {
        void onLoad();

        void onEmpty();

        void onError();
    }

    public Point coordinates = null;

    public OnboardingLoadedListener onboardLoadedListener;
    public OnboardingLoadedListener singleLoadedListener;

    public void showOnboardingStories(final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        if (InAppStoryService.getInstance() == null) {
            Log.d("ShowStories", "Onboarding service is null");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStories(tags, outerContext, manager);
                }
            }, 1000);
            return;
        }
        if (StoriesActivity.destroyed == -1) {

            Log.d("ShowStories", "Onboarding readerOpened");
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
            Log.d("ShowStories", "Onboarding openedPause");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStories(tags, outerContext, manager);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        }

        Log.d("ShowStories", "Onboarding statisticClosed");
        if (checkOpenStatistic(new InAppStoryService.CheckStatisticCallback() {
            @Override
            public void openStatistic() {
                showOnboardingStories(tags == null ? getTags() : tags, outerContext, manager);
            }

            @Override
            public void errorStatistic() {
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_ONBOARD));
            }
        })) {
            Log.d("ShowStories", "Onboarding statisticOpened");
            NetworkClient.getApi().onboardingStories(StatisticSession.getInstance().id, tags == null ? getTags() : tags,
                    getApiKey()).enqueue(new NetworkCallback<List<Story>>() {
                @Override
                public void onSuccess(List<Story> response) {
                    if (response == null || response.size() == 0) {
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
                    StoryDownloader.getInstance().uploadingAdditional(stories);
                    StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(),
                            storiesIds.get(0));
                    if (Sizes.isTablet() && outerContext != null) {
                        DialogFragment settingsDialogFragment = new StoriesDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", 0);
                        bundle.putInt("source", ShowStory.ONBOARDING);
                        bundle.putIntegerArrayList("stories_ids", storiesIds);
                        if (manager != null) {
                            bundle.putInt(CS_CLOSE_POSITION, manager.csClosePosition());
                            bundle.putInt(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
                        }
                        settingsDialogFragment.setArguments(bundle);
                        settingsDialogFragment.show(
                                ((AppCompatActivity) context).getSupportFragmentManager(),
                                "DialogFragment");
                    } else {
                        Intent intent2 = new Intent(InAppStoryManager.getInstance().getContext(), StoriesActivity.class);
                        intent2.putExtra("index", 0);
                        intent2.putExtra("source", ShowStory.ONBOARDING);
                        intent2.putIntegerArrayListExtra("stories_ids", storiesIds);
                        if (manager != null) {
                            intent2.putExtra(CS_CLOSE_POSITION, manager.csClosePosition());
                            intent2.putExtra(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
                        }
                        if (outerContext == null) {
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            InAppStoryManager.getInstance().getContext().startActivity(intent2);
                        } else {
                            outerContext.startActivity(intent2);
                        }
                    }

                    if (onboardLoadedListener != null) {
                        onboardLoadedListener.onLoad();
                    }
                }

                @Override
                public Type getType() {
                    // List<Story> c = new ArrayList<Story>();
                    ParameterizedType ptype = new ParameterizedType() {
                        @NonNull
                        @Override
                        public Type[] getActualTypeArguments() {
                            return new Type[]{Story.class};
                        }

                        @NonNull
                        @Override
                        public Type getRawType() {
                            return List.class;
                        }

                        @Nullable
                        @Override
                        public Type getOwnerType() {
                            return List.class;
                        }
                    };
                    return ptype;
                }

                @Override
                public void onError(int code, String message) {

                    if (onboardLoadedListener != null) {
                        onboardLoadedListener.onError();
                    }
                    CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_ONBOARD));
                }
            });
        }
    }

    public void showOnboardingStories(Context context, final AppearanceManager manager) {
        showOnboardingStories(getTags(), context, manager);
    }


    public void showStory(final String storyId, final Context context, final AppearanceManager manager, final IShowStoryCallback callback) {

        if (InAppStoryService.getInstance() == null) {
            Log.d("ShowStories", "Single service is null");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStory(storyId, context, manager, callback);
                }
            }, 1000);
            return;
        }
        if (StoriesActivity.destroyed == -1) {
            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.AUTO));

            Log.d("ShowStories", "Single readerOpened");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStory(storyId, context, manager, callback);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        } else if (System.currentTimeMillis() - StoriesActivity.destroyed < 1000) {
            Log.d("ShowStories", "Single openedPause");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStory(storyId, context, manager, callback);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        }

        Log.d("ShowStories", "Single statisticClosed");
        InAppStoryService.getInstance().getFullStoryByStringId(new GetStoryByIdCallback() {
            @Override
            public void getStory(Story story) {
                if (story != null) {
                    if (callback != null)
                        callback.onShow();
                    if (story.deeplink != null) {
                        InAppStoryService.getInstance().addDeeplinkClickStatistic(story.id);
                        if (InAppStoryManager.getInstance().getUrlClickCallback() != null) {
                            InAppStoryManager.getInstance().getUrlClickCallback().onUrlClick(story.deeplink);
                        } else {
                            if (!InAppStoryService.getInstance().isConnected()) {
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


                    Log.d("ShowStories", "Single statisticOpened");
                    if (Sizes.isTablet() && context != null) {
                        StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(),
                                story.id);
                        DialogFragment settingsDialogFragment = new StoriesDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", 0);
                        bundle.putInt("source", ShowStory.SINGLE);
                        if (manager != null)
                            bundle.putInt(CS_CLOSE_POSITION, manager.csClosePosition());
                        ArrayList<Integer> stIds = new ArrayList<>();
                        stIds.add(story.id);
                        bundle.putIntegerArrayList("stories_ids", stIds);
                        settingsDialogFragment.setArguments(bundle);
                        settingsDialogFragment.show(
                                ((AppCompatActivity) context).getSupportFragmentManager(),
                                "DialogFragment");
                    } else {
                        StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(),
                                story.id);
                        Intent intent2 = new Intent(InAppStoryManager.getInstance().getContext(), StoriesActivity.class);
                        if (context == null)
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.putExtra("index", 0);
                        intent2.putExtra("source", ShowStory.SINGLE);
                        if (manager != null)
                            intent2.putExtra(CS_CLOSE_POSITION, manager.csClosePosition());
                        ArrayList<Integer> stIds = new ArrayList<>();
                        stIds.add(story.id);
                        intent2.putIntegerArrayListExtra("stories_ids", stIds);
                        if (context == null)
                            InAppStoryManager.getInstance().getContext().startActivity(intent2);
                        else
                            context.startActivity(intent2);
                    }
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

    public void showStory(final String storyId, final Context context, final AppearanceManager manager) {
        showStory(storyId, context, manager, null);
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

        boolean sandbox = true;
        boolean closeOnOverscroll = true;
        boolean closeOnSwipe = true;
        boolean hasLike = false;
        boolean hasFavorite = false;
        boolean hasShare = false;
        String userId;
        String apiKey;
        String testKey;
        ArrayList<String> tags;

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
            for (int i = 1; i < tags.length; i++) {
                Builder.this.tags.add(tags[i]);
            }
            return Builder.this;
        }

        public Builder tags(ArrayList<String> tags) {
            Builder.this.tags = tags;
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
