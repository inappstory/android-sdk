package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.listwidget.StoriesWidgetService;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.callbacks.SimpleListCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.SessionManager;

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

    @WorkerThread
    public void uploadingAdditional(List<Story> newStories, Story.StoryType type) {
        addStories(newStories, type);
    }

    static final String EXPAND_STRING = "slides_html,slides_structure,layout,slides_duration,src_list,img_placeholder_src_list,slides_screenshot_share,slides_payload";

    Object storiesLock = new Object();

    public void getFullStoryById(final GetStoryByIdCallback storyByIdCallback,
                                 final int id,
                                 Story.StoryType type) {
        List<Story> lStories = new ArrayList<>();
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            if (stories != null)
                lStories.addAll(stories);
        }
        for (Story story : lStories) {
            if (story.id == id) {
                storyByIdCallback.getStory(story);
                return;
            }
        }
    }

    public void getFullStoryByStringId(final GetStoryByIdCallback storyByIdCallback,
                                       final String id, final Story.StoryType type) {
        if (InAppStoryService.isNull()) {
            storyByIdCallback.loadError(-1);
            return;
        }
        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                if (InAppStoryService.isNull()) {
                    storyByIdCallback.loadError(-1);
                    return;
                }
                final String storyUID = ProfilingManager.getInstance().addTask("api_story");
                NetworkClient.getApi().getStoryById(id, 1, EXPAND_STRING
                ).enqueue(new NetworkCallback<Story>() {
                    @Override
                    public void onSuccess(final Story response) {
                        if (InAppStoryService.isNull()) {
                            storyByIdCallback.loadError(-1);
                            return;
                        }
                        ProfilingManager.getInstance().setReady(storyUID);
                        if (CallbackManager.getInstance().getSingleLoadCallback() != null) {
                            CallbackManager.getInstance().getSingleLoadCallback().singleLoad(id);
                        }
                        ArrayList<Story> st = new ArrayList<>();
                        st.add(response);
                        uploadingAdditional(st, type);
                        setStory(response, response.id, type);
                        if (storyByIdCallback != null)
                            storyByIdCallback.getStory(response);
                    }

                    @Override
                    public Type getType() {
                        return Story.class;
                    }

                    @Override
                    public void onTimeout() {
                        onError(-1, "Timeout");
                    }

                    @Override
                    public void onError(int code, String message) {

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

    public void changePriority(int storyId, List<Integer> adjacent, Story.StoryType type) {
        if (slidesDownloader != null)
            slidesDownloader.changePriority(storyId, adjacent, type);
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
        try {
            InAppStoryService inAppStoryService = InAppStoryService.getInstance();
            if (inAppStoryService != null) {
                inAppStoryService.listStoriesIds.clear();
                inAppStoryService.getCommonCache().clearCache();
                inAppStoryService.getFastCache().clearCache();
                inAppStoryService.getInfiniteCache().clearCache();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void removeSubscriber(ReaderPageManager manager) {
        synchronized (lock) {
            subscribers.remove(manager);
        }
    }

    void slideLoaded(SlideTaskData key) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == key.storyId && subscriber.getStoryType() == key.storyType) {
                    subscriber.slideLoadedInCache(key.index);
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

    void storyLoaded(int storyId, Story.StoryType type) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == storyId && subscriber.getStoryType() == type) {
                    subscriber.storyLoadedInCache();
                    return;
                }
            }
        }
    }

    public void addStories(List<Story> storiesToAdd, Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        for (Story story : storiesToAdd) {
            if (!stories.contains(story))
                stories.add(story);
            else {
                Story tmp = story;
                int ind = stories.indexOf(story);
                if (ind >= 0) {
                    if (tmp.pages == null & stories.get(ind).pages != null) {
                        tmp.pages = new ArrayList<>();
                        tmp.pages.addAll(stories.get(ind).pages);
                    }
                    if (tmp.durations == null & stories.get(ind).durations != null) {
                        tmp.durations = new ArrayList<>();
                        tmp.durations.addAll(stories.get(ind).durations);
                        tmp.setSlidesCount(tmp.durations.size());
                    }
                    if (tmp.layout == null & stories.get(ind).layout != null) {
                        tmp.layout = stories.get(ind).layout;
                    }
                    if (tmp.srcList == null & stories.get(ind).srcList != null) {
                        tmp.srcList = new ArrayList<>();
                        tmp.srcList.addAll(stories.get(ind).srcList);
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
        if (stories.isEmpty()) {
            stories.addAll(storiesToPut);
        } else {
            for (int i = 0; i < storiesToPut.size(); i++) {
                boolean newStory = true;
                for (int j = 0; j < stories.size(); j++) {
                    if (stories.get(j).id == storiesToPut.get(i).id) {
                        stories.get(j).isOpened = storiesToPut.get(i).isOpened;
                        newStory = false;
                        stories.set(j, storiesToPut.get(i));
                    }
                }
                if (newStory) {
                    stories.add(storiesToPut.get(i));
                }
            }
        }
    }

    public int checkIfPageLoaded(int storyId, int index, Story.StoryType type) {
        try {
            return slidesDownloader.checkIfPageLoaded(new SlideTaskData(storyId, index, type));
        } catch (IOException e) {
            return 0;
        }
    }

    public StoryDownloadManager(final Context context, ExceptionCache cache) {
        this.context = context;
        this.stories = new ArrayList<>();
        this.ugcStories = new ArrayList<>();
        this.favStories = new ArrayList<>();
        this.favoriteImages = new ArrayList<>();
        if (cache != null) {
            if (!cache.getStories().isEmpty())
                this.stories.addAll(cache.getStories());
            if (!cache.getStories().isEmpty())
                this.favStories.addAll(cache.getFavStories());
            if (!cache.getStories().isEmpty())
                this.favoriteImages.addAll(cache.getFavoriteImages());

        }
        this.storyDownloader = new StoryDownloader(new DownloadStoryCallback() {
            @Override
            public void onDownload(Story story, int loadType, Story.StoryType type) {
                Story local = getStoryById(story.id, type);
                story.isOpened = local.isOpened;
                story.lastIndex = local.lastIndex;
                setStory(story, story.id, type);
                storyLoaded(story.id, type);
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
                    DownloadFileState state = Downloader.downloadOrGetFile(urlWithAlter.getUrl(), true, InAppStoryService.getInstance().getCommonCache(), null, null);
                    if (urlWithAlter.getAlter() != null && (state == null || state.getFullFile() == null)) {
                        Downloader.downloadOrGetFile(urlWithAlter.getAlter(), true, InAppStoryService.getInstance().getCommonCache(), null, null);
                        if (state != null && state.getFullFile() != null) return DownloadPageFileStatus.SUCCESS;
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

    public void setCurrentSlide(int storyId, int slideIndex) {
        slidesDownloader.setCurrentSlide(storyId, slideIndex);
    }


    public void clearAllFavoriteStatus(Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        for (Story story : stories) {
            story.favorite = false;
        }
    }

    public Story getStoryById(int id, Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            for (Story story : stories) {
                if (story.id == id) return story;
            }
        }
        return null;
    }

    public void setStory(final Story story, int id, Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        Story cur = getStoryById(id, type);
        if (cur == null) {
            stories.add(story);
            return;
        }
        cur.loadedPages = new ArrayList<>();
        cur.pages = new ArrayList<>(story.pages);
        for (int i = 0; i < cur.pages.size(); i++) {
            cur.loadedPages.add(false);
        }
        cur.id = id;
        cur.layout = story.layout;
        cur.hasAudio = story.hasAudio;
        cur.tags = story.tags;
        cur.hasSwipeUp = story.hasSwipeUp();
        cur.title = story.title;
        cur.statTitle = story.statTitle;
        cur.srcList = new ArrayList<>(story.getSrcList());
        cur.imagePlaceholdersList = new ArrayList<>(story.getImagePlaceholdersList());
        cur.durations = new ArrayList<>(story.durations);
        cur.slidesShare = story.slidesShare;
        cur.slidesPayload = story.slidesPayload;
        if (!cur.durations.isEmpty()) {
            cur.setSlidesCount(story.durations.size());
        } else {
            cur.setSlidesCount(story.getSlidesCount());
        }
    }

    private StoryDownloader storyDownloader;
    private SlidesDownloader slidesDownloader;

    public void cleanStoriesIndex(Story.StoryType type) {
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            for (Story story : stories) {
                if (story != null)
                    story.lastIndex = 0;
            }
        }
    }

    public void addCompletedStoryTask(Story story, Story.StoryType type) {
        boolean noStory = true;
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            for (Story localStory : stories) {
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
            storyLoaded(story.id, type);
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

    public void loadStories(String feed, final LoadStoriesCallback callback,
                            boolean isFavorite, boolean hasFavorite) {
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
                                    for (Story st2 : response2) {
                                        if (st2.id == st.id) {
                                            st.isOpened = true;
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
                                        ids.add(story.id);
                                    }
                                    callback.setFeedId(sFeedId);
                                    callback.storiesLoaded(ids);
                                }
                            } else {
                                if (callback != null) {
                                    List<Integer> ids = new ArrayList<>();
                                    for (Story story : response) {
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
                        public void onTimeout() {
                            ProfilingManager.getInstance().setReady(loadFavUID);
                            super.onTimeout();
                        }

                        @Override
                        public void onError(int code, String m) {
                            ProfilingManager.getInstance().setReady(loadFavUID);
                            if (callback != null) {
                                List<Integer> ids = new ArrayList<>();
                                for (Story story : response) {
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
            storyDownloader.loadStoryListByFeed(feed, loadCallback);
        } else {
            storyDownloader.loadStoryList(isFavorite ? loadCallbackWithoutFav : loadCallback, isFavorite);
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

    void setLocalsOpened(List<Story> response, Story.StoryType type) {
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().saveStoriesOpened(response, type);
    }


    private List<Story> stories = new ArrayList<>();
    private List<Story> ugcStories = new ArrayList<>();
    public List<Story> favStories = new ArrayList<>();
    public List<FavoriteImage> favoriteImages = new ArrayList<>();
}
