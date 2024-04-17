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
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.game.preload.GamePreloader;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.game.reader.logger.GameLogSaver;
import com.inappstory.sdk.game.reader.logger.GameLogSender;
import com.inappstory.sdk.game.reader.logger.IGameLogSaver;
import com.inappstory.sdk.game.reader.logger.IGameLogSender;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.lrudiskcache.CacheType;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryPlaceholder;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.cache.FakeStoryDownloadManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.stackfeed.StackStoryObserver;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.ListManager;
import com.inappstory.sdk.stories.utils.SessionHolder;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InAppStoryService {

    public static InAppStoryService getInstance() {
        synchronized (lock) {
            if (InAppStoryManager.getInstance() == null) return null;
            return INSTANCE;
        }
    }

    private ISessionHolder sessionHolder = new SessionHolder();

    public ISessionHolder getSession() {
        return sessionHolder;
    }

    public static boolean isNotNull() {
        return getInstance() != null;
    }

    public static boolean isNull() {
        return getInstance() == null;
    }

    public InAppStoryService() {

    }

    public static void useInstance(@NonNull UseServiceInstanceCallback callback) {
        InAppStoryService inAppStoryService = getInstance();
        try {
            if (inAppStoryService != null) {
                callback.use(inAppStoryService);
            } else {
                callback.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public GamePreloader getGamePreloader() {
        return gamePreloader;
    }

    private GamePreloader gamePreloader = new GamePreloader();

    public String getUserId() {
        if (userId == null) {
            InAppStoryManager manager = InAppStoryManager.getInstance();
            if (manager != null) return manager.getUserId();
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public String getTagsString() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        return manager != null ? manager.getTagsString() : null;
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

    public void saveStoriesOpened(final List<Story> stories, final Story.StoryType type) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) {
                Set<String> opens = SharedPreferencesAPI.getStringSet(manager.getLocalOpensKey(type));
                if (opens == null) opens = new HashSet<>();
                for (Story story : stories) {
                    if (story.isOpened) {
                        opens.add(Integer.toString(story.id));
                    } else if (opens.contains(Integer.toString(story.id))) {
                        story.isOpened = true;
                    }
                }
                SharedPreferencesAPI.saveStringSet(manager.getLocalOpensKey(type), opens);
            }
        });
    }

    public void saveStoryOpened(final int id, final Story.StoryType type) {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) {
                Set<String> opens = SharedPreferencesAPI.getStringSet(manager.getLocalOpensKey(type));
                if (opens == null) opens = new HashSet<>();
                opens.add(Integer.toString(id));
                SharedPreferencesAPI.saveStringSet(manager.getLocalOpensKey(type), opens);
            }
        });
    }

    public boolean isSoundOn() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        return manager == null || manager.soundOn();
    }

    public void changeSoundStatus() {
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) {
                manager.soundOn(!manager.soundOn());
            }
        });
    }

    public boolean statV2Disallowed() {
        return !sessionHolder.allowStatV2() || !InAppStoryManager.getInstance().isSendStatistic();
    }

    public boolean statV1Disallowed() {
        return !sessionHolder.allowStatV1() || !InAppStoryManager.getInstance().isSendStatistic();
    }

    public InAppStoryService(String userId) {
        this.userId = userId;
    }


    public StoryDownloadManager getDownloadManager() {
        if (downloadManager == null) return fakeStoryDownloadManager;
        return downloadManager;
    }

    FakeStoryDownloadManager fakeStoryDownloadManager = new FakeStoryDownloadManager();
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

    IGameLogSender logSender;

    public IGameLogSaver getLogSaver() {
        return logSaver;
    }

    IGameLogSaver logSaver;

    void logout() {
        OldStatisticManager.useInstance(new GetOldStatisticManagerCallback() {
            @Override
            public void get(@NonNull OldStatisticManager manager) {
                manager.closeStatisticEvent(null, true);
            }
        });
        SessionManager.getInstance().closeSession(
                true,
                false,
                userId,
                sessionHolder.getSessionId()
        );
    }

    public void clearLocalData() {
        listStoriesIds.clear();
        downloadManager.clearLocalData();
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
        checkSpaceThread.shutdown();
        getDownloadManager().destroy();
        if (INSTANCE == this)
            INSTANCE = null;
    }


    public void sendPageOpenStatistic(final int storyId, final int index, String feedId) {
        OldStatisticManager.useInstance(new GetOldStatisticManagerCallback() {
            @Override
            public void get(@NonNull OldStatisticManager manager) {
                manager.addStatisticBlock(storyId, index);
            }
        });
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

    public boolean isConnected() {
        Context ctx = getContext();
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

    public static boolean isServiceConnected() {
        InAppStoryService service = getInstance();
        if (service == null) return false;
        return service.isConnected();
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

    public void saveSessionImagePlaceholders(final List<StoryPlaceholder> placeholders) {
        if (placeholders == null) return;
        InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryManager manager) {
                for (StoryPlaceholder placeholder : placeholders) {
                    if (!URLUtil.isNetworkUrl(placeholder.defaultVal))
                        continue;
                    String key = placeholder.name;
                    ImagePlaceholderValue defaultVal = ImagePlaceholderValue.createByUrl(placeholder.defaultVal);
                    manager.setDefaultImagePlaceholder(key,
                            defaultVal);
                }
            }
        });
    }

    public void openGameReaderWithGC(
            Context context,
            GameStoryData data,
            String gameId,
            String observableId
    ) {
        ScreensManager.getInstance().openGameReader(
                context,
                data,
                gameId,
                observableId
        );
    }

    public void runStatisticThread() {
        OldStatisticManager.useInstance(new GetOldStatisticManagerCallback() {
            @Override
            public void get(@NonNull OldStatisticManager manager) {
                manager.refreshCallbacks();
            }
        });
    }

    ListReaderConnector connector = new ListReaderConnector();

    public ListReaderConnector getListReaderConnector() {
        if (connector == null) connector = new ListReaderConnector();
        return connector;
    }

    public class ListReaderConnector {
        public void changeStory(final int storyId, final String listID) {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (StackStoryObserver storyObserver : stackStoryObservers.values()) {
                        storyObserver.onUpdate(storyId, listID);
                    }
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.changeStory(storyId, listID);
                    }
                }
            });
        }

        public void closeReader() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.closeReader();
                    }
                }
            });
        }

        public void openReader() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.openReader();
                    }
                }
            });

        }

        public void userIdChanged() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.userIdChanged();
                    }
                }
            });
        }

        public void sessionIsOpened(final String currentSessionId) {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.sessionIsOpened(currentSessionId);
                    }
                }
            });
        }

        public void storyFavorite(final int id, final boolean favStatus) {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    List<FavoriteImage> favImages = service.getFavoriteImages();
                    boolean isEmpty = favImages.isEmpty();
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.storyFavorite(id, favStatus, isEmpty);
                    }
                }
            });
        }

        public void clearAllFavorites() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.clearAllFavorites();
                    }
                }
            });
        }
    }

    Set<ListManager> listSubscribers;
    final HashMap<String, StackStoryObserver> stackStoryObservers = new HashMap<>();
    public static Set<ListManager> tempListSubscribers;

    public Set<ListManager> getListSubscribers() {
        if (listSubscribers == null) listSubscribers = new HashSet<>();
        return listSubscribers;
    }

    public void subscribeStackStoryObserver(String key, StackStoryObserver observer) {
        synchronized (stackStoryObservers) {
            stackStoryObservers.put(key, observer);
        }
    }

    public void unsubscribeStackStoryObserver(StackStoryObserver observer) {
        synchronized (stackStoryObservers) {
            stackStoryObservers.remove(observer);
        }
    }

    public static void checkAndAddListSubscriber(final ListManager listManager) {
        useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                service.addListSubscriber(listManager);
            }

            @Override
            public void error() {
                if (tempListSubscribers == null) tempListSubscribers = new HashSet<>();
                tempListSubscribers.add(listManager);
            }
        });
    }

    public void addListSubscriber(ListManager listManager) {
        if (listSubscribers == null) listSubscribers = new HashSet<>();
        listSubscribers.add(listManager);
    }


    public void clearSubscribers() {
        for (ListManager listManager : listSubscribers) {
            listManager.clear();
        }
        synchronized (stackStoryObservers) {
            stackStoryObservers.clear();
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
        public void uncaughtException(final Thread thread, final Throwable throwable) {
            createExceptionLog(throwable);
            Log.d("InAppStory_SDK_error", throwable.getCause() + "\n"
                    + throwable.getMessage());
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull final InAppStoryService service) {
                    InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
                        @Override
                        public void use(@NonNull InAppStoryManager manager) {
                            if (thread != manager.serviceThread) {
                                if (oldHandler != null)
                                    oldHandler.uncaughtException(thread, throwable);
                                return;
                            }
                            manager.setExceptionCache(new ExceptionCache(
                                    service.getDownloadManager().getStories(Story.StoryType.COMMON),
                                    service.getDownloadManager().favStories,
                                    service.getDownloadManager().favoriteImages
                            ));
                            synchronized (lock) {
                                service.onDestroy();
                            }
                            manager.createServiceThread(
                                    manager.context
                            );
                            if (manager.getExceptionCallback() != null) {
                                manager.getExceptionCallback().onException(throwable);
                            }
                        }
                    });

                }
            });

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
        }
    };

    private ScheduledExecutorService checkSpaceThread = new ScheduledThreadPoolExecutor(1);


    private void clearOldFiles() {
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "Stories"));
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "temp"));
    }

    public void createDownloadManager(ExceptionCache cache) {
        if (downloadManager == null)
            downloadManager = new StoryDownloadManager(context, cache);
    }

    public void onCreate(final Context context, ExceptionCache exceptionCache) {
        this.context = context;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                clearOldFiles();
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
        new ImageLoader(context);
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
        if (checkSpaceThread.isShutdown()) {
            checkSpaceThread = new ScheduledThreadPoolExecutor(1);
        }
        checkSpaceThread.scheduleAtFixedRate(checkFreeSpace, 1L, 60000L, TimeUnit.MILLISECONDS);
        getDownloadManager().initDownloaders();
        logSaver = new GameLogSaver();
        logSender = new GameLogSender(this, logSaver);
        gamePreloader.successUseCaseCallback = new SuccessUseCaseCallback<IGameCenterData>() {
            @Override
            public void onSuccess(final IGameCenterData result) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                context,
                                "Game " + result.id() + " is loaded",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }
        };
    }

    private static final Object lock = new Object();

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
