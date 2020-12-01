package com.inappstory.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.CacheFontObject;
import com.inappstory.sdk.stories.api.models.StatisticResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryLinkObject;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenStatisticCallback;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.ContentLoadedEvent;
import com.inappstory.sdk.stories.events.ListVisibilityEvent;
import com.inappstory.sdk.stories.events.LoadFavStories;
import com.inappstory.sdk.stories.events.NextStoryPageEvent;
import com.inappstory.sdk.stories.events.NextStoryReaderEvent;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.PrevStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.events.StoryReaderTapEvent;
import com.inappstory.sdk.stories.outerevents.ClickOnButton;
import com.inappstory.sdk.stories.outerevents.DislikeStory;
import com.inappstory.sdk.stories.outerevents.FavoriteStory;
import com.inappstory.sdk.stories.outerevents.LikeStory;
import com.inappstory.sdk.stories.serviceevents.DestroyStoriesFragmentEvent;
import com.inappstory.sdk.stories.serviceevents.LikeDislikeEvent;
import com.inappstory.sdk.stories.serviceevents.StoryFavoriteEvent;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.utils.Sizes;

public class InAppStoryService extends Service {


    public static InAppStoryService getInstance() {
        return INSTANCE;
    }


    public static InAppStoryService INSTANCE;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void setUserId() {

    }

    void logout() {
        closeStatisticEvent(null, true);
        NetworkClient.getApi().statisticsClose(new StatisticSendObject(StatisticSession.getInstance().id,
                statistic)).enqueue(new NetworkCallback<StatisticResponse>() {
            @Override
            public void onSuccess(StatisticResponse response) {
            }

            @Override
            public Type getType() {
                return StatisticResponse.class;
            }

            @Override
            public void onError(int code, String message) {
            }
        });
        statistic.clear();
        statistic = null;
    }

    LoadStoriesCallback loadStoriesCallback;

