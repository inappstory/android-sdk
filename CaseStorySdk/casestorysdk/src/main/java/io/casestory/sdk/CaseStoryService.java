package io.casestory.sdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.stories.api.models.CacheFontObject;
import io.casestory.sdk.stories.api.models.StatisticResponse;
import io.casestory.sdk.stories.api.models.StatisticSendObject;
import io.casestory.sdk.stories.api.models.StatisticSession;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.callbacks.CloseStatisticCallback;
import io.casestory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import io.casestory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import io.casestory.sdk.stories.api.models.callbacks.OpenStatisticCallback;
import io.casestory.sdk.stories.api.networkclient.ApiClient;
import io.casestory.sdk.stories.api.networkclient.RetrofitCallback;
import io.casestory.sdk.stories.cache.Downloader;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ContentLoadedEvent;
import io.casestory.sdk.stories.events.ListVisibilityEvent;
import io.casestory.sdk.stories.events.LoadFavNarratives;
import io.casestory.sdk.stories.events.LoadNarrativesOnEmpty;
import io.casestory.sdk.stories.events.LoadNarrativesOnError;
import io.casestory.sdk.stories.events.NextStoryReaderEvent;
import io.casestory.sdk.stories.events.PrevStoryReaderEvent;
import io.casestory.sdk.stories.events.ReloadStoriesManagerNarratives;
import io.casestory.sdk.stories.events.StoriesErrorEvent;
import io.casestory.sdk.stories.utils.Sizes;

public class CaseStoryService extends Service {


    public static CaseStoryService getInstance() {
        return INSTANCE;
    }


    public static CaseStoryService INSTANCE;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void setUserId() {

    }

    void logout() {

    }

    LoadStoriesCallback loadStoriesCallback;

