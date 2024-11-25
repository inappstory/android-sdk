package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.listwidget.StoriesWidgetService;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.StorySlide;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFavoritesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.callbacks.SimpleListCallback;
import com.inappstory.sdk.stories.cache.usecases.GetCacheFileUseCase;
import com.inappstory.sdk.stories.cache.usecases.StoryResourceFileUseCase;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCase;
import com.inappstory.sdk.stories.cache.usecases.StoryVODResourceFileUseCaseResult;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryDownloadManager {
    public List<Story> getStories(Story.StoryType type) {
        return getStoriesListByType(type);
    }

    private Context context;

    public void clearLocalData() {
        favoriteImages.clear();
        favStories.clear();
        stories.clear();
    }

    @WorkerThread
    public void uploadingAdditional(List<Story> newStories, Story.StoryType type) {
        addStories(newStories, type);
    }

    static final String EXPAND_STRING = "slides,layout";

    final Object storiesLock = new Object();

    public void getFullStoryByStringId(
            final GetStoryByIdCallback storyByIdCallback,
            final String id,
            final Story.StoryType type,
            final boolean showOnce,
            final SourceType readerSource
    ) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        final InAppStoryService service = InAppStoryService.getInstance();
        if (networkClient == null || service == null) {
            storyByIdCallback.loadError(-1);
            return;
        }
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(final String sessionId) {
                final String storyUID = ProfilingManager.getInstance().addTask("api_story");
                networkClient.enqueue(
                        networkClient.getApi().getStoryById(
                                id,
                                ApiSettings.getInstance().getTestKey(),
                                showOnce ? 1 : 0,
                                1,
                                EXPAND_STRING
                        ),
                        new NetworkCallback<Story>() {
                            @Override
                            public void onSuccess(final Story response) {
                                ProfilingManager.getInstance().setReady(storyUID);
                                if (CallbackManager.getInstance().getSingleLoadCallback() != null) {
                                    CallbackManager.getInstance().getSingleLoadCallback().singleLoad(
                                            StoryData.getStoryData(
                                                    response,
                                                    null,
                                                    readerSource,
                                                    type
                                            )
                                    );
                                }
                                ArrayList<Story> st = new ArrayList<>();
                                st.add(response);
                                uploadingAdditional(st, type);
                                setStory(response, response.id, type);
                                if (storyByIdCallback != null)
                                    storyByIdCallback.getStory(response, sessionId);
                            }

                            @Override
                            public Type getType() {
                                return Story.class;
                            }

                            @Override
                            public void emptyContent() {
                                if (storyByIdCallback != null)
                                    storyByIdCallback.loadError(-2);
                            }

                            @Override
                            public void errorDefault(String message) {

                                ProfilingManager.getInstance().setReady(storyUID);
                                if (CallbackManager.getInstance().getErrorCallback() != null) {
                                    CallbackManager.getInstance().getErrorCallback().loadSingleError();
                                }
                                if (storyByIdCallback != null)
                                    storyByIdCallback.loadError(-1);
                            }
                        });
            }

            @Override
            public void onError() {

                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().loadSingleError();
                }
                if (storyByIdCallback != null)
                    storyByIdCallback.loadError(-1);
            }

        });
    }

    public boolean changePriority(int storyId, List<Integer> adjacent, Story.StoryType type) {
        if (slidesDownloader != null)
            return slidesDownloader.changePriority(storyId, adjacent, type);
        return false;
    }

    public void changePriorityForSingle(int storyId, Story.StoryType type) {
        if (slidesDownloader != null)
            slidesDownloader.changePriorityForSingle(storyId, type);

    }

    public void initDownloaders() {
        storyDownloader.init();
        slidesDownloader.init();
    }

    public void destroy() {
        storyDownloader.destroy();
        slidesDownloader.destroy();
        getStoriesListByType(Story.StoryType.UGC).clear();
        getStoriesListByType(Story.StoryType.COMMON).clear();
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void cleanTasks() {
        cleanTasks(true);
    }

    public void cleanTasks(boolean cleanStories) {
        if (cleanStories) {
            getStoriesListByType(Story.StoryType.UGC).clear();
            getStoriesListByType(Story.StoryType.COMMON).clear();
        }
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }


    public void clearCache() {
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();

        KeyValueStorage.clear();
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService inAppStoryService) throws Exception {
                inAppStoryService.listStoriesIds.clear();
                inAppStoryService.getCommonCache().clearCache();
                inAppStoryService.getFastCache().clearCache();
                inAppStoryService.getInfiniteCache().clearCache();
                inAppStoryService.getVodCache().clearCache();
                inAppStoryService.getFilesDownloadManager().getVodCacheJournal().clear();
                inAppStoryService.getFilesDownloadManager().clearCallbacks();
            }
        });
    }

    private final Object lock = new Object();
    List<ReaderPageManager> subscribers = new ArrayList<>();

    public void addSubscriber(ReaderPageManager manager) {
        synchronized (lock) {
            subscribers.add(manager);
        }
        Long errorTime = storyErrorDelayed.remove(manager.getStoryId());
        if (errorTime != null) {
            manager.storyLoadError();
        }
    }

    public void removeSubscriber(
            InAppStoryService service,
            ReaderPageManager subscriber,
            SessionAssetsIsReadyCallback callback
    ) {
        synchronized (lock) {
            subscribers.remove(subscriber);
            ISessionHolder sessionHolder = service.getSession();
            if (callback != null)
                sessionHolder.removeSessionAssetsIsReadyCallback(callback);
        }
    }


    public void checkBundleResources(
            InAppStoryService service,
            SessionAssetsIsReadyCallback callback,
            final ReaderPageManager subscriber,
            final SlideTaskData key
    ) {
        ISessionHolder sessionHolder = service.getSession();
        if (sessionHolder.checkIfSessionAssetsIsReady()) {
            subscriber.bundleContentInCache(key.index);
        } else {
            sessionHolder.addSessionAssetsIsReadyCallback(callback);
            service.downloadSessionAssets(sessionHolder.getSessionAssets());
        }
    }

    void slideLoaded(final SlideTaskData key) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == key.storyId && subscriber.getStoryType() == key.storyType) {
                    subscriber.slideContentInCache(key.index);
                    // checkBundleResources(subscriber, key);
                    return;
                }
            }
        }
    }

    HashMap<StoryTaskData, Long> storyErrorDelayed = new HashMap<>();
    HashMap<SlideTaskData, Long> slideErrorDelayed = new HashMap<>();

    void storyError(StoryTaskData storyTaskData) {
        synchronized (lock) {
            if (subscribers.isEmpty()) {
                storyErrorDelayed.put(
                        storyTaskData,
                        System.currentTimeMillis()
                );
                return;
            }
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == storyTaskData.storyId) {
                    subscriber.storyLoadError();
                    return;
                }
            }
        }
    }

    void slideError(SlideTaskData slideTaskData) {
        synchronized (lock) {
            if (subscribers.isEmpty()) {
                slideErrorDelayed.put(
                        slideTaskData,
                        System.currentTimeMillis()
                );
                return;
            }
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == slideTaskData.storyId) {
                    subscriber.slideLoadError(slideTaskData.index);
                    return;
                }
            }
        }
    }

    void storyLoaded(Story story, Story.StoryType type) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == story.id && subscriber.getStoryType() == type) {
                    subscriber.storyLoadedInCache(story);
                    return;
                }
            }
        }
    }

    public void addStories(List<Story> storiesToAdd, Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        for (Story story : storiesToAdd) {
            if (story == null) continue;
            if (!stories.contains(story))
                stories.add(story);
            else {
                Story tmp = story;
                int ind = stories.indexOf(story);
                if (ind >= 0) {
                    Story localStory = stories.get(ind);
                    tmp.slides = mergeSlides(tmp, localStory);
                    if (tmp.layout == null & localStory.layout != null) {
                        tmp.layout = localStory.layout;
                    }
                    if (tmp.ugcPayload == null & localStory.ugcPayload != null) {
                        tmp.ugcPayload = new HashMap<>(localStory.ugcPayload);
                    }
                    tmp.isOpened = tmp.isOpened || stories.get(ind).isOpened;
                }
                stories.set(ind, tmp);
            }
        }
    }

    public List<Story> getStoriesListByType(Story.StoryType type) {
        if (type == Story.StoryType.COMMON) {
            if (this.stories == null) this.stories = new ArrayList<>();
            return this.stories;
        } else {
            if (this.ugcStories == null) this.ugcStories = new ArrayList<>();
            return this.ugcStories;
        }
    }

    public void putStories(List<Story> storiesToPut, Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        if (storiesToPut == null) return;
        for (Story story : storiesToPut) {
            if (story == null) continue;
            boolean newStory = true;
            for (int j = 0; j < stories.size(); j++) {
                if (stories.get(j).id == story.id) {
                    stories.get(j).isOpened = story.isOpened;
                    newStory = false;
                    stories.set(j, story);
                }
            }
            if (newStory) {
                stories.add(story);
            }
        }
    }


    public int checkIfPageLoaded(int storyId, int index, Story.StoryType type) {
        try {
            return slidesDownloader.checkIfPageLoaded(new SlideTaskData(storyId, index, type));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public StoryDownloadManager() {
    }

    public StoryDownloadManager(final Context context, ExceptionCache cache) {
        this.context = context;
        this.stories = new ArrayList<>();
        this.ugcStories = new ArrayList<>();
        this.favStories = new ArrayList<>();
        this.favoriteImages = new ArrayList<>();
        if (cache != null) {
            this.stories.addAll(cache.getStories());
            this.favStories.addAll(cache.getFavStories());
            this.favoriteImages.addAll(cache.getFavoriteImages());
        }
        this.storyDownloader = new StoryDownloader(new DownloadStoryCallback() {
            @Override
            public void onDownload(Story story, int loadType, Story.StoryType type) {
                Story local = getStoryById(story.id, type);
                if (local != null) {
                    story.isOpened = local.isOpened;
                    story.lastIndex = local.lastIndex;
                }
                setStory(story, story.id, type);
                storyLoaded(story, type);
                //story.testMethod();
                try {
                    slidesDownloader.addStoryPages(story, loadType, type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(StoryTaskData storyTaskData) {
                storyError(storyTaskData);
            }
        }, StoryDownloadManager.this);

        this.slidesDownloader = new SlidesDownloader(new DownloadPageCallback() {
            @Override
            public DownloadPageFileStatus downloadFile(UrlWithAlter urlWithAlter, SlideTaskData slideTaskData) {
                try {
                    Log.e("StoryResources", slideTaskData.storyId + " " + slideTaskData.index + " " + urlWithAlter);
                    InAppStoryService service = InAppStoryService.getInstance();
                    if (service == null) return DownloadPageFileStatus.ERROR;
                    GetCacheFileUseCase<DownloadFileState> useCase =
                            new StoryResourceFileUseCase(
                                    service.getFilesDownloadManager(),
                                    urlWithAlter.getUrl()
                            );
                    DownloadFileState state = useCase.getFile();
                    if (urlWithAlter.getAlter() != null && (state == null || state.getFullFile() == null)) {
                        //placeholders case, download full
                        useCase =
                                new StoryResourceFileUseCase(
                                        service.getFilesDownloadManager(),
                                        urlWithAlter.getAlter()
                                );
                        state = useCase.getFile();
                        if (state != null && state.getFullFile() != null)
                            return DownloadPageFileStatus.SUCCESS;
                        return DownloadPageFileStatus.SKIP;
                    }
                    if (state != null && state.getFullFile() != null)
                        return DownloadPageFileStatus.SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return DownloadPageFileStatus.ERROR;
            }

            @Override
            public DownloadPageFileStatus downloadVODFile(
                    String url,
                    String uniqueKey,
                    SlideTaskData slideTaskData,
                    long start,
                    long end
            ) {
                try {
                    Log.e("StoryResources", slideTaskData.storyId + " " + slideTaskData.index + " " + url);
                    InAppStoryService service = InAppStoryService.getInstance();
                    if (service == null) return DownloadPageFileStatus.ERROR;
                    GetCacheFileUseCase<StoryVODResourceFileUseCaseResult> useCase =
                            new StoryVODResourceFileUseCase(
                                    service.getFilesDownloadManager(),
                                    url,
                                    uniqueKey,
                                    start,
                                    end
                            );
                    Log.e("UrlFile", "DownloadManager: " + start + "-" + end + " " + url);

                    StoryVODResourceFileUseCaseResult state = useCase.getFile();
                    if (state != null) {
                        return DownloadPageFileStatus.SUCCESS;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return DownloadPageFileStatus.ERROR;
            }


            @Override
            public void onError(StoryTaskData storyTaskData) {
                storyError(storyTaskData);
            }

            @Override
            public void onSlideError(SlideTaskData taskData) {
                slideError(taskData);
                storyDownloader.setStoryLoadType(
                        new StoryTaskData(
                                taskData.storyId,
                                taskData.storyType
                        ),
                        -2);
            }
        }, StoryDownloadManager.this);
    }

    public void addStoryTask(int storyId, ArrayList<Integer> addIds, Story.StoryType type) {
        try {
            storyDownloader.addStoryTask(storyId, addIds, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reloadStory(int storyId, Story.StoryType type) {
        slidesDownloader.removeSlideTasks(new StoryTaskData(storyId, type));
        storyDownloader.reload(storyId, new ArrayList<Integer>(), type);
    }


    public void clearAllFavoriteStatus(Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        for (Story story : stories) {
            if (story == null) continue;
            story.favorite = false;
        }
    }

    public Story getStoryById(int id, Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            for (Story story : stories) {
                if (story == null) continue;
                if (story.id == id) return story;
            }
        }
        return null;
    }

    private StorySlide copySlide(StorySlide oldSlide, StorySlide newSlide) {
        if (newSlide == null) return oldSlide;
        if (oldSlide == null) return newSlide;
        StorySlide storySlide;
        if (newSlide.resources == null) {
            storySlide = oldSlide;
            storySlide.timelineSettings = newSlide.timelineSettings;
        } else {
            storySlide = newSlide;
        }
        return storySlide;
    }

    private List<StorySlide> mergeSlides(Story oldStory, Story newStory) {
        List<StorySlide> merged = new ArrayList<>();
        for (int i = 0; i < Math.max(oldStory.slides().size(), newStory.slides().size()); i++) {
            StorySlide oldSlide = null;
            StorySlide newSlide = null;
            if (oldStory.slides().size() > i) {
                oldSlide = oldStory.slides().get(i);
            }
            if (newStory.slides().size() > i) {
                newSlide = newStory.slides().get(i);
            }
            StorySlide mergeSlide = copySlide(oldSlide, newSlide);
            merged.add(mergeSlide);
        }
        return merged;
    }

    public void setStory(final Story story, int id, Story.StoryType type) {
        if (story == null) return;
        List<Story> stories = getStoriesListByType(type);
        Story cur = getStoryById(id, type);
        if (cur == null) {
            stories.add(story);
            return;
        }
        cur.loadedPages = new ArrayList<>();
        cur.slides = new ArrayList<>(story.slides());
        for (int i = 0; i < Math.max(cur.slides().size(), story.slides().size()); i++) {
            cur.loadedPages.add(false);
        }
        cur.slides = mergeSlides(cur, story);
        cur.id = id;
        cur.layout = story.layout;
        cur.hasAudio = story.hasAudio;
        cur.tags = story.tags;
        cur.hasSwipeUp = story.hasSwipeUp();
        cur.title = story.title;
        cur.statTitle = story.statTitle;
        cur.setSlidesCount(story.getSlidesCount());
    }

    private StoryDownloader storyDownloader;
    private SlidesDownloader slidesDownloader;

    public void cleanStoriesIndex(Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            for (Story story : stories) {
                if (story == null)
                    continue;
                story.lastIndex = 0;
            }
        }
    }

    public void addCompletedStoryTask(Story story, Story.StoryType type) {
        boolean noStory = true;
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            for (Story localStory : stories) {
                if (localStory == null) continue;
                if (localStory.id == story.id) {
                    noStory = false;
                    break;
                }
            }
            if (noStory) stories.add(story);
        }
        if (storyDownloader != null) {
            storyDownloader.addCompletedStoryTask(story.id, type);
            Story local = getStoryById(story.id, type);
            story.isOpened = local.isOpened;
            story.lastIndex = local.lastIndex;
            stories.set(stories.indexOf(local), story);
            setStory(story, story.id, type);
            storyLoaded(story, type);
            try {
                slidesDownloader.addStoryPages(story, 3, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void loadUgcStories(final LoadStoriesCallback callback, final String payload) {
        SimpleListCallback loadCallback = new SimpleListCallback() {

            @Override
            public void onSuccess(final List<Story> response, Object... args) {
                uploadingAdditional(response, Story.StoryType.UGC);
                List<Story> stories = getStoriesListByType(Story.StoryType.UGC);
                setLocalsOpened(stories, Story.StoryType.UGC);
                if (callback != null) {
                    List<Integer> ids = new ArrayList<>();
                    for (Story story : response) {
                        if (story == null) continue;
                        ids.add(story.id);
                    }
                    callback.storiesLoaded(ids);
                }
            }

            @Override
            public void onError(String message) {
                if (callback != null) {
                    callback.onError();
                }
            }
        };
        storyDownloader.loadUgcStoryList(loadCallback, payload);
    }

    public void loadStories(
            String feed,
            final LoadStoriesCallback callback,
            final LoadFavoritesCallback favCallback,
            boolean isFavorite,
            boolean hasFavorite
    ) {
        final boolean loadFavorite = hasFavorite;
        SimpleListCallback loadCallback = new SimpleListCallback() {
            @Override
            public void onSuccess(final List<Story> response, Object... args) {

                String feedId = null;
                final ArrayList<Story> resStories = new ArrayList<>();
                for (int i = 0; i < Math.min(response.size(), 4); i++) {
                    resStories.add(response.get(i));
                }

                if (StoriesWidgetService.getInstance() != null) {
                    try {
                        SharedPreferencesAPI.saveString("widgetStories", JsonParser.getJson(resStories));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    StoriesWidgetService.getInstance().refreshFactory();
                }
                if (response == null || response.size() == 0) {
                    if (AppearanceManager.csWidgetAppearance() != null
                            && AppearanceManager.csWidgetAppearance().getWidgetClass() != null) {
                        StoriesWidgetService.loadEmpty(context,
                                AppearanceManager.csWidgetAppearance().getWidgetClass());
                    }
                } else {
                    if (AppearanceManager.csWidgetAppearance() != null
                            && AppearanceManager.csWidgetAppearance().getWidgetClass() != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                StoriesWidgetService.loadSuccess(context,
                                        AppearanceManager.csWidgetAppearance().getWidgetClass());
                            }
                        }, 500);
                    }
                }
                setLocalsOpened(response, Story.StoryType.COMMON);
                uploadingAdditional(response, Story.StoryType.COMMON);
                List<Story> newStories = new ArrayList<>();
                List<Story> stories = getStoriesListByType(Story.StoryType.COMMON);
                synchronized (storiesLock) {
                    for (Story story : response) {
                        if (story == null) continue;
                        if (!stories.contains(story)) {
                            newStories.add(story);
                        }
                    }
                }
                if (newStories.size() > 0) {
                    try {
                        uploadingAdditional(newStories, Story.StoryType.COMMON);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                boolean loadFav = loadFavorite;
                if (args != null && args.length > 0) {
                    loadFav &= (boolean) args[0];
                    if (args.length > 1) {
                        feedId = (String) args[1];
                    }
                }
                final String sFeedId = feedId;
                if (loadFav) {
                    final String loadFavUID = ProfilingManager.getInstance().addTask("api_favorite_item");

                    storyDownloader.loadStoryFavoriteList(new NetworkCallback<List<Story>>() {
                        @Override
                        public void onSuccess(List<Story> response2) {
                            ProfilingManager.getInstance().setReady(loadFavUID);
                            favStories.clear();
                            favStories.addAll(response2);
                            favoriteImages.clear();
                            List<Story> stories = getStoriesListByType(Story.StoryType.COMMON);
                            synchronized (storiesLock) {
                                for (Story st : stories) {
                                    if (st == null) continue;
                                    for (Story st2 : response2) {
                                        if (st2 == null) continue;
                                        if (st2.id == st.id) {
                                            st.isOpened = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (response2 != null && response2.size() > 0) {
                                setLocalsOpened(response2, Story.StoryType.COMMON);
                                for (Story story : response2) {
                                    favoriteImages.add(new FavoriteImage(story.id, story.image, story.backgroundColor));
                                }
                                if (callback != null) {
                                    List<Integer> ids = new ArrayList<>();
                                    for (Story story : response) {
                                        if (story == null) continue;
                                        ids.add(story.id);
                                    }
                                    callback.setFeedId(sFeedId);
                                    callback.storiesLoaded(ids);
                                }
                                if (favCallback != null) {
                                    favCallback.success(favoriteImages);
                                }
                            } else {
                                if (callback != null) {
                                    List<Integer> ids = new ArrayList<>();
                                    for (Story story : response) {
                                        if (story == null) continue;
                                        ids.add(story.id);
                                    }
                                    callback.setFeedId(sFeedId);
                                    callback.storiesLoaded(ids);
                                }
                            }
                        }

                        @Override
                        public Type getType() {
                            return new StoryListType();
                        }

                        @Override
                        public void errorDefault(String message) {
                            ProfilingManager.getInstance().setReady(loadFavUID);
                            if (callback != null) {
                                List<Integer> ids = new ArrayList<>();
                                for (Story story : response) {
                                    if (story == null) continue;
                                    ids.add(story.id);
                                }
                                callback.setFeedId(sFeedId);
                                callback.storiesLoaded(ids);
                            }
                        }
                    });
                } else {
                    if (callback != null) {
                        List<Integer> ids = new ArrayList<>();
                        for (Story story : response) {
                            if (story == null) continue;
                            ids.add(story.id);
                        }
                        callback.setFeedId(sFeedId);
                        callback.storiesLoaded(ids);
                    }
                }
            }

            @Override
            public void onError(String message) {
                if (callback != null) {
                    callback.onError();
                }
            }
        };
        SimpleListCallback loadCallbackWithoutFav = new SimpleListCallback() {

            @Override
            public void onSuccess(final List<Story> response, Object... args) {
                uploadingAdditional(response, Story.StoryType.COMMON);
                List<Story> newStories = new ArrayList<>();
                List<Story> stories = getStoriesListByType(Story.StoryType.COMMON);
                synchronized (storiesLock) {
                    for (Story story : response) {
                        if (story == null) continue;
                        if (!stories.contains(story)) {
                            newStories.add(story);
                        }
                    }
                }
                if (newStories.size() > 0) {
                    try {
                        setLocalsOpened(newStories, Story.StoryType.COMMON);
                        uploadingAdditional(newStories, Story.StoryType.COMMON);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    List<Integer> ids = new ArrayList<>();
                    for (Story story : response) {
                        if (story == null) continue;
                        ids.add(story.id);
                    }
                    callback.storiesLoaded(ids);
                }
            }

            @Override
            public void onError(String message) {
                if (callback != null) {
                    callback.onError();
                }
            }
        };
        if (feed != null && !isFavorite) {
            storyDownloader.loadStoryListByFeed(feed, loadCallback, true);
        } else {
            storyDownloader.loadStoryList(isFavorite ? loadCallbackWithoutFav : loadCallback, isFavorite, true);
        }
    }


    public void refreshLocals(Story.StoryType type) {
        List<Story> lStories = new ArrayList<>();
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            lStories.addAll(stories);
        }
        synchronized (storiesLock) {
            for (Story story : lStories) {
                story.isOpened = false;
            }
            setLocalsOpened(lStories, type);
        }
    }

    void setLocalsOpened(final List<Story> response, final Story.StoryType type) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                service.saveStoriesOpened(response, type);
            }
        });
    }

    private List<Story> stories = new ArrayList<>();
    private List<Story> ugcStories = new ArrayList<>();
    public List<Story> favStories = new ArrayList<>();
    public List<FavoriteImage> favoriteImages = new ArrayList<>();
}
