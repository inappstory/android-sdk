package com.inappstory.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StatisticResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.events.NextStoryPageEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.serviceevents.DestroyStoriesFragmentEvent;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.utils.SessionManager;

public class InAppStoryService extends Service {

    public static InAppStoryService getInstance() {
        return INSTANCE;
    }

    public StoryDownloadManager getDownloadManager() {
        return downloadManager;
    }

    StoryDownloadManager downloadManager;
    public static InAppStoryService INSTANCE;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void logout() {
        Log.d(IAS_LOG, "logout service");
        OldStatisticManager.getInstance().closeStatisticEvent(null, true);
        SessionManager.getInstance().closeSession(true, false);
        OldStatisticManager.getInstance().statistic.clear();
        OldStatisticManager.getInstance().statistic = null;
    }





    public List<FavoriteImage> getFavoriteImages() {
        if (downloadManager == null) return new ArrayList<>();
        if (downloadManager.favoriteImages == null) downloadManager.favoriteImages = new ArrayList<>();
        return downloadManager.favoriteImages;
    }


    private int currentId;

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    private int currentIndex;

    public static final String IAS_LOG = "IAS_LOG";

    @Override
    public void onDestroy() {
        Log.d(IAS_LOG, "destroy service");
        super.onDestroy();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryPageEvent(StoryPageOpenEvent event) {
        OldStatisticManager.getInstance().addStatisticBlock(event.storyId, event.index);
        StatisticManager.getInstance().createCurrentState(event.storyId, event.index);
    }




    public boolean isBackgroundPause = false;

    @CsSubscribe
    public void destroyFragmentEvent(DestroyStoriesFragmentEvent event) {
        currentId = 0;
        currentIndex = 0;
        for (int i = 0; i < InAppStoryService.getInstance().getDownloadManager().getStories().size(); i++) {
            InAppStoryService.getInstance().getDownloadManager().getStories().get(i).lastIndex = 0;
        }
    }

    @CsSubscribe
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        if (timerManager == null) timerManager = new TimerManager();
        try {
            if (event.isWithBackground()) {
                isBackgroundPause = true;
                timerManager.pauseTimer();
            } else {
                timerManager.pauseLocalTimer();
            }
        } catch (Exception e) {

        }
    }

    boolean backPaused = false;

    @CsSubscribe
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (timerManager == null) timerManager = new TimerManager();
        if (event.isWithBackground()) {
            isBackgroundPause = false;
            timerManager.resumeTimer();
        } else {
            timerManager.resumeLocalTimer();
        }
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    TimerManager timerManager;

    public static boolean isConnected() {
        if (InAppStoryManager.getInstance() == null) return false;
        if (InAppStoryManager.getInstance().context == null) return false;
        try {
            ConnectivityManager cm = (ConnectivityManager) InAppStoryManager.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            return true;
        }
    }

    public void saveSessionPlaceholders(List<StoryPlaceholder> placeholders) {
        if (placeholders == null) return;
        for (StoryPlaceholder placeholder : placeholders) {
            String key = "%" + placeholder.name + "%";
            InAppStoryManager.getInstance().defaultPlaceholders.put(key,
                    placeholder.defaultVal);
            if (!InAppStoryManager.getInstance().placeholders.containsKey(key)) {
                InAppStoryManager.getInstance().placeholders.put(key,
                        placeholder.defaultVal);
            }
        }
    }


    public void runStatisticThread() {
        if (handler != null)
            handler.postDelayed(OldStatisticManager.getInstance().statisticUpdateThread, statisticUpdateInterval);
    }

    private static final long statisticUpdateInterval = 30000;

    private Handler handler = new Handler();

    private String getApiKey() {
        return InAppStoryManager.getInstance().getApiKey();
    }


    private static final String CRASH_KEY = "CRASH_KEY";

    public class TryMe implements Thread.UncaughtExceptionHandler {

        Thread.UncaughtExceptionHandler oldHandler;

        public TryMe() {
            oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable throwable) {
            SharedPreferencesAPI.saveString(CRASH_KEY, throwable.getCause().toString() + "\n" + throwable.getMessage());
            System.exit(0);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(IAS_LOG, "create service");
        CsEventBus.getDefault().register(this);
        Thread.setDefaultUncaughtExceptionHandler(new TryMe());
        new ImageLoader(getApplicationContext());
        OldStatisticManager.getInstance().statistic = new ArrayList<>();
        downloadManager = new StoryDownloadManager(getApplicationContext());
        timerManager = new TimerManager();
        INSTANCE = this;
        Log.d(IAS_LOG, "service created");
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        INSTANCE = this;
        Log.d(IAS_LOG, "service onStart");
    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        INSTANCE = this;
        Log.d(IAS_LOG, "service onStartCommand");
        return START_STICKY;
    }

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
