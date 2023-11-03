package com.inappstory.sdk;

import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.core.lrudiskcache.LruDiskCache.MB_50;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.repository.stories.dto.IFavoritePreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.StoryDTO;
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.core.lrudiskcache.CacheType;
import com.inappstory.sdk.core.lrudiskcache.FileManager;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.models.Response;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.FavoriteCallback;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IAllStoriesListsNotify;
import com.inappstory.sdk.stories.uidomain.list.listnotify.IStoriesListNotify;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    public HashMap<String, List<IPreviewStoryDTO>> cachedListStories = new HashMap<>();

    public boolean genException = false;


    public void generateException() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("test exception");
            }
        }).start();
    }

    Handler exHandler = new Handler();

    public Runnable exHandlerThread = new Runnable() {
        @Override
        public void run() {
            if (genException) {
                genException = false;
                generateException();
            }
            exHandler.postDelayed(exHandlerThread, 3000);
        }
    };

    public void saveStoriesOpened(List<Story> stories, Story.StoryType type) {
        Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey(type));
        if (opens == null) opens = new HashSet<>();
        for (Story story : stories) {
            if (story.isOpened) {
                opens.add(Integer.toString(story.id));
            } else if (opens.contains(Integer.toString(story.id))) {
                story.isOpened = true;
            }
        }
        SharedPreferencesAPI.saveStringSet(InAppStoryManager.getInstance().getLocalOpensKey(type), opens);
    }

    public void saveStoryOpened(int id, Story.StoryType type) {
        if (InAppStoryManager.getInstance() == null) return;
        Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey(type));
        if (opens == null) opens = new HashSet<>();
        opens.add(Integer.toString(id));
        SharedPreferencesAPI.saveStringSet(InAppStoryManager.getInstance().getLocalOpensKey(type), opens);
    }

    public boolean isSoundOn() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null)
            return true;
        else
            return inAppStoryManager.soundOn();
    }

    public void changeSoundStatus() {
        if (InAppStoryManager.getInstance() != null) {
            InAppStoryManager.getInstance().soundOn(!InAppStoryManager.getInstance().soundOn());
        }
    }


    public boolean getSendNewStatistic() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return false;
        return inAppStoryManager.isSendStatistic()
                && IASCoreManager.getInstance().sessionRepository.isAllowStatV2();
    }

    public boolean getSendStatistic() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return false;
        return inAppStoryManager.isSendStatistic()
                && IASCoreManager.getInstance().sessionRepository.isAllowStatV1();
    }

    GameCacheManager gameCacheManager = new GameCacheManager();

    public GameCacheManager gameCacheManager() {
        if (gameCacheManager == null) {
            gameCacheManager = new GameCacheManager();
        }
        return gameCacheManager;
    }

    public static InAppStoryService INSTANCE;

    public void clearGames() {
        gameCacheManager().clearGames();
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
        spaceHandler.removeCallbacksAndMessages(null);
        if (INSTANCE == this)
            INSTANCE = null;
    }


    public void sendPageOpenStatistic(int storyId, int index, String feedId) {
        OldStatisticManager.getInstance().addStatisticBlock(storyId, index);
        StatisticManager.getInstance().createCurrentState(storyId, index, feedId);
    }


    private LruDiskCache fastCache; //use for covers
    private LruDiskCache commonCache; //use for slides, etc.

    private LruDiskCache infiniteCache; //use for games, etc.

    private final Object cacheLock = new Object();

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
                            IAS_PREFIX,
                            MB_10, CacheType.FAST
                    );
                } catch (IOException e) {
                    InAppStoryService.createExceptionLog(e);
                }
            }
            return fastCache;
        }
    }

    public LruDiskCache getInfiniteCache() {
        synchronized (cacheLock) {
            if (infiniteCache == null) {
                try {
                    long cacheType = context.getCacheDir().getFreeSpace();
                    if (cacheType > 0) {
                        infiniteCache = LruDiskCache.create(
                                context.getFilesDir(),
                                IAS_PREFIX,
                                cacheType,
                                CacheType.INFINITE
                        );
                    }
                } catch (IOException e) {
                    InAppStoryService.createExceptionLog(e);
                }
            }
            return infiniteCache;
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
                                IAS_PREFIX,
                                cacheType, CacheType.COMMON
                        );
                    }
                } catch (IOException e) {
                    InAppStoryService.createExceptionLog(e);
                }
            }
            return commonCache;
        }
    }

    boolean backPaused = false;


    public TimerManager getTimerManager() {
        return timerManager;
    }

    TimerManager timerManager;

    public static boolean isConnected() {
        InAppStoryService service = getInstance();
        if (service == null) return false;
        Context ctx = service.getContext();
        if (ctx == null) return false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network nw = connectivityManager.getActiveNetwork();
                if (nw == null) return false;
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                return actNw != null && (
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
            } else {
                NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
                return nwInfo != null && nwInfo.isConnected();
            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            return true;
        }
    }


    public Map<String, String> getPlaceholders() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null)
            return manager.getPlaceholdersCopy();
        return new HashMap<>();
    }


    public Map<String, ImagePlaceholderValue> getImagePlaceholdersValues() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null)
            return manager.getImagePlaceholdersValues();
        return new HashMap<>();
    }

    public Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> getImagePlaceholdersValuesWithDefaults() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null)
            return manager.getImagePlaceholdersValuesWithDefaults();
        return new HashMap<>();
    }

    public void saveSessionPlaceholders(List<StoryPlaceholder> placeholders) {
        if (placeholders == null) return;
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return;
        manager.setDefaultPlaceholders(placeholders);
    }

    public void saveSessionImagePlaceholders(List<StoryPlaceholder> placeholders) {
        if (placeholders == null) return;
        if (InAppStoryManager.getInstance() == null) return;
        for (StoryPlaceholder placeholder : placeholders) {
            if (!URLUtil.isNetworkUrl(placeholder.defaultVal))
                continue;
            String key = placeholder.name;
            ImagePlaceholderValue defaultVal = ImagePlaceholderValue.createByUrl(placeholder.defaultVal);
            InAppStoryManager.getInstance().setDefaultImagePlaceholder(key,
                    defaultVal);
        }
    }

    public void openGameReaderWithGC(
            Context context,
            GameStoryData data,
            String gameId
    ) {
        ScreensManager.getInstance().openGameReader(
                context,
                data,
                gameId,
                null,
                null,
                null,
                null,
                null
        );
    }

    public void runStatisticThread() {
        OldStatisticManager.getInstance().refreshCallbacks();
    }

    private ListNotifier listNotifier = new ListNotifier();

    public ListNotifier getListNotifier() {
        if (listNotifier == null) listNotifier = new ListNotifier();
        return listNotifier;
    }

    public class ListNotifier {
        public void changeStory(int storyId, Story.StoryType type, String listID) {
            for (IStoriesListNotify sub : getStoriesListNotifySet()) {
                if (listID.equals(sub.getListUID()))
                    sub.changeStory(storyId, type);
            }
        }

        public void openStory(int storyId, Story.StoryType type) {
            IASCoreManager.getInstance().getStoriesRepository(type).openStory(storyId);
        }

        public void closeReader(String listID) {
            for (IStoriesListNotify sub : getStoriesListNotifySet()) {
                if (listID.equals(sub.getListUID()))
                    sub.closeReader();
            }
        }

        public void openReader(String listID) {
            for (IStoriesListNotify sub : getStoriesListNotifySet()) {
                if (listID.equals(sub.getListUID()))
                    sub.openReader();
            }
        }

        public void changeUserId() {
            for (IAllStoriesListsNotify sub : getAllStoriesListsNotifySet()) {
                sub.changeUserId();
            }
        }
    }

    Set<IStoriesListNotify> storiesListNotifySet;
    Set<IAllStoriesListsNotify> allStoriesListsNotifySet;
    public static Set<IStoriesListNotify> tempStoriesListNotifySet;
    public static Set<IAllStoriesListsNotify> tempAllStoriesListsNotifySet;

    public Set<IStoriesListNotify> getStoriesListNotifySet() {
        if (storiesListNotifySet == null) storiesListNotifySet = new HashSet<>();
        return storiesListNotifySet;
    }

    public Set<IAllStoriesListsNotify> getAllStoriesListsNotifySet() {
        if (allStoriesListsNotifySet == null) allStoriesListsNotifySet = new HashSet<>();
        return allStoriesListsNotifySet;
    }

    public static void checkAndAddStoriesListNotify(IStoriesListNotify storiesListNotify) {
        InAppStoryService service = getInstance();
        if (service != null) {
            service.addStoriesListNotify(storiesListNotify);
        } else {
            if (tempStoriesListNotifySet == null) tempStoriesListNotifySet = new HashSet<>();
            tempStoriesListNotifySet.add(storiesListNotify);
        }
    }

    public static void checkAndAddAllStoriesListsNotify(IAllStoriesListsNotify storiesListNotify) {
        InAppStoryService service = getInstance();
        if (service != null) {
            service.addAllStoriesListsNotify(storiesListNotify);
        } else {
            if (tempAllStoriesListsNotifySet == null)
                tempAllStoriesListsNotifySet = new HashSet<>();
            tempAllStoriesListsNotifySet.add(storiesListNotify);
        }
    }

    public void addStoriesListNotify(IStoriesListNotify storiesListNotify) {
        if (storiesListNotifySet == null) storiesListNotifySet = new HashSet<>();
        storiesListNotifySet.add(storiesListNotify);
    }


    public void addAllStoriesListsNotify(IAllStoriesListsNotify storiesListNotify) {
        if (allStoriesListsNotifySet == null) allStoriesListsNotifySet = new HashSet<>();
        allStoriesListsNotifySet.add(storiesListNotify);
    }


    public void clearSubscribers() {
        for (IStoriesListNotify storiesListNotify : storiesListNotifySet) {
            storiesListNotify.unsubscribe();
        }
        for (IAllStoriesListsNotify allStoriesListsNotify : allStoriesListsNotifySet) {
            allStoriesListsNotify.unsubscribe();
        }
        tempStoriesListNotifySet.clear();
        storiesListNotifySet.clear();
        tempAllStoriesListsNotifySet.clear();
        allStoriesListsNotifySet.clear();
    }


    public void removeStoriesListNotify(IStoriesListNotify storiesListNotify) {
        if (storiesListNotifySet == null) return;
        if (tempStoriesListNotifySet != null)
            tempStoriesListNotifySet.remove(storiesListNotify);
        storiesListNotifySet.remove(storiesListNotify);
    }

    public void removeAllStoriesListsNotify(IAllStoriesListsNotify storiesListNotify) {
        if (allStoriesListsNotifySet != null)
            allStoriesListsNotifySet.remove(storiesListNotify);
        if (tempAllStoriesListsNotifySet != null)
            tempAllStoriesListsNotifySet.remove(storiesListNotify);
    }

    public static void createExceptionLog(Throwable throwable) {
        ExceptionManager em = new ExceptionManager();
        ExceptionLog el = em.generateExceptionLog(throwable);
        em.saveException(el);
        em.sendException(el);
    }

    public static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        Thread.UncaughtExceptionHandler oldHandler;

        public DefaultExceptionHandler() {
            oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable throwable) {
            createExceptionLog(throwable);
            Log.d("InAppStory_SDK_error", throwable.getCause() + "\n"
                    + throwable.getMessage());

            if (InAppStoryManager.getInstance() != null) {
                if (thread != InAppStoryManager.getInstance().serviceThread) {
                    if (oldHandler != null)
                        oldHandler.uncaughtException(thread, throwable);
                    return;
                }

            }
            try {
                synchronized (lock) {
                    if (getInstance() != null)
                        getInstance().onDestroy();
                }
                if (InAppStoryManager.getInstance() != null) {
                    if (InAppStoryManager.getInstance().getExceptionCallback() != null) {
                        InAppStoryManager.getInstance().getExceptionCallback().onException(throwable);
                    }
                }
            } catch (Exception ignored) {

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (oldHandler != null)
                oldHandler.uncaughtException(thread, throwable);
        }
    }

    Runnable checkFreeSpace = new Runnable() {
        @Override
        public void run() {
            LruDiskCache commonCache = getCommonCache();
            LruDiskCache fastCache = getFastCache();
            if (commonCache != null && fastCache != null) {
                long freeSpace = commonCache.getCacheDir().getFreeSpace();
                if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                    commonCache.setCacheSize(MB_50);
                    if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                        commonCache.setCacheSize(MB_10);
                        fastCache.setCacheSize(MB_5);
                        if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                            commonCache.setCacheSize(MB_10);
                            fastCache.setCacheSize(MB_5);
                        }
                    }
                }
            }
            spaceHandler.postDelayed(checkFreeSpace, 60000);
        }
    };


    Handler spaceHandler = new Handler();

    private void clearOldFiles() {
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "Stories"));
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "temp"));
    }


    public void onCreate(Context context) {
        this.context = context;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                clearOldFiles();
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
        new ImageLoader(context);
        OldStatisticManager.getInstance().statistic = new ArrayList<>();
        timerManager = new TimerManager();
        if (tempStoriesListNotifySet != null) {
            if (storiesListNotifySet == null) storiesListNotifySet = new HashSet<>();
            InAppStoryManager.debugSDKCalls("IASService_subscribers", "temp size:" + tempStoriesListNotifySet.size() + " / size:" + storiesListNotifySet.size());
            storiesListNotifySet.addAll(tempStoriesListNotifySet);
            tempStoriesListNotifySet.clear();
        }
        synchronized (lock) {
            INSTANCE = this;
        }
        spaceHandler.postDelayed(checkFreeSpace, 60000);
        if (exHandler == null) exHandler = new Handler();
        exHandler.postDelayed(exHandlerThread, 100);
    }

    private static final Object lock = new Object();
}
