package io.casestory.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.exceptions.DataException;
import io.casestory.sdk.network.NetworkCallback;
import io.casestory.sdk.network.NetworkClient;
import io.casestory.sdk.stories.api.models.StatisticResponse;
import io.casestory.sdk.stories.api.models.StatisticSendObject;
import io.casestory.sdk.stories.api.models.StatisticSession;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import io.casestory.sdk.stories.api.networkclient.ApiSettings;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ChangeUserIdEvent;
import io.casestory.sdk.stories.events.ChangeUserIdForListEvent;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.events.StoriesErrorEvent;
import io.casestory.sdk.stories.ui.reader.StoriesActivity;
import io.casestory.sdk.stories.ui.reader.StoriesDialogFragment;
import io.casestory.sdk.stories.utils.KeyValueStorage;
import io.casestory.sdk.stories.utils.Sizes;

import static io.casestory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static io.casestory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static io.casestory.sdk.CaseStoryService.checkOpenStatistic;

public class CaseStoryManager {

    private static CaseStoryManager INSTANCE;

    public Context getContext() {
        return context;
    }

    Context context;

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

    private static final String TEST_DOMAIN = "https://api.casestory.io/";
    private static final String PRODUCT_DOMAIN = "https://api.casestory.io/";

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


    private CaseStoryManager(Builder builder) {

        KeyValueStorage.setContext(builder.context);
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
        intent = new Intent(context, CaseStoryService.class);
        context.startService(intent);
    }

