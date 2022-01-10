package com.inappstory.sdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.exceptions.DataException;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.events.DebugEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.serviceevents.DestroyStoriesFragmentEvent;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.utils.SessionManager;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;

public class InAppStoryService {

    public static InAppStoryService getInstance() {
        synchronized (lock) {
            if (InAppStoryManager.getInstance() == null) return null;
            return INSTANCE;
        }
    }

    public static boolean isNotNull() {
        return getInstance() != null;
    }

    public static boolean isNull() {
        return getInstance() == null;
    }

    public InAppStoryService() {

    }

    public String getUserId() {
        if (userId == null && !InAppStoryManager.isNull())
            return InAppStoryManager.getInstance().getUserId();
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public String getTagsString() {
        if (InAppStoryManager.getInstance() != null) {
            return InAppStoryManager.getInstance().getTagsString();
        } else {
            return null;
        }
    }

    public void saveStoriesOpened(List<Story> stories) {

        Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
        if (opens == null) opens = new HashSet<>();
        for (Story story : stories) {
            if (story.isOpened) {
                opens.add(Integer.toString(story.id));
            } else if (opens.contains(Integer.toString(story.id))) {
                story.isOpened = true;
            }
        }
        SharedPreferencesAPI.saveStringSet(InAppStoryManager.getInstance().getLocalOpensKey(), opens);
    }

    public void saveStoryOpened(int id) {
        if (InAppStoryManager.getInstance() == null) return;
        Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
        if (opens == null) opens = new HashSet<>();
        opens.add(Integer.toString(id));
        SharedPreferencesAPI.saveStringSet(InAppStoryManager.getInstance().getLocalOpensKey(), opens);
    }

    public boolean isSoundOn() {
        if (InAppStoryManager.getInstance() != null) {
            return InAppStoryManager.getInstance().soundOn;
        } else {
            return true;
        }
    }

    public void changeSoundStatus() {
        if (InAppStoryManager.getInstance() != null) {
            InAppStoryManager.getInstance().soundOn = !InAppStoryManager.getInstance().soundOn;
        }
    }


    public boolean getSendStatistic() {
        if (InAppStoryManager.getInstance() != null) {
            return InAppStoryManager.getInstance().sendStatistic;
        } else {
            return true;
        }
    }

    public InAppStoryService(String userId) {
        this.userId = userId;
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
        try {
            CsEventBus.getDefault().unregister(this);
            handler.removeCallbacks(OldStatisticManager.getInstance().statisticUpdateThread);
        } catch (Exception e) {

        }
        if (timerManager != null) timerManager.destroy();
        timerManager = null;
        OldStatisticManager.getInstance().destroy();
        getDownloadManager().destroy();
        if (INSTANCE == this)
            INSTANCE = null;
    }


    public void sendPageOpenStatistic(int storyId, int index) {
        OldStatisticManager.getInstance().addStatisticBlock(storyId, index);
        StatisticManager.getInstance().createCurrentState(storyId, index);
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

    public Context getContext() {
        return context;
    }

    private Context context;

    public LruDiskCache getFastCache() {
        synchronized (cacheLock) {
            if (fastCache == null) {
                try {
                    fastCache = LruDiskCache.create(
                            context.getCacheDir(),
                            IAS_PREFIX + "fastCache",
                            MB_10, true);
                } catch (IOException e) {
                    final Throwable e2 = e;
                    if (handler != null) handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            throw new RuntimeException(e2);

                        }
                    }, 500);
                }
            }
            return fastCache;
        }
    }

    public LruDiskCache getCommonCache() {
        synchronized (cacheLock) {
            if (commonCache == null) {
                try {
                    long cacheType = MB_100;
                    long fastCacheType = MB_10;
                    long freeSpace = context.getCacheDir().getFreeSpace();
                    if (freeSpace < cacheType + fastCacheType + MB_10) {
                        cacheType = MB_50;
                        if (freeSpace < cacheType + fastCacheType + MB_10) {
                            cacheType = MB_10;
                            fastCacheType = MB_5;
                            if (freeSpace < cacheType + fastCacheType + MB_10) {
                                cacheType = 0;
                            }
                        }
                    }
                    if (cacheType > 0) {
                        commonCache = LruDiskCache.create(
                                context.getCacheDir(),
                                IAS_PREFIX + "commonCache",
                                cacheType, false);
                    }
                } catch (IOException e) {
                    final Throwable e2 = e;
                    if (handler != null) handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            throw new RuntimeException(e2);
                        }
                    }, 500);
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
        if (getInstance() == null || getInstance().getContext() == null) return false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            return true;
        }
    }

    public Map<String, String> getPlaceholders() {
        if (InAppStoryManager.getInstance() != null)
            return InAppStoryManager.getInstance().getPlaceholders();
        return new HashMap<>();
    }

    public void saveSessionPlaceholders(List<StoryPlaceholder> placeholders) {
        if (placeholders == null) return;
        if (InAppStoryManager.getInstance() == null) return;
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
        if (handler != null) {
            try {
                handler.removeCallbacks(OldStatisticManager.getInstance().statisticUpdateThread);
            } finally {
                OldStatisticManager.getInstance().destroy();
                handler.postDelayed(OldStatisticManager.getInstance().statisticUpdateThread,
                        statisticUpdateInterval);
            }
        }
    }

    private static final long statisticUpdateInterval = 30000;

    private Handler handler = new Handler();

    public static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        Thread.UncaughtExceptionHandler oldHandler;

        public DefaultExceptionHandler() {
            oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable throwable) {
            if (oldHandler != null) oldHandler.uncaughtException(thread, throwable);
            Log.d("InAppStoryException", throwable.getCause() + "\n"
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
                InAppStoryManager.getInstance().createServiceThread(
                        InAppStoryManager.getInstance().context,
                        InAppStoryManager.getInstance().getUserId());
                if (InAppStoryManager.getInstance().getExceptionCallback() != null) {
                    InAppStoryManager.getInstance().getExceptionCallback().onException(throwable);
                }
            }
        }
    }

    Runnable checkFreeSpace = new Runnable() {
        @Override
        public void run() {
            if (getCommonCache() != null) {
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