    public void loadStories(final LoadStoriesCallback callback, final boolean isFavorite) {
        loadStoriesCallback = callback;
        if (isConnected()) {
            if (StatisticSession.getInstance() == null
                    || StatisticSession.getInstance().id == null
                    || StatisticSession.getInstance().id.isEmpty()) {
                openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        loadStories(loadStoriesCallback, isFavorite);
                    }

                    @Override
                    public void onError() {
                        CsEventBus.getDefault().post(new StoriesErrorEvent(NoConnectionEvent.LOAD_LIST));
                    }
                });
                return;
            } else if (StatisticSession.needToUpdate()) {
                openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        NetworkClient.getApi().getStories(StatisticSession.getInstance().id, getTestKey(), isFavorite ? 1 : 0,
                                getTags(), getTestKey(), null).enqueue(isFavorite ? loadCallbackWithoutFav : loadCallback);
                    }

                    @Override
                    public void onError() {
                        CsEventBus.getDefault().post(new StoriesErrorEvent(NoConnectionEvent.LOAD_LIST));
                    }
                });
            } else {
                NetworkClient.getApi().getStories(StatisticSession.getInstance().id, getTestKey(), isFavorite ? 1 : 0,
                        getTags(), getTestKey(), null).enqueue(isFavorite ? loadCallbackWithoutFav : loadCallback);
            }
        } else {
            CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.LOAD_LIST));
        }
    }

    private String getTags() {
        return InAppStoryManager.getInstance().getTagsString();
    }

    private String getTestKey() {
        return InAppStoryManager.getInstance().getTestKey();
    }


    Handler timerHandler = new Handler();
    public long timerStart;
    public long timerDuration;
    public long totalTimerDuration;
    public long pauseShift;

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - timerStart >= timerDuration) {
                timerHandler.removeCallbacks(timerTask);
                pauseShift = 0;
                CsEventBus.getDefault().post(new NextStoryPageEvent(currentId));
                return;
                //if (currentIndex == )
            }
            timerHandler.postDelayed(timerTask, 50);
        }
    };

    public void startTimer(long timerDuration, boolean clearDuration) {
        Log.e("startTimer", timerDuration + "");
        if (timerDuration == 0) {
            try {
                timerHandler.removeCallbacks(timerTask);
                this.timerDuration = timerDuration;
                if (clearDuration)
                    this.totalTimerDuration = timerDuration;
            } catch (Exception e) {

            }
            return;
        }
        if (timerDuration < 0) {
            return;
        }
        pauseShift = 0;
        timerStart = System.currentTimeMillis();
        this.timerDuration = timerDuration;
        try {
            timerHandler.removeCallbacks(timerTask);
        } catch (Exception e) {

        }
        timerHandler.post(timerTask);
    }

    public void restartTimer() {
        startTimer(totalTimerDuration, true);
    }

    @CsSubscribe
    public void storyPageTapEvent(StoryReaderTapEvent event) {
        if (event.getLink() != null && !event.getLink().isEmpty()) {
            StoryLinkObject object = JsonParser.fromJson(event.getLink(), StoryLinkObject.class);// new Gson().fromJson(event.getLink(), StoryLinkObject.class);
            if (object != null) {
                switch (object.getLink().getType()) {
                    case "url":
                        Story story = StoryDownloader.getInstance().getStoryById(currentId);
                        CsEventBus.getDefault().post(new ClickOnButton(story.id, story.title,
                                story.tags, story.slidesCount, story.lastIndex,
                                object.getLink().getTarget()));

                        addLinkOpenStatistic();
                        if (InAppStoryManager.getInstance().getUrlClickCallback() != null) {
                            InAppStoryManager.getInstance().getUrlClickCallback().onUrlClick(
                                    object.getLink().getTarget()
                            );
                        } else {
                            if (!isConnected()) {
                                return;
                            }
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setData(Uri.parse(object.getLink().getTarget()));
                            startActivity(i);
                        }
                        break;
                    default:
                        if (InAppStoryManager.getInstance().getAppClickCallback() != null) {
                            InAppStoryManager.getInstance().getAppClickCallback().onAppClick(
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

    NetworkCallback loadCallback = new NetworkCallback<List<Story>>() {

        @Override
        public Type getType() {
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

        boolean isRefreshing = false;

        @Override
        public void onError(int code, String message) {

            CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
            super.onError(code, message);
        }

        @Override
        protected void error424(String message) {
            CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
            if (!openProcess)
                openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isRefreshing)
                            isRefreshing = true;

                        NetworkClient.getApi().getStories(StatisticSession.getInstance().id, getTags(), getTestKey(),
                                null).enqueue(loadCallback);
                    }

                    @Override
                    public void onError() {

                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
                    }
                });
        }

        @Override
        public void onSuccess(final List<Story> response) {
            if (response == null || response.size() == 0) {
                CsEventBus.getDefault().post(new ContentLoadedEvent(true));
            } else {
                CsEventBus.getDefault().post(new ContentLoadedEvent(false));
            }
            StoryDownloader.getInstance().uploadingAdditional(response);
            CsEventBus.getDefault().post(new ListVisibilityEvent());
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
            if (InAppStoryManager.getInstance().hasFavorite) {
                NetworkClient.getApi().getStories(StatisticSession.getInstance().id, getTestKey(), 1,
                        null, "id, background_color, image",
                        null).enqueue(new NetworkCallback<List<Story>>() {
                    @Override
                    public void onSuccess(List<Story> response2) {
                        favStories.clear();
                        favStories.addAll(response2);
                        favoriteImages.clear();
                        CsEventBus.getDefault().post(new LoadFavStories());
                        if (response2 != null && response2.size() > 0) {
                            for (Story story : response2) {
                                //if (favoriteImages.size() < 4)
                                favoriteImages.add(new FavoriteImage(story.id, story.image, story.backgroundColor));
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
                    public Type getType() {
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


    NetworkCallback loadCallbackWithoutFav = new NetworkCallback<List<Story>>() {

        @Override
        public Type getType() {
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

        boolean isRefreshing = false;

        @Override
        public void onError(int code, String message) {

            CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
            super.onError(code, message);
        }

        @Override
        protected void error424(String message) {
            CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
            if (!openProcess)
                openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isRefreshing)
                            isRefreshing = true;

                        NetworkClient.getApi().getStories(StatisticSession.getInstance().id, getTags(), getTestKey(),
                                null).enqueue(loadCallbackWithoutFav);
                    }

                    @Override
                    public void onError() {

                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
                    }
                });
        }

        @Override
        public void onSuccess(final List<Story> response) {
            if (response == null || response.size() == 0) {
                CsEventBus.getDefault().post(new ContentLoadedEvent(true));
            } else {
                CsEventBus.getDefault().post(new ContentLoadedEvent(false));
            }
            StoryDownloader.getInstance().uploadingAdditional(response);
            CsEventBus.getDefault().post(new ListVisibilityEvent());
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
            if (loadStoriesCallback != null) {
                List<Integer> ids = new ArrayList<>();
                for (Story story : response) {
                    ids.add(story.id);
                }
                loadStoriesCallback.storiesLoaded(ids);
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

    @CsSubscribe
    public void nextStoryEvent(NextStoryReaderEvent event) {
        lastTapEventTime = System.currentTimeMillis() + 100;
        cubeAnimation = true;
    }


    @CsSubscribe
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

    private void addStatisticEvent(int eventType, int storyId, int index) {
        currentEvent = new StatisticEvent(eventType, storyId, index);
    }

    private void addArticleStatisticEvent(int eventType, int articleId) {
        currentEvent = new StatisticEvent(eventType, articleEventCount, articleId, articleTimer);
    }


    public int eventCount = 0;

    public void addStatisticBlock(int storyId, int index) {
        //if (currentEvent != null)
        closeStatisticEvent();
        addStatisticEvent(1, storyId, index);
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
        Log.e("startTimer", "resumeTimer");
        resumeLocalTimer();
        currentEvent.eventType = 1;
        currentEvent.timer = System.currentTimeMillis();
        pauseTime += System.currentTimeMillis() - startPauseTime;
        startPauseTime = 0;
    }

    public void resumeLocalTimer() {
        startTimer(timerDuration - pauseShift, false);
    }


    public long startPauseTime;


    public long pauseTime = 0;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryPageEvent(StoryPageOpenEvent event) {
        closeStatisticEvent(null, false);
        addStatisticEvent(1, event.storyId, event.index);
        eventCount++;
    }

    public void pauseLocalTimer() {
        timerHandler.removeCallbacks(timerTask);
        pauseShift = (System.currentTimeMillis() - timerStart);
    }

    public void pauseTimer() {
        pauseLocalTimer();
        startPauseTime = System.currentTimeMillis();
        closeStatisticEvent(null, true);
        sendStatistic();
        eventCount++;
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
            Log.e("statisticEvent", currentEvent.eventType + " " + eventCount + " " +
                    currentEvent.storyId + " " + currentEvent.index + " " +
                    Math.max(time != null ? time : System.currentTimeMillis() - currentEvent.timer, 0));
            //if (isBackgroundPause)
            //    pauseTimer();
            if (!clear)
                currentEvent = null;
        }
    }

    public boolean isBackgroundPause = false;


    @CsSubscribe
    public void destroyFragmentEvent(DestroyStoriesFragmentEvent event) {
        currentId = 0;
        currentIndex = 0;
        for (int i = 0; i < StoryDownloader.getInstance().getStories().size(); i++) {
            StoryDownloader.getInstance().getStories().get(i).lastIndex = 0;
        }
    }

    public void changeOuterIndex(int storyIndex) {

    }


    @CsSubscribe
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

    @CsSubscribe
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (event.isWithBackground()) {
            isBackgroundPause = false;
            resumeTimer();
        } else {
            resumeLocalTimer();
        }
    }


    public boolean isConnected() {
        if (InAppStoryManager.getInstance().context == null) return false;
        try {
            ConnectivityManager cm = (ConnectivityManager) InAppStoryManager.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            return true;
        }
    }


    public static Object openProcessLock = new Object();
    public static ArrayList<OpenStatisticCallback> callbacks = new ArrayList<>();

    public void openStatistic(final OpenStatisticCallback callback) {
        synchronized (openProcessLock) {
            if (openProcess) {
                callbacks.add(callback);
                return;
            }
        }
        synchronized (openProcessLock) {
            callbacks.clear();
            openProcess = true;
            callbacks.add(callback);
        }
        Context context = InAppStoryManager.getInstance().context;
        String platform = "android";
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
            synchronized (openProcessLock) {
                openProcess = false;
            }
            return;
        }
        NetworkClient.getApi().statisticsOpen(
                "cache",
                InAppStoryManager.getInstance().getTagsString(),
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
                InAppStoryManager.getInstance().getUserId()
        ).enqueue(new NetworkCallback<StatisticResponse>() {
            @Override
            public void onSuccess(final StatisticResponse response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        response.session.save();
                        synchronized (openProcessLock) {
                            openProcess = false;
                            for (OpenStatisticCallback localCallback : callbacks)
                                localCallback.onSuccess();
                            callbacks.clear();
                        }
                        //getInstance().share = response.share;

                        getInstance().handler.postDelayed(getInstance().statisticUpdateThread, statisticUpdateInterval);
                        if (response.cachedFonts != null) {
                            for (CacheFontObject cacheFontObject : response.cachedFonts) {
                                Downloader.downFontFile(InAppStoryManager.getInstance().context, cacheFontObject.url);
                            }
                        }
                    }
                });

            }

            @Override
            public Type getType() {
                return StatisticResponse.class;
            }

            @Override
            public void onError(int code, String message) {
                synchronized (openProcessLock) {
                    openProcess = false;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (OpenStatisticCallback localCallback : callbacks)
                                localCallback.onError();
                            callbacks.clear();
                        }
                    });
                }
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.OPEN_SESSION));
                super.onError(code, message);

            }

            @Override
            public void onTimeout() {
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.OPEN_SESSION));
                synchronized (openProcessLock) {
                    openProcess = false;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (OpenStatisticCallback localCallback : callbacks)
                                localCallback.onError();
                            callbacks.clear();
                        }
                    });
                }

            }
        });
    }


    private static final long statisticUpdateInterval = 30000;

    private Handler handler = new Handler();

    List<List<Object>> statistic = new ArrayList<>();

    public Runnable statisticUpdateThread = new Runnable() {
        @Override
        public void run() {
            if (InAppStoryManager.getInstance().context == null || getInstance() == null) {
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
            try {
                Log.e("stat", JsonParser.getJson(sendObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            NetworkClient.getApi().statisticsUpdate(
                    new StatisticSendObject(StatisticSession.getInstance().id,
                            statistic)).enqueue(new NetworkCallback<StatisticResponse>() {
                @Override
                public void onSuccess(StatisticResponse response) {
                    StatisticSession.getInstance();
                    StatisticSession.updateStatistic();
                    if (statistic == null) return;
                    statistic.clear();
                }

                @Override
                public Type getType() {
                    return StatisticResponse.class;
                }
            });
        } catch (Exception e) {
        }
        return true;
    }


    public static boolean openProcess = false;

    interface CheckStatisticCallback {
        void openStatistic();

        void errorStatistic();
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
                        callback.errorStatistic();
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
                        callback.errorStatistic();
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
    }

    public interface LikeDislikeCallback {
        void onSuccess();
        void onError();
    }

    public void likeDislikeClick(boolean isDislike, final int storyId,
                                 final LikeDislikeCallback callback) {
        final Story story = StoryDownloader.getInstance().findItemByStoryId(storyId);
        final int val;
        if (isDislike) {
            if (story.disliked()) {
                CsEventBus.getDefault().post(new DislikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, false));
                val = 0;
            } else {
                CsEventBus.getDefault().post(new DislikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, true));
                val = -1;
            }
        } else {
            if (story.liked()) {
                CsEventBus.getDefault().post(new LikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, false));
                val = 0;
            } else {
                CsEventBus.getDefault().post(new LikeStory(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex, true));
                val = 1;
            }
        }


        NetworkClient.getApi().storyLike(Integer.toString(storyId),
                StatisticSession.getInstance().id,
                getApiKey(), val).enqueue(
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        if (story != null)
                            story.like = val;
                        callback.onSuccess();
                        CsEventBus.getDefault().post(new LikeDislikeEvent(storyId, val));
                    }


                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        callback.onError();
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    public void favoriteClick(final int storyId, final LikeDislikeCallback callback) {
        final Story story = StoryDownloader.getInstance().findItemByStoryId(storyId);
        final boolean val = story.favorite;

        CsEventBus.getDefault().post(new FavoriteStory(story.id, story.title,
                story.tags, story.slidesCount, story.lastIndex, story.favorite));
        NetworkClient.getApi().storyFavorite(Integer.toString(storyId),
                StatisticSession.getInstance().id,
                getApiKey(), val ? 0 : 1).enqueue(
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        if (story != null)
                            story.favorite = !val;
                        callback.onSuccess();
                        CsEventBus.getDefault().post(new StoryFavoriteEvent(storyId, !val));
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        callback.onError();
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });

    }

    public void getFullStoryById(final GetStoryByIdCallback storyByIdCallback, final int id) {
        for (Story story : StoryDownloader.getInstance().getStories()) {
            if (story.id == id) {
                if (story.pages != null) {
                    storyByIdCallback.getStory(story);
                    return;
                } else {
                    storyByIdCallback.getStory(story);
                    return;
                }
            }
        }
    }

    public void getFullStoryByStringId(final GetStoryByIdCallback storyByIdCallback, final String id) {
        if (checkOpenStatistic(new CheckStatisticCallback() {
            @Override
            public void openStatistic() {
                getFullStoryByStringId(storyByIdCallback, id);
            }

            @Override
            public void errorStatistic() {

                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_SINGLE));
            }
        })) {
            NetworkClient.getApi().getStoryById(id, StatisticSession.getInstance().id, 1,
                    getApiKey(), EXPAND_STRING
            ).enqueue(new NetworkCallback<Story>() {
                @Override
                public void onSuccess(final Story response) {
                    if (InAppStoryManager.getInstance().singleLoadedListener != null) {
                        InAppStoryManager.getInstance().singleLoadedListener.onLoad();
                    }
                    StoryDownloader.getInstance().uploadingAdditional(new ArrayList<Story>() {{
                        add(response);
                    }});
                    StoryDownloader.getInstance().setStory(response, response.id);
                    storyByIdCallback.getStory(response);
                }

                @Override
                public Type getType() {
                    return Story.class;
                }

                @Override
                public void onError(int code, String message) {
                    if (InAppStoryManager.getInstance().singleLoadedListener != null) {
                        InAppStoryManager.getInstance().singleLoadedListener.onError();
                    }
                    CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_SINGLE));
                }
            });
        }
    }


    public static final String EXPAND_STRING = "slides_html,layout,slides_duration,src_list";

    private String getApiKey() {
        return InAppStoryManager.getInstance().getApiKey();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CsEventBus.getDefault().register(this);

        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
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


    public static final String CHANNEL_ID = "inAppStorySdk";
    private static final String CHANNEL_NAME = "inAppStorySdk";
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