    public void loadStories(final LoadStoriesCallback callback) {
        loadStoriesCallback = callback;
        if (isConnected()) {
            if (StatisticSession.getInstance() == null
                    || StatisticSession.getInstance().id == null
                    || StatisticSession.getInstance().id.isEmpty()) {
                openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        loadStories(loadStoriesCallback);
                    }

                    @Override
                    public void onError() {
                        EventBus.getDefault().post(new LoadNarrativesOnError());
                    }
                });
                return;
            } else if (StatisticSession.needToUpdate()) {
                openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        ApiClient.getApi().getStories(StatisticSession.getInstance().id, getTags(), getTestKey(),
                                getApiKey()).enqueue(reloadCallback);
                    }

                    @Override
                    public void onError() {
                        EventBus.getDefault().post(new LoadNarrativesOnError());
                    }
                });
            } else {
                ApiClient.getApi().getStories(StatisticSession.getInstance().id, getTags(), getTestKey(),
                        getApiKey()).enqueue(reloadCallback);
            }
        } else {
            EventBus.getDefault().post(new LoadNarrativesOnError());
        }
    }

    private String getTags() {
        return CaseStoryManager.getInstance().getTagsString();
    }
    private String getTestKey() {
        return CaseStoryManager.getInstance().getTestKey();
    }


    public List<Story> favNarratives = new ArrayList<>();

    RetrofitCallback reloadCallback = new RetrofitCallback<List<Story>>() {

        boolean isRefreshing = false;

        @Override
        public void onError(int code, String message) {

            EventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
            EventBus.getDefault().post(new LoadNarrativesOnError());
            super.onError(code, message);
        }

        @Override
        protected void error424(String message) {
            EventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
            if (!openProcess)
                openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isRefreshing)
                            isRefreshing = true;

                        ApiClient.getApi().getStories(StatisticSession.getInstance().id, getTags(), getTestKey(),
                                getApiKey()).enqueue(reloadCallback);
                    }

                    @Override
                    public void onError() {
                        EventBus.getDefault().post(new LoadNarrativesOnError());
                    }
                });
        }

        @Override
        public void onSuccess(final List<Story> response) {
            if (response == null || response.size() == 0) {
                EventBus.getDefault().post(new LoadNarrativesOnEmpty());
                EventBus.getDefault().post(new ContentLoadedEvent(true));
            } else {
                EventBus.getDefault().post(new ContentLoadedEvent(false));
            }
            StoryDownloader.getInstance().uploadingAdditional(response);
            EventBus.getDefault().post(new ListVisibilityEvent());
            Log.e("reloadNarratives", "success2");
            List<Story> newStories = new ArrayList<>();
            if (StoryDownloader.getInstance().getStories() != null) {
                for (Story story : response) {
                    if (!StoryDownloader.getInstance().getStories().contains(story)) {
                        newStories.add(story);
                    }
                }
            }
            if (newStories != null && newStories.size() > 0) {
                try {
                    StoryDownloader.getInstance().uploadingAdditional(newStories);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (CaseStoryManager.getInstance().hasFavorite) {
                ApiClient.getApi().getStories(StatisticSession.getInstance().id, getTestKey(), 1,
                        null, "id, background_color, image",
                        getApiKey()).enqueue(new RetrofitCallback<List<Story>>() {
                    @Override
                    public void onSuccess(List<Story> response2) {
                        favNarratives.clear();
                        favNarratives.addAll(response2);
                        EventBus.getDefault().post(new LoadFavNarratives());
                        if (response2 != null && response2.size() > 0) {
                            for (Story story : response2) {
                                Glide.with(getApplicationContext())
                                        .asBitmap()
                                        .load(story.getImage().get(0).getUrl())
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                            }
                            if (loadStoriesCallback != null) {
                                List<Integer> ids = new ArrayList<>();
                                for (Story story : response) {
                                    ids.add(story.id);
                                }
                                loadStoriesCallback.storiesLoaded(ids);
                            }

                        }
                    }

                    @Override
                    public void onError(int code, String m) {
                        if (loadStoriesCallback != null) {
                            List<Integer> ids = new ArrayList<>();
                            for (Story story : response) {
                                ids.add(story.id);
                            }
                            loadStoriesCallback.storiesLoaded(ids);
                        }
                    }
                });
            } else {
                if (loadStoriesCallback != null) {
                    List<Integer> ids = new ArrayList<>();
                    for (Story story : response) {
                        ids.add(story.id);
                    }
                    loadStoriesCallback.storiesLoaded(ids);
                }
            }
        }
    };

    private int currentId;

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    private int currentIndex;

    public long lastTapEventTime = 0;
    public boolean cubeAnimation = false;

    @Subscribe
    public void nextNarrativeEvent(NextStoryReaderEvent event) {
        lastTapEventTime = System.currentTimeMillis() + 100;
        cubeAnimation = true;
    }


    @Subscribe
    public void prevNarrativeEvent(PrevStoryReaderEvent event) {
        lastTapEventTime = System.currentTimeMillis() + 100;
        cubeAnimation = true;
    }

    public boolean isConnected() {
        if (CaseStoryManager.getInstance().context == null) return false;
        try {
            ConnectivityManager cm = (ConnectivityManager) CaseStoryManager.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            return true;
        }
    }

    public void openStatistic(final OpenStatisticCallback callback) {
        Context context = CaseStoryManager.getInstance().context;
        openProcess = true;
        String platform = "android";
        // Log.e("Login3", getInstance().uniqueHash);
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);// Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // deviceId = deviceId + "1";
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String screenWidth = Integer.toString(Sizes.getScreenSize().x);
        String screenHeight = Integer.toString(Sizes.getScreenSize().y);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        String screenDpi = Float.toString(metrics.density * 160f);
        String osVersion = Build.VERSION.CODENAME;
        String osSdkVersion = Integer.toString(Build.VERSION.SDK_INT);
        String appPackageId = context.getPackageName();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersion = (pInfo != null ? pInfo.versionName : "");
        String appBuild = (pInfo != null ? Integer.toString(pInfo.versionCode) : "");
        if (!isConnected()) {
            return;
        }
        ApiClient.getFastApi().statisticsOpen(
                "cache",
                CaseStoryManager.getInstance().getTagsString(),
                "animation,data,deeplink",
                platform,
                deviceId,
                model,
                manufacturer,
                brand,
                screenWidth,
                screenHeight,
                screenDpi,
                osVersion,
                osSdkVersion,
                appPackageId,
                appVersion,
                appBuild,
                CaseStoryManager.getInstance().getUserId()
        ).enqueue(new RetrofitCallback<StatisticResponse>() {
            @Override
            public void onSuccess(final StatisticResponse response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        openProcess = false;
                        //getInstance().share = response.share;
                        response.session.save();
                        if (callback != null)
                            callback.onSuccess();
                        getInstance().handler.postDelayed(getInstance().statisticUpdateThread, statisticUpdateInterval);
                        if (response.cachedFonts != null) {
                            for (CacheFontObject cacheFontObject : response.cachedFonts) {
                                Downloader.downFontFile(CaseStoryManager.getInstance().context, cacheFontObject.url);
                            }
                        }
                    }
                });

            }

            @Override
            public void onError(int code, String message) {
                EventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.OPEN_SESSION));
                super.onError(code, message);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null)
                            callback.onError();
                    }
                });
            }

            @Override
            public void onTimeout() {
                EventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.OPEN_SESSION));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null)
                            callback.onError();
                    }
                });
            }
        });
    }

    public void closeStatistic(CloseStatisticCallback callback) {

    }

    private static final long statisticUpdateInterval = 30000;

    private Handler handler = new Handler();

    List<List<Object>> statistic;

    public Runnable statisticUpdateThread = new Runnable() {
        @Override
        public void run() {
            if (CaseStoryManager.getInstance().context == null || getInstance() == null) {
                handler.removeCallbacks(statisticUpdateThread);
                return;
            }
            if (sendStatistic()) {
                handler.postDelayed(statisticUpdateThread, statisticUpdateInterval);
            }
        }
    };

    private boolean sendStatistic() {
        if (!isConnected()) return true;
        if (StatisticSession.getInstance().id == null || StatisticSession.needToUpdate())
            return false;
        if (statistic == null || (statistic.isEmpty() && !StatisticSession.needToUpdate())) {
            return true;
        }
        try {
            ApiClient.getApi().statisticsUpdate(
                    new StatisticSendObject(StatisticSession.getInstance().id,
                            statistic)).enqueue(new RetrofitCallback<StatisticResponse>() {
                @Override
                public void onSuccess(StatisticResponse response) {
                    StatisticSession.getInstance();
                    StatisticSession.updateStatistic();
                    if (statistic == null) return;
                    statistic.clear();
                }
            });
        } catch (Exception e) {
        }
        return true;
    }


    public static boolean openProcess = false;

    public void getStoryById(final GetStoryByIdCallback storyByIdCallback, int id) {
        for (Story story : StoryDownloader.getInstance().getStories()) {
            if (story.id == id) {
                storyByIdCallback.getStory(story);
                return;
            }
        }
        ApiClient.getApi().getStoryById(Integer.toString(id), StatisticSession.getInstance().id,
                getApiKey(), EXPAND_STRING
        ).enqueue(new RetrofitCallback<Story>() {
            @Override
            public void onSuccess(final Story response) {
                StoryDownloader.getInstance().uploadingAdditional(new ArrayList<Story>() {{
                    add(response);
                }});
                storyByIdCallback.getStory(response);
            }
        });
    }

    private static final String EXPAND_STRING = "slides_html,layout,slides_duration";

    private String getApiKey() {
        return CaseStoryManager.getInstance().getApiKey();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(NOTIFICATION_ID, notification);
        }
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        INSTANCE = this;
    }


    public static final String CHANNEL_ID = "caseStorySdk";
    private static final String CHANNEL_NAME = "caseStorySdk";
    private static final int NOTIFICATION_ID = 791;

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        INSTANCE = this;
        return START_NOT_STICKY;
    }

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
