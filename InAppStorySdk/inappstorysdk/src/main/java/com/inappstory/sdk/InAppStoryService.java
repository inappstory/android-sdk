package com.inappstory.sdk;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_1;
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

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.externalapi.subscribers.InAppStoryAPISubscribersManager;
import com.inappstory.sdk.game.preload.IGamePreloader;
import com.inappstory.sdk.game.reader.logger.GameLogSaver;
import com.inappstory.sdk.game.reader.logger.GameLogSender;
import com.inappstory.sdk.game.reader.logger.IGameLogSaver;
import com.inappstory.sdk.game.reader.logger.IGameLogSender;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.ResourceMappingObject;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.FakeStoryDownloadManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.cache.vod.VODCacheItemPart;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.stackfeed.StackStoryObserver;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.list.ListManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    public static boolean isNotNull() {
        return getInstance() != null;
    }

    public static boolean isNull() {
        return getInstance() == null;
    }

    private final IASCore core;

    public InAppStoryService(IASCore core) {
        this.core = core;
    }

    public boolean hasLottieAnimation() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null)
            return manager.utilModulesHolder.hasLottieModule();
        return false;
    }

    public IGamePreloader getGamePreloader() {
        return gamePreloader;
    }

    private IGamePreloader gamePreloader;

    public void restartGamePreloader() {
        IGamePreloader gamePreloader = getGamePreloader();
        gamePreloader.pause();
        gamePreloader.active(true);
        gamePreloader.restart();
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
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                String key = core.storyListCache().getLocalOpensKey(type);
                Set<String> opens = SharedPreferencesAPI.getStringSet(key);
                if (opens == null) opens = new HashSet<>();
                for (Story story : stories) {
                    if (story.isOpened) {
                        opens.add(Integer.toString(story.id));
                    } else if (opens.contains(Integer.toString(story.id))) {
                        story.isOpened = true;
                    }
                }
                SharedPreferencesAPI.saveStringSet(key, opens);
            }
        });
    }

    public void saveStoryOpened(final int id, final Story.StoryType type) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                String key = core.storyListCache().getLocalOpensKey(type);
                Set<String> opens = SharedPreferencesAPI.getStringSet(key);
                if (opens == null) opens = new HashSet<>();
                opens.add(Integer.toString(id));
                SharedPreferencesAPI.saveStringSet(key, opens);
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


    public StoryDownloadManager getStoryDownloadManager() {
        if (storyDownloadManager == null) return fakeStoryDownloadManager;
        return storyDownloadManager;
    }

    FakeStoryDownloadManager fakeStoryDownloadManager;
    StoryDownloadManager storyDownloadManager;

    public static InAppStoryService INSTANCE;


    IGameLogSender logSender;

    public IGameLogSaver getLogSaver() {
        return logSaver;
    }

    IGameLogSaver logSaver;

    public List<FavoriteImage> getFavoriteImages() {
        if (storyDownloadManager == null) return new ArrayList<>();
        if (storyDownloadManager.favoriteImages == null)
            storyDownloadManager.favoriteImages = new ArrayList<>();
        return storyDownloadManager.favoriteImages;
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
        getStoryDownloadManager().destroy();
        if (INSTANCE == this)
            INSTANCE = null;
    }


    public HashMap<String, StackStoryObserver> getStackStoryObservers() {
        return stackStoryObservers;
    }

    public void sendPageOpenStatistic(final int storyId, final int index, final String feedId) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.statistic().v2().createCurrentState(storyId, index, feedId);
                core.statistic().v1(new GetStatisticV1Callback() {
                    @Override
                    public void get(@NonNull IASStatisticV1 manager) {
                        manager.addStatisticBlock(storyId, index);
                    }
                });
            }
        });
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
        return filesDownloadManager.getCachesHolder().getFastCache();
    }

    public LruDiskCache getInfiniteCache() {
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }

    public void addVODResources(Story story, int slideIndex) {
        List<ResourceMappingObject> resources = new ArrayList<>();
        resources.addAll(story.vodResources(slideIndex));
        for (ResourceMappingObject object : resources) {
            VODCacheJournalItem item = filesDownloadManager.getVodCacheJournal().getItem(object.filename);
            if (item == null) {
                filesDownloadManager.getVodCacheJournal().putItem(new VODCacheJournalItem(
                        "",
                        object.filename,
                        "",
                        "",
                        new ArrayList<VODCacheItemPart>(),
                        "",
                        0,
                        object.getUrl(),
                        System.currentTimeMillis()
                ));
            }
        }
    }

    public void setCacheSizes(Context context) {
        long cacheType = MB_100;
        long fastCacheType = MB_10;
        long freeSpace = context.getCacheDir().getFreeSpace();
        if (freeSpace < cacheType + fastCacheType + MB_10) {
            cacheType = MB_50;
            if (freeSpace < cacheType + fastCacheType + MB_10) {
                cacheType = MB_10;
                fastCacheType = MB_5;
                if (freeSpace < cacheType + fastCacheType + MB_10) {
                    cacheType = MB_1;
                    fastCacheType = MB_1;
                }
            }
        }
        getFastCache().setCacheSize(fastCacheType);
        getCommonCache().setCacheSize(cacheType);
    }

    public LruDiskCache getCommonCache() {
        return filesDownloadManager.getCachesHolder().getCommonCache();
    }

    public LruDiskCache getVodCache() {
        return filesDownloadManager.getCachesHolder().getVodCache();
    }

    boolean backPaused = false;


    public TimerManager getTimerManager() {
        return timerManager;
    }

    TimerManager timerManager;

    public InAppStoryAPISubscribersManager getApiSubscribersManager() {
        return apiSubscribersManager;
    }

    private InAppStoryAPISubscribersManager apiSubscribersManager = new InAppStoryAPISubscribersManager(
            InAppStoryManager
                    .getInstance()
                    .iasCore()
    );


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
    ListReaderConnector connector = new ListReaderConnector();

    public ListReaderConnector getListReaderConnector() {
        if (connector == null) connector = new ListReaderConnector();
        return connector;
    }

    public class ListReaderConnector {
        public void changeStory(final int storyId, final String listID, final boolean shownOnlyNewStories) {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (StackStoryObserver storyObserver : stackStoryObservers.values()) {
                        Log.e("changeStory", storyId + " " + listID);
                        storyObserver.onUpdate(storyId, listID, shownOnlyNewStories);
                    }
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.changeStory(storyId, listID);
                    }
                    Story story = getStoryDownloadManager().getStoryById(storyId, Story.StoryType.COMMON);
                    if (story != null)
                        apiSubscribersManager.openStory(story.id, listID);
                }
            });
        }

        public void readerIsClosed() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.readerIsClosed();
                    }
                    apiSubscribersManager.readerIsClosed();
                }
            });
        }

        public void readerIsOpened() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.readerIsOpened();
                    }
                    apiSubscribersManager.readerIsOpened();
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
                    service.apiSubscribersManager.refreshAllLists();
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
                    Story story = service.getStoryDownloadManager().getStoryById(id, Story.StoryType.COMMON);
                    if (story == null) return;
                    if (favStatus) {
                        FavoriteImage favoriteImage = new FavoriteImage(id, story.getImage(), story.getBackgroundColor());
                        if (!favImages.contains(favoriteImage))
                            favImages.add(0, favoriteImage);
                    } else {
                        Iterator<FavoriteImage> favoriteImageIterator = favImages.iterator();
                        while (favoriteImageIterator.hasNext()) {
                            if (favoriteImageIterator.next().getId() == id) {
                                favoriteImageIterator.remove();
                                break;
                            }
                        }
                    }
                    boolean isEmpty = favImages.isEmpty();
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.storyFavorite(id, favStatus, isEmpty);
                    }
                    service.apiSubscribersManager.storyFavorite();
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
                    service.apiSubscribersManager.clearAllFavorites();
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
        ExceptionManager em = new ExceptionManager(InAppStoryManager.getInstance().iasCore());
        ExceptionLog el = em.generateExceptionLog(throwable);
        em.saveException(el);
        em.sendException(el);
    }

    public static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        Thread.UncaughtExceptionHandler oldHandler;
        private final IASCore core;

        public DefaultExceptionHandler(IASCore core) {
            this.core = core;
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
                                    service.getStoryDownloadManager().getStories(Story.StoryType.COMMON),
                                    service.getStoryDownloadManager().favStories,
                                    service.getStoryDownloadManager().favoriteImages
                            ));

                            manager.createServiceThread(
                                    core.appContext()
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


    public void onCreate(final Context context, int cacheSize, ExceptionCache exceptionCache) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(core));

        timerManager = new TimerManager(core);
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

        logSaver = new GameLogSaver();
        logSender = new GameLogSender(this, logSaver);

    }

    private static final Object lock = new Object();

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
