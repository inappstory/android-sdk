package io.casestory.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.eventbus.ThreadMode;
import io.casestory.sdk.stories.api.models.CacheFontObject;
import io.casestory.sdk.stories.api.models.StatisticResponse;
import io.casestory.sdk.stories.api.models.StatisticSendObject;
import io.casestory.sdk.stories.api.models.StatisticSession;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.StoryLinkObject;
import io.casestory.sdk.stories.api.models.callbacks.CloseStatisticCallback;
import io.casestory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import io.casestory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import io.casestory.sdk.stories.api.models.callbacks.OpenStatisticCallback;
import io.casestory.sdk.stories.api.networkclient.ApiClient;
import io.casestory.sdk.stories.api.networkclient.RetrofitCallback;
import io.casestory.sdk.stories.cache.Downloader;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ChangeStoryEvent;
import io.casestory.sdk.stories.events.ContentLoadedEvent;
import io.casestory.sdk.stories.events.ListVisibilityEvent;
import io.casestory.sdk.stories.events.LoadFavStories;
import io.casestory.sdk.stories.events.LoadNarrativesOnEmpty;
import io.casestory.sdk.stories.events.LoadNarrativesOnError;
import io.casestory.sdk.stories.events.NextStoryPageEvent;
import io.casestory.sdk.stories.events.NextStoryReaderEvent;
import io.casestory.sdk.stories.events.PauseStoryReaderEvent;
import io.casestory.sdk.stories.events.PrevStoryReaderEvent;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.StoriesErrorEvent;
import io.casestory.sdk.stories.events.StoryTapEvent;
import io.casestory.sdk.stories.serviceevents.ContentRenewPriorities;
import io.casestory.sdk.stories.serviceevents.DestroyStoriesFragmentEvent;
import io.casestory.sdk.stories.serviceevents.LikeDislikeEvent;
import io.casestory.sdk.stories.serviceevents.StoryFavoriteEvent;
import io.casestory.sdk.stories.storieslistenerevents.OnNextEvent;
import io.casestory.sdk.stories.ui.list.FavoriteImage;
import io.casestory.sdk.stories.utils.Sizes;
import okhttp3.ResponseBody;

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

    @Subscribe
    public void contentRenewPriorities(ContentRenewPriorities event) {
        Log.e("ContentRenewPriorities", "ContentRenewPriorities");

    }

    Handler timerHandler = new Handler();
    public long timerStart;
    public long timerDuration;
    public long pauseShift;

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - timerStart >= timerDuration) {
                timerHandler.removeCallbacks(timerTask);
                pauseShift = 0;
                EventBus.getDefault().post(new NextStoryPageEvent(currentId));
                return;
                //if (currentIndex == )
            }
            timerHandler.postDelayed(timerTask, 50);
        }
    };

    public void startTimer(long timerDuration) {
        if (timerDuration <= 0) return;
        pauseShift = 0;
        timerStart = System.currentTimeMillis();
        this.timerDuration = timerDuration;
        try {
            timerHandler.removeCallbacks(timerTask);
        } catch (Exception e) {

        }
        timerHandler.post(timerTask);
    }

    @Subscribe
    public void storyPageTapEvent(StoryTapEvent event) {
        if (event.getLink() != null && !event.getLink().isEmpty()) {
            StoryLinkObject object = new Gson().fromJson(event.getLink(), StoryLinkObject.class);
            if (object != null) {
                switch (object.getLink().getType()) {
                    case "url":
                        addLinkOpenStatistic();
                        if (CaseStoryManager.getInstance().getUrlClickCallback() != null) {
                            CaseStoryManager.getInstance().getUrlClickCallback().onUrlClick(
                                    object.getLink().getTarget()
                            );
                        } else {
                            if (!isConnected()) {
                                return;
                            }
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(object.getLink().getTarget()));
                            startActivity(i);
                        }
                        break;
                    default:
                        if (CaseStoryManager.getInstance().getAppClickCallback() != null) {
                            CaseStoryManager.getInstance().getAppClickCallback().onAppClick(
                                    object.getLink().getType(),
                                    object.getLink().getTarget()
                            );
                        }
                        break;
                }
            }
        }
    }

    public List<Story> favStories = new ArrayList<>();
    public List<FavoriteImage> favoriteImages = new ArrayList<>();

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
                        favStories.clear();
                        favStories.addAll(response2);
                        favoriteImages.clear();
                        EventBus.getDefault().post(new LoadFavStories());
                        if (response2 != null && response2.size() > 0) {
                            for (Story story : response2) {
                                if (favoriteImages.size() < 4)
                                    favoriteImages.add(new FavoriteImage(story.id, story.image, story.backgroundColor));
                            }
                            for (final FavoriteImage favoriteImage : favoriteImages) {
                                final int id = favoriteImage.getId();
                                Glide.with(getApplicationContext())
                                        .asBitmap()
                                        .load(favoriteImage.getImage().get(0).getUrl())
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                for (FavoriteImage image : favoriteImages) {
                                                    if (id == image.getId())
                                                        image.setBitmap(resource);
                                                }
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
    public void nextStoryEvent(NextStoryReaderEvent event) {
        lastTapEventTime = System.currentTimeMillis() + 100;
        cubeAnimation = true;
    }


    @Subscribe
    public void prevStoryEvent(PrevStoryReaderEvent event) {
        lastTapEventTime = System.currentTimeMillis() + 100;
        cubeAnimation = true;
    }

    public void closeStatisticEvent() {
        closeStatisticEvent(null, false);
    }

    public class StatisticEvent {
        public int eventType;
        public int storyId;
        public int index;
        public long timer;

        public StatisticEvent(int eventType, int storyId, int index) {
            this.eventType = eventType;
            this.storyId = storyId;
            this.index = index;
            this.timer = System.currentTimeMillis();
        }

        public StatisticEvent(int eventType, int storyId, int index, long timer) {
            this.eventType = eventType;
            this.storyId = storyId;
            this.index = index;
            this.timer = timer;
        }
    }

    public StatisticEvent currentEvent;

    private void addStatisticEvent(int eventType, int narrativeId, int index) {
        currentEvent = new StatisticEvent(eventType, narrativeId, index);
    }

    private void addArticleStatisticEvent(int eventType, int articleId) {
        currentEvent = new StatisticEvent(eventType, articleEventCount, articleId, articleTimer);
    }


    public int eventCount = 0;

    public void addStatisticBlock(int narrativeId, int index) {
        //if (currentEvent != null)
        //Log.e("closeStatistic", "addStatisticBlock");
        closeStatisticEvent();
        addStatisticEvent(1, narrativeId, index);
        eventCount++;
    }

    public int articleEventCount = 0;
    public long articleTimer = 0;

    public void addArticleOpenStatistic(int eventType, int articleId) {
        articleEventCount = eventCount;
        currentEvent.eventType = 2;
        closeStatisticEvent();
        eventCount++;
        articleTimer = System.currentTimeMillis();
        addArticleStatisticEvent(eventType, articleId);
    }

    public void addLinkOpenStatistic() {
        currentEvent.eventType = 2;
    }

    public void addDeeplinkClickStatistic(int id) {
        closeStatisticEvent();
        eventCount++;
        addStatisticEvent(1, id, 0);
        closeStatisticEvent(0, false);
        eventCount++;
        addStatisticEvent(2, id, 0);
        closeStatisticEvent(0, false);
    }

    public void addArticleCloseStatistic() {
        closeStatisticEvent();
        eventCount++;
        addStatisticEvent(1, currentId, currentIndex);
    }

    public void resumeTimer() {
        resumeLocalTimer();
        currentEvent.eventType = 1;
        currentEvent.timer = System.currentTimeMillis();
        pauseTime += System.currentTimeMillis() - startPauseTime;
        startPauseTime = 0;
        // Log.e("pauseTimer", Long.toString(pauseTime));
    }

    public void resumeLocalTimer() {
        startTimer(timerDuration - pauseShift);
    }



    public long startPauseTime;


    public long pauseTime = 0;

    public void pauseLocalTimer() {
        timerHandler.removeCallbacks(timerTask);
        pauseShift = (System.currentTimeMillis() - timerStart);
        //  Log.e("startPauseTime", Long.toString(startPauseTime));
    }

    public void pauseTimer() {
        pauseLocalTimer();
        startPauseTime = System.currentTimeMillis();
        closeStatisticEvent(null, true);
        sendStatistic();
        eventCount++;
        //  Log.e("startPauseTime", Long.toString(startPauseTime));
    }

    public void closeStatisticEvent(final Integer time, boolean clear) {
        if (currentEvent != null) {


            //if (isBackgroundPause)
            //    resumeTimer();
            statistic.add(new ArrayList<Object>() {{
                add(currentEvent.eventType);
                add(eventCount);
                add(currentEvent.storyId);
                add(currentEvent.index);
                add(Math.max(time != null ? time : System.currentTimeMillis() - currentEvent.timer, 0));
            }});
            //if (isBackgroundPause)
            //    pauseTimer();
            if (!clear)
                currentEvent = null;
        }
    }

    public boolean isBackgroundPause = false;


    @Subscribe
    public void destroyFragmentEvent(DestroyStoriesFragmentEvent event) {
        currentId = 0;
        currentIndex = 0;
        for (int i = 0; i < StoryDownloader.getInstance().getStories().size(); i++) {
            StoryDownloader.getInstance().getStories().get(i).lastIndex = 0;
        }
    }

    public void changeOuterIndex(int storyIndex) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        try {
            if (event.isWithBackground()) {
                isBackgroundPause = true;
                pauseTimer();
            } else {
                pauseLocalTimer();
            }
        } catch (Exception e) {

        }
    }

    boolean backPaused = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (event.isWithBackground()) {
            isBackgroundPause = false;
            resumeTimer();
        } else {
            resumeLocalTimer();
        }
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
        if (openProcess) return;
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
            openProcess = false;
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
                openProcess = false;
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
                openProcess = false;
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

    public void previewStatisticEvent(ArrayList<Integer> vals) {
        ArrayList<Object> sendObject = new ArrayList<Object>() {{
            add(5);
            add(eventCount);
        }};
        ArrayList<Integer> addedVals = new ArrayList<>();
        for (Integer val : vals) {
            if (!StatisticSession.getInstance().viewed.contains(val)) {
                sendObject.add(val);
                StatisticSession.getInstance().viewed.add(val);
            }
        }
        if (sendObject.size() > 2) {
            statistic.add(sendObject);
            eventCount++;
        }
    }

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

    interface CheckStatisticCallback {
        void openStatistic();
    }

    public static boolean checkOpenStatistic(final CheckStatisticCallback callback) {
        if (getInstance().isConnected()) {
            if (StatisticSession.getInstance() == null
                    || StatisticSession.getInstance().id == null
                    || StatisticSession.getInstance().id.isEmpty()) {
                getInstance().openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        callback.openStatistic();
                    }

                    @Override
                    public void onError() {
                    }
                });
                return false;
            } else if (StatisticSession.needToUpdate()) {
                getInstance().openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        callback.openStatistic();
                    }

                    @Override
                    public void onError() {
                    }
                });
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void getStoryById(final GetStoryByIdCallback storyByIdCallback, final int id) {
        for (Story story : StoryDownloader.getInstance().getStories()) {
            if (story.id == id) {
                storyByIdCallback.getStory(story);
                return;
            }
        }

        if (checkOpenStatistic(new CheckStatisticCallback() {
            @Override
            public void openStatistic() {
                getStoryById(storyByIdCallback, id);
            }
        })) {
            ApiClient.getApi().getStoryById(Integer.toString(id), StatisticSession.getInstance().id, 1,
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
    }

    public void likeDislikeClick(boolean isDislike, final int storyId) {
        final Story story = StoryDownloader.getInstance().findItemByStoryId(storyId);
        final int val;
        if (isDislike) {
            if (story.disliked()) {
                val = 0;
            } else {
                val = -1;
            }
        } else {
            if (story.liked()) {
                val = 0;
            } else {
                val = 1;
            }
        }
        ApiClient.getFastApi().storyLike(Integer.toString(storyId),
                StatisticSession.getInstance().id,
                getApiKey(), val).enqueue(
                new RetrofitCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        if (story != null)
                            story.like = val;
                        EventBus.getDefault().post(new LikeDislikeEvent(storyId, val));
                    }
                });
    }

    public void favoriteClick(final int storyId) {
        final Story story = StoryDownloader.getInstance().findItemByStoryId(storyId);
        final boolean val = story.favorite;
        ApiClient.getFastApi().storyFavorite(Integer.toString(storyId),
                StatisticSession.getInstance().id,
                getApiKey(), val ? 0 : 1).enqueue(
                new RetrofitCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        if (story != null)
                            story.favorite = !val;
                        EventBus.getDefault().post(new StoryFavoriteEvent(storyId, !val));
                    }
                });

    }

    public void getFullStoryById(final GetStoryByIdCallback storyByIdCallback, final int id) {
        for (Story story : StoryDownloader.getInstance().getStories()) {
            if (story.id == id && story.pages != null) {
                storyByIdCallback.getStory(story);
                return;
            }
        }
        if (checkOpenStatistic(new CheckStatisticCallback() {
            @Override
            public void openStatistic() {
                getFullStoryById(storyByIdCallback, id);
            }
        })) {
            ApiClient.getApi().getStoryById(Integer.toString(id), StatisticSession.getInstance().id, 1,
                    getApiKey(), EXPAND_STRING
            ).enqueue(new RetrofitCallback<Story>() {
                @Override
                public void onSuccess(final Story response) {
                    StoryDownloader.getInstance().uploadingAdditional(new ArrayList<Story>() {{
                        add(response);
                    }});
                    StoryDownloader.getInstance().setStory(response, response.id);
                    storyByIdCallback.getStory(response);
                }
            });
        }
    }

    public void getFullStoryByStringId(final GetStoryByIdCallback storyByIdCallback, final String id) {
        if (checkOpenStatistic(new CheckStatisticCallback() {
            @Override
            public void openStatistic() {
                getFullStoryByStringId(storyByIdCallback, id);
            }
        })) {
            ApiClient.getApi().getStoryById(id, StatisticSession.getInstance().id, 1,
                    getApiKey(), EXPAND_STRING
            ).enqueue(new RetrofitCallback<Story>() {
                @Override
                public void onSuccess(final Story response) {
                    StoryDownloader.getInstance().uploadingAdditional(new ArrayList<Story>() {{
                        add(response);
                    }});
                    StoryDownloader.getInstance().setStory(response, response.id);
                    storyByIdCallback.getStory(response);
                }
            });
        }
    }


    public static final String EXPAND_STRING = "slides_html,layout,slides_duration,src_list";

    private String getApiKey() {
        return CaseStoryManager.getInstance().getApiKey();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        statistic = new ArrayList<>();
        INSTANCE = this;
        /*if (Build.VERSION.SDK_INT >= 26) {
            final NotificationManager manager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Stories service");
            channel.setShowBadge(false);
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("123")
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setContentText("456")
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true).build();

            startForeground(NOTIFICATION_ID, notification);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    manager.cancel(NOTIFICATION_ID);
                }
            }, 200);
        }*/
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
        return START_STICKY;
    }

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
