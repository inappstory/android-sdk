package com.inappstory.sdk;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.webkit.URLUtil;

import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.game.cache.GameLoadCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.lrudiskcache.CacheType;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.game.GameLoadedCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.ListManager;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.io.File;
import java.io.IOException;
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

    private boolean sharingProcess = false;
    private static final Object shareLock = new Object();

    public boolean isShareProcess() {
        synchronized (shareLock) {
            return sharingProcess;
        }
    }

    public void isShareProcess(boolean sharingProcess) {
        synchronized (shareLock) {
            this.sharingProcess = sharingProcess;
        }
    }


    public HashMap<String, List<Integer>> listStoriesIds = new HashMap<>();

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
  /*      Set<String> opens = SharedPreferencesAPI.getStringSet(InAppStoryManager.getInstance().getLocalOpensKey());
        if (opens == null) opens = new HashSet<>();
        for (Story story : stories) {
            opens.add(Integer.toString(story.id));
        }
        SharedPreferencesAPI.saveStringSet(InAppStoryManager.getInstance().getLocalOpensKey(), opens);


*/
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
        if (InAppStoryManager.getInstance() != null) {
            return InAppStoryManager.getInstance().soundOn();
        } else {
            return true;
        }
    }

    public void changeSoundStatus() {
        if (InAppStoryManager.getInstance() != null) {
            InAppStoryManager.getInstance().soundOn(!InAppStoryManager.getInstance().soundOn());
        }
    }


    public boolean getSendNewStatistic() {
        if (InAppStoryManager.getInstance() == null) return false;
        if (!Session.needToUpdate()) {
            if (Session.getInstance().statisticPermissions == null) return false;
            return InAppStoryManager.getInstance().isSendStatistic()
                    && Session.getInstance().statisticPermissions.allowStatV2;
        }
        return false;
    }

    public boolean getSendStatistic() {
        if (InAppStoryManager.getInstance() == null) return false;
        if (!Session.needToUpdate()) {
            if (Session.getInstance().statisticPermissions == null) return false;
            return InAppStoryManager.getInstance().isSendStatistic()
                    && Session.getInstance().statisticPermissions.allowStatV1;
        }
        return false;
    }

    public InAppStoryService(String userId) {
        this.userId = userId;
    }

    public StoryDownloadManager getDownloadManager() {
        return downloadManager;
    }

    StoryDownloadManager downloadManager;
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

    void logout() {
        OldStatisticManager.getInstance().closeStatisticEvent(null, true);
        SessionManager.getInstance().closeSession(true, false);
        OldStatisticManager.getInstance().clear();
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
        spaceHandler.removeCallbacksAndMessages(null);
        getDownloadManager().destroy();
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

    ListReaderConnector connector = new ListReaderConnector();

    public ListReaderConnector getListReaderConnector() {
        if (connector == null) connector = new ListReaderConnector();
        return connector;
    }

    public class ListReaderConnector {
        public void changeStory(int storyId, String listID) {
            if (InAppStoryService.isNull()) return;
            for (ListManager sub : InAppStoryService.getInstance().getListSubscribers()) {
                sub.changeStory(storyId, listID);
            }
        }

        public void closeReader() {
            if (InAppStoryService.isNull()) return;
            for (ListManager sub : InAppStoryService.getInstance().getListSubscribers()) {
                sub.closeReader();
            }
        }

        public void openReader() {
            if (InAppStoryService.isNull()) return;
            for (ListManager sub : InAppStoryService.getInstance().getListSubscribers()) {
                sub.openReader();
            }
        }

        public void changeUserId() {
            if (InAppStoryService.isNull()) return;
            for (ListManager sub : InAppStoryService.getInstance().getListSubscribers()) {
                sub.changeUserId();
            }
        }

        public void storyFavorite(int id, boolean favStatus) {
            if (InAppStoryService.isNull()) return;

            List<FavoriteImage> favImages = InAppStoryService.getInstance().getFavoriteImages();
            boolean isEmpty = favImages.isEmpty();
            for (ListManager sub : InAppStoryService.getInstance().getListSubscribers()) {
                sub.storyFavorite(id, favStatus, isEmpty);
            }
        }

        public void clearAllFavorites() {
            if (InAppStoryService.isNull()) return;
            for (ListManager sub : InAppStoryService.getInstance().getListSubscribers()) {
                sub.clearAllFavorites();
            }
        }
    }

    Set<ListManager> listSubscribers;
    public static Set<ListManager> tempListSubscribers;

    public Set<ListManager> getListSubscribers() {
        if (listSubscribers == null) listSubscribers = new HashSet<>();
        return listSubscribers;
    }

    public static void checkAndAddListSubscriber(ListManager listManager) {
        if (isNotNull()) {
            getInstance().addListSubscriber(listManager);
        } else {
            if (tempListSubscribers == null) tempListSubscribers = new HashSet<>();
            tempListSubscribers.add(listManager);
        }
    }

    public void addListSubscriber(ListManager listManager) {
        if (listSubscribers == null) listSubscribers = new HashSet<>();
        listSubscribers.add(listManager);
    }


    public void clearSubscribers() {
        for (ListManager listManager : listSubscribers) {
            listManager.clear();
        }
        tempListSubscribers.clear();
        listSubscribers.clear();
    }


    public void removeListSubscriber(ListManager listManager) {
        if (listSubscribers == null) return;
        listManager.clear();
        if (tempListSubscribers != null)
            tempListSubscribers.remove(listManager);
        listSubscribers.remove(listManager);
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
                InAppStoryManager.getInstance().setExceptionCache(new ExceptionCache(
                        getInstance().getDownloadManager().getStories(Story.StoryType.COMMON),
                        getInstance().getDownloadManager().favStories,
                        getInstance().getDownloadManager().favoriteImages
                ));

            }
            try {
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
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
        new ImageLoader(context);
        OldStatisticManager.getInstance().statistic = new ArrayList<>();
        createDownloadManager(exceptionCache);
        timerManager = new TimerManager();
        if (tempListSubscribers != null) {
            if (listSubscribers == null) listSubscribers = new HashSet<>();
            InAppStoryManager.debugSDKCalls("IASService_subscribers", "temp size:" + tempListSubscribers.size() + " / size:" + listSubscribers.size());
            listSubscribers.addAll(tempListSubscribers);
            tempListSubscribers.clear();
        }
        synchronized (lock) {
            INSTANCE = this;
        }
        spaceHandler.postDelayed(checkFreeSpace, 60000);


        if (exHandler == null) exHandler = new Handler();
        exHandler.postDelayed(exHandlerThread, 100);
    }

    private static Object lock = new Object();

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
