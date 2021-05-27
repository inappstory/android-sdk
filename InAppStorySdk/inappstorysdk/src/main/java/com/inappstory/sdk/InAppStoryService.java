package com.inappstory.sdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.serviceevents.DestroyStoriesFragmentEvent;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.utils.SessionManager;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;

public class InAppStoryService {

    public static InAppStoryService getInstance() {
        synchronized (lock) {
            return INSTANCE;
        }
    }

    public StoryDownloadManager getDownloadManager() {
        return downloadManager;
    }

    StoryDownloadManager downloadManager;
    public static InAppStoryService INSTANCE;

    void logout() {
        OldStatisticManager.getInstance().closeStatisticEvent(null, true);
        SessionManager.getInstance().closeSession(true, false);
        OldStatisticManager.getInstance().statistic.clear();
        OldStatisticManager.getInstance().statistic = null;
    }


    public List<FavoriteImage> getFavoriteImages() {
        if (downloadManager == null) return new ArrayList<>();
        if (downloadManager.favoriteImages == null)
            downloadManager.favoriteImages = new ArrayList<>();
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


    public void onDestroy() {
        getDownloadManager().destroy();
        if (INSTANCE == this)
            INSTANCE = null;
    }

    @CsSubscribe//(threadMode = CsThreadMode.MAIN)
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

    private LruDiskCache fastCache; //use for covers
    private LruDiskCache commonCache; //use for slides, games, etc.

    private Object cacheLock = new Object();

    public static final String IAS_PREFIX = File.separator + "ias" + File.separator;
    Context context;

    public LruDiskCache getFastCache() {
        synchronized (cacheLock) {
            if (fastCache == null) {
                try {
                    fastCache = LruDiskCache.create(new File(
                                    context.getCacheDir() +
                                            IAS_PREFIX + "fastCache"),
                            MB_10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fastCache;
        }
    }

    public LruDiskCache getCommonCache() {
        synchronized (cacheLock) {
            if (commonCache == null) {
                try {
                    commonCache = LruDiskCache.create(new File(
                                    context.getCacheDir() +
                                            IAS_PREFIX + "commonCache"),
                            LruDiskCache.MB_100);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return commonCache;
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

    public static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        Thread.UncaughtExceptionHandler oldHandler;

        public DefaultExceptionHandler() {
            oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable throwable) {
            Log.e("InAppStoryException", throwable.getCause() + "\n"
                    + throwable.getMessage());

            if (InAppStoryManager.getInstance() != null) {

                InAppStoryManager.getInstance().setExceptionCache(new ExceptionCache(
                        getInstance().getDownloadManager().getStories(),
                        getInstance().getDownloadManager().favStories,
                        getInstance().getDownloadManager().favoriteImages
                ));

            }
            synchronized (lock) {
                if (getInstance() != null)
                    getInstance().onDestroy();
            }
            if (InAppStoryManager.getInstance() != null) {
                InAppStoryManager.getInstance().createServiceThread(InAppStoryManager.getInstance().context);
                if (InAppStoryManager.getInstance().getExceptionCallback() != null) {
                    InAppStoryManager.getInstance().getExceptionCallback().onException(throwable);
                }
            }
        }
    }

    Runnable checkFreeSpace = new Runnable() {
        @Override
        public void run() {
            long freeSpace = getCommonCache().getCacheDir().getFreeSpace();
            if (freeSpace < getCommonCache().getCacheSize() + getFastCache().getCacheSize() + MB_10) {
                getCommonCache().setCacheSize(MB_50);
                if (freeSpace < getCommonCache().getCacheSize() + getFastCache().getCacheSize() + MB_10) {
                    getCommonCache().setCacheSize(MB_10);
                    getFastCache().setCacheSize(MB_5);
                    if (freeSpace < getCommonCache().getCacheSize() + getFastCache().getCacheSize() + MB_10) {
                        getCommonCache().setCacheSize(MB_10);
                        getFastCache().setCacheSize(MB_5);
                    }
                }
            }
            spaceHandler.postDelayed(checkFreeSpace, 60000);
        }
    };

    Handler spaceHandler;

    private void clearOldFiles() {
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "Stories"));
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "temp"));
    }

    public void createDownloadManager(ExceptionCache cache) {
        if (downloadManager == null)
            downloadManager = new StoryDownloadManager(context, cache);
    }

    public void onCreate(Context context, ExceptionCache exceptionCache) {
        this.context = context;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                clearOldFiles();
            }
        });
        CsEventBus.getDefault().register(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
        new ImageLoader(context);
        OldStatisticManager.getInstance().statistic = new ArrayList<>();
        createDownloadManager(exceptionCache);
        timerManager = new TimerManager();
        spaceHandler = new Handler();
        synchronized (lock) {
            INSTANCE = this;
        }
        spaceHandler.postDelayed(checkFreeSpace, 60000);
    }

    private static Object lock = new Object();

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