    public void setUserId(String userId) throws DataException {
        if (userId.length() < 255) {
            this.userId = userId;
            EventBus.getDefault().post(new ChangeUserIdEvent());
            StatisticSession.clear();
            NetworkClient.getApi().statisticsClose(new StatisticSendObject(StatisticSession.getInstance().id,
                    CaseStoryService.getInstance().statistic)).enqueue(new NetworkCallback<StatisticResponse>() {
                @Override
                public void onSuccess(StatisticResponse response) {
                    EventBus.getDefault().post(new ChangeUserIdForListEvent());
                }

                @Override
                public Type getType() {
                    return StatisticResponse.class;
                }

                @Override
                public void onError(int code, String message) {
                    EventBus.getDefault().post(new ChangeUserIdForListEvent());
                }
            });
        } else {
            throw new DataException("'userId' can't be longer than 255 characters", new Throwable("CaseStoryManager data is not valid"));
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
        if (CaseStoryService.getInstance() != null) {
            CaseStoryService.getInstance().statistic = new ArrayList<>();
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
            CaseStoryService.getInstance().logout();
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


    public static CaseStoryManager getInstance() {
        return INSTANCE;
    }

    public interface OnboardingLoadedListener {
        void onLoad();

        void onEmpty();

        void onError();
    }

    public Point coordinates = null;

    public OnboardingLoadedListener popupLoadedListener;
    public OnboardingLoadedListener singleLoadedListener;

    public void showOnboardingStories(final List<String> tags, final Context outerContext, final AppearanceManager manager) {
        if (CaseStoryService.getInstance() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOnboardingStories(tags, outerContext, manager);
                }
            }, 1000);
            return;
        }
        if (checkOpenStatistic(new CaseStoryService.CheckStatisticCallback() {
            @Override
            public void openStatistic() {
                showOnboardingStories(tags == null ? getTags() : tags, outerContext, manager);
            }

            @Override
            public void errorStatistic() {
                EventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_ONBOARD));
            }
        })) {
            NetworkClient.getApi().onboardingStories(StatisticSession.getInstance().id, tags == null ? getTags() : tags,
                    getApiKey()).enqueue(new NetworkCallback<List<Story>>() {
                @Override
                public void onSuccess(List<Story> response) {
                    if (response == null || response.size() == 0) return;
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
                        Intent intent2 = new Intent(CaseStoryManager.getInstance().getContext(), StoriesActivity.class);
                        intent2.putExtra("index", 0);
                        intent2.putExtra("isOnboarding", true);
                        intent2.putIntegerArrayListExtra("stories_ids", storiesIds);
                        if (manager != null) {
                            intent2.putExtra(CS_CLOSE_POSITION, manager.csClosePosition());
                            intent2.putExtra(CS_STORY_READER_ANIMATION, manager.csStoryReaderAnimation());
                        }
                        if (outerContext == null) {
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            CaseStoryManager.getInstance().getContext().startActivity(intent2);
                        } else {
                            outerContext.startActivity(intent2);
                        }
                    }
                }

                @Override
                public Type getType() {
                    return new TypeToken<List<Story>>() {}.getType();
                }

                @Override
                public void onError(int code, String message) {
                    EventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_ONBOARD));
                }
            });
        }
    }

    public void showOnboardingStories(Context context, final AppearanceManager manager) {
        showOnboardingStories(getTags(), context, manager);
    }

    public void showStory(final String storyId, final Context context, final AppearanceManager manager) {
        if (StoriesActivity.destroyed == -1) {
            EventBus.getDefault().post(new CloseStoryReaderEvent());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStory(storyId, context, manager);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        } else if (System.currentTimeMillis() - StoriesActivity.destroyed < 1000) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStory(storyId, context, manager);
                    StoriesActivity.destroyed = 0;
                }
            }, 350);
            return;
        }
        if (CaseStoryService.getInstance() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStory(storyId, context, manager);
                }
            }, 1000);
            return;
        }
        if (Sizes.isTablet() && context != null) {
            CaseStoryService.getInstance().getFullStoryByStringId(new GetStoryByIdCallback() {
                @Override
                public void getStory(Story story) {
                    StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(),
                            story.id);
                    DialogFragment settingsDialogFragment = new StoriesDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("index", 0);
                    if (manager != null)
                        bundle.putInt(CS_CLOSE_POSITION, manager.csClosePosition());
                    ArrayList<Integer> stIds = new ArrayList<>();
                    stIds.add(story.id);
                    bundle.putIntegerArrayList("stories_ids", stIds);
                    settingsDialogFragment.setArguments(bundle);
                    settingsDialogFragment.show(
                            ((AppCompatActivity) context).getSupportFragmentManager(),
                            "DialogFragment");
                }

                @Override
                public void loadError(int type) {

                }

                @Override
                public void getPartialStory(Story story) {

                }
            }, storyId);
        } else {
            CaseStoryService.getInstance().getFullStoryByStringId(new GetStoryByIdCallback() {
                @Override
                public void getStory(Story story) {
                    StoryDownloader.getInstance().loadStories(StoryDownloader.getInstance().getStories(),
                            story.id);
                    Intent intent2 = new Intent(CaseStoryManager.getInstance().getContext(), StoriesActivity.class);
                    if (context == null)
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.putExtra("index", 0);
                    if (manager != null)
                        intent2.putExtra(CS_CLOSE_POSITION, manager.csClosePosition());
                    ArrayList<Integer> stIds = new ArrayList<>();
                    stIds.add(story.id);
                    intent2.putIntegerArrayListExtra("stories_ids", stIds);
                    if (context == null)
                        CaseStoryManager.getInstance().getContext().startActivity(intent2);
                    else
                        context.startActivity(intent2);
                }

                @Override
                public void loadError(int type) {

                }

                @Override
                public void getPartialStory(Story story) {

                }
            }, storyId);
        }
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
                throw new DataException("Context must not be null", new Throwable("CaseStoryManager.Builder data is not valid"));
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
                throw new DataException("'userId' can't be longer than 255 characters", new Throwable("CaseStoryManager.Builder data is not valid"));
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

        public CaseStoryManager create() throws DataException {
            if (Builder.this.context == null) {
                throw new DataException("'context' can't be null", new Throwable("CaseStoryManager.Builder data is not valid"));
            }
            return new CaseStoryManager(Builder.this);
        }
    }
}
