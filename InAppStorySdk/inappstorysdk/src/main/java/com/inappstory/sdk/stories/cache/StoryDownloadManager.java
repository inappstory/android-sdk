package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.listwidget.ListLoadedEvent;
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
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.outerevents.SingleLoad;
import com.inappstory.sdk.stories.outerevents.SingleLoadError;
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
    public List<Story> getStories() {
        synchronized (storiesLock) {
            if (stories == null) stories = new ArrayList<>();
            return stories;
        }
    }

    private Context context;

    @WorkerThread
    public void uploadingAdditional(List<Story> newStories) {
        addStories(newStories);
        if (slidesDownloader.uploadAdditional(
                storyDownloader.uploadAdditional())) {
            return;
        }
        putStories(stories);
    }

    static final String EXPAND_STRING = "slides_html,slides_structure,layout,slides_duration,src_list,slides_screenshot_share";

    Object storiesLock = new Object();

    public void getFullStoryById(final GetStoryByIdCallback storyByIdCallback, final int id) {
        List<Story> lStories = new ArrayList<>();
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

    public void getFullStoryByStringId(final GetStoryByIdCallback storyByIdCallback, final String id) {
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
                        CsEventBus.getDefault().post(new SingleLoad(id));
                        if (CallbackManager.getInstance().getSingleLoadCallback() != null) {
                            CallbackManager.getInstance().getSingleLoadCallback().singleLoad(id);
                        }
                        ArrayList<Story> st = new ArrayList<>();
                        st.add(response);
                        InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(st);
                        InAppStoryService.getInstance().getDownloadManager().setStory(response, response.id);
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
                        CsEventBus.getDefault().post(new SingleLoadError());

                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_SINGLE));
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
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_SINGLE));
                if (storyByIdCallback != null)
                    storyByIdCallback.loadError(-1);
            }

        });
    }

    public void changePriority(int storyId, List<Integer> adjacent) {
        if (slidesDownloader != null)
            slidesDownloader.changePriority(storyId, adjacent);
    }

    public void changePriorityForSingle(int storyId) {
        if (slidesDownloader != null)
            slidesDownloader.changePriorityForSingle(storyId);

    }

    public void initDownloaders() {
        storyDownloader.init();
        slidesDownloader.init();
    }

    public void destroy() {
        storyDownloader.destroy();
        slidesDownloader.destroy();
        if (stories == null)
            return;
        stories.clear();
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void cleanTasks() {
        cleanTasks(true);
    }

    public void cleanTasks(boolean cleanStories) {
        if (cleanStories)
            stories.clear();
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void clearCache() {
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
        try {
            if (InAppStoryService.isNull()) return;
            InAppStoryService.getInstance().getCommonCache().clearCache();
            InAppStoryService.getInstance().getFastCache().clearCache();
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

    void slideLoaded(int storyId, int index) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == storyId) {
                    subscriber.slideLoadedInCache(index);
                    return;
                }
            }
        }
    }

    HashMap<Integer, Long> storyErrorDelayed = new HashMap<>();

    void storyError(int storyId) {
        synchronized (lock) {
            if (subscribers.isEmpty()) {
                storyErrorDelayed.put(storyId, System.currentTimeMillis());
                return;
            }
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == storyId) {
                    subscriber.storyLoadError();
                    return;
                }
            }
        }
    }

    void storyLoaded(int storyId) {

        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == storyId) {
                    subscriber.storyLoadedInCache();
                    return;
                }
            }
        }
    }

    public void addStories(List<Story> stories) {
        if (this.stories == null) this.stories = new ArrayList<>();
        for (Story story : stories) {
            if (!this.stories.contains(story))
                this.stories.add(story);
            else {
                Story tmp = story;
                int ind = this.stories.indexOf(story);
                if (ind >= 0) {
                    if (tmp.pages == null & this.stories.get(ind).pages != null) {
                        tmp.pages = new ArrayList<>();
                        tmp.pages.addAll(this.stories.get(ind).pages);
                    }
                    if (tmp.durations == null & this.stories.get(ind).durations != null) {
                        tmp.durations = new ArrayList<>();
                        tmp.durations.addAll(this.stories.get(ind).durations);
                        tmp.setSlidesCount(tmp.durations.size());
                    }
                    if (tmp.layout == null & this.stories.get(ind).layout != null) {
                        tmp.layout = this.stories.get(ind).layout;
                    }
                    if (tmp.srcList == null & this.stories.get(ind).srcList != null) {
                        tmp.srcList = new ArrayList<>();
                        tmp.srcList.addAll(this.stories.get(ind).srcList);
                    }
                    tmp.isOpened = tmp.isOpened || this.stories.get(ind).isOpened;
                }
                this.stories.set(ind, tmp);
            }
        }
    }

    public void putStories(List<Story> stories) {
        if (this.stories == null || this.stories.isEmpty()) {
            this.stories = new ArrayList<>();
            this.stories.addAll(stories);
        } else {
            for (int i = 0; i < stories.size(); i++) {
                boolean newStory = true;
                for (int j = 0; j < this.stories.size(); j++) {
                    if (this.stories.get(j).id == stories.get(i).id) {
                        this.stories.get(j).isOpened = stories.get(i).isOpened;
                        newStory = false;
                        this.stories.set(j, stories.get(i));
                    }
                }
                if (newStory) {
                    this.stories.add(stories.get(i));
                }
            }
        }
    }

    public boolean checkIfPageLoaded(int storyId, int index) {
        try {
            return slidesDownloader.checkIfPageLoaded(new Pair<>(storyId, index));
        } catch (IOException e) {
            return false;
        }
    }

    public StoryDownloadManager(final Context context, ExceptionCache cache) {
        this.context = context;
        this.stories = new ArrayList<>();
        this.favStories = new ArrayList<>();
        this.favoriteImages = new ArrayList<>();
        if (cache != null) {
            if (!cache.getStories().isEmpty())
                this.stories = cache.getStories();
            if (!cache.getStories().isEmpty())
                this.favStories = cache.getFavStories();
            if (!cache.getStories().isEmpty())
                this.favoriteImages = cache.getFavoriteImages();

        }
        this.storyDownloader = new StoryDownloader(new DownloadStoryCallback() {
            @Override
            public void onDownload(Story story, int loadType) {
                Story local = getStoryById(story.id);
                story.isOpened = local.isOpened;
                story.lastIndex = local.lastIndex;
                stories.set(stories.indexOf(local), story);
                setStory(story, story.id);
                storyLoaded(story.id);
                try {
                    slidesDownloader.addStoryPages(story, loadType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int storyId) {
                storyError(storyId);
            }
        }, StoryDownloadManager.this);

        this.slidesDownloader = new SlidesDownloader(new DownloadPageCallback() {
            @Override
            public boolean downloadFile(String url, String storyId, int index) {
                try {
                    Downloader.downloadOrGetFile(url, InAppStoryService.getInstance().getCommonCache(), null, null);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void onError(int storyId) {
                storyError(storyId);
            }
        }, StoryDownloadManager.this);
    }

    public void addStoryTask(int storyId, ArrayList<Integer> addIds) {
        try {
            storyDownloader.addStoryTask(storyId, addIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reloadStory(int storyId) {
        storyDownloader.reloadPage(storyId, new ArrayList<Integer>());
    }

    public void reloadPage(int storyId, int index, ArrayList<Integer> addIds) {
        if (storyDownloader.reloadPage(storyId, addIds)) {
            slidesDownloader.reloadPage(storyId, index);
        }
    }

    public void setCurrentSlide(int storyId, int slideIndex) {
        slidesDownloader.setCurrentSlide(storyId, slideIndex);
    }


    public void clearAllFavoriteStatus() {
        if (stories != null) {
            for (Story story : stories) {
                story.favorite = false;
            }
        }
    }

    public Story getStoryById(int id) {
        if (stories != null) {
            for (Story story : stories) {
                if (story.id == id) return story;
            }
        }
        return null;
    }

    public void setStory(final Story story, int id) {
        Story cur = getStoryById(id);
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
        cur.title = story.title;
        cur.srcList = new ArrayList<>(story.getSrcList());
        cur.durations = new ArrayList<>(story.durations);
        if (!cur.durations.isEmpty()) {
            cur.setSlidesCount(story.durations.size());
        } else {
            cur.setSlidesCount(story.getSlidesCount());
        }
    }

    private StoryDownloader storyDownloader;
    private SlidesDownloader slidesDownloader;


    public void loadStories(String feed, final LoadStoriesCallback callback, boolean isFavorite, boolean hasFavorite) {
        final boolean loadFavorite = hasFavorite;
        SimpleListCallback loadCallback = new SimpleListCallback() {
            @Override
            public void onSuccess(final List<Story> response, Object...args) {

                final ArrayList<Story> resStories = new ArrayList<>();
                for (int i = 0; i < Math.min(response.size(), 4); i++) {
                    resStories.add(response.get(i));
                }
                boolean loadFav = loadFavorite;
                if (args != null && args.length > 0) {
                    loadFav &= (boolean)args[0];
                }
                if (StoriesWidgetService.getInstance() != null) {
                    try {
                        SharedPreferencesAPI.saveString("widgetStories", JsonParser.getJson(resStories));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    StoriesWidgetService.getInstance().refreshFactory();
                }
                CsEventBus.getDefault().post(new ListLoadedEvent());
                if (response == null || response.size() == 0) {
                    if (AppearanceManager.csWidgetAppearance() != null && AppearanceManager.csWidgetAppearance().getWidgetClass() != null) {
                        StoriesWidgetService.loadEmpty(context, AppearanceManager.csWidgetAppearance().getWidgetClass());
                    }
                } else {
                    if (AppearanceManager.csWidgetAppearance() != null && AppearanceManager.csWidgetAppearance().getWidgetClass() != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                StoriesWidgetService.loadSuccess(context, AppearanceManager.csWidgetAppearance().getWidgetClass());
                            }
                        }, 500);
                    }
                }
                setLocalsOpened(response);
                InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(response);
                List<Story> newStories = new ArrayList<>();
                synchronized (storiesLock) {
                    if (stories != null) {
                        for (Story story : response) {
                            if (!stories.contains(story)) {
                                newStories.add(story);
                            }
                        }
                    }
                }
                if (newStories.size() > 0) {
                    try {
                        InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(newStories);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (loadFav) {
                    final String loadFavUID = ProfilingManager.getInstance().addTask("api_favorite_item");
                    storyDownloader.loadStoryFavoriteList(new NetworkCallback<List<Story>>() {
                        @Override
                        public void onSuccess(List<Story> response2) {
                            ProfilingManager.getInstance().setReady(loadFavUID);
                            favStories.clear();
                            favStories.addAll(response2);
                            favoriteImages.clear();
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
                                setLocalsOpened(response2);
                                for (Story story : response2) {
                                    favoriteImages.add(new FavoriteImage(story.id, story.image, story.backgroundColor));
                                }
                                if (callback != null) {
                                    List<Integer> ids = new ArrayList<>();
                                    for (Story story : response) {
                                        ids.add(story.id);
                                    }
                                    callback.storiesLoaded(ids);
                                }
                            } else {
                                if (callback != null) {
                                    List<Integer> ids = new ArrayList<>();
                                    for (Story story : response) {
                                        ids.add(story.id);
                                    }
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
            public void onSuccess(final List<Story> response, Object...args) {
                InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(response);
                List<Story> newStories = new ArrayList<>();
                synchronized (storiesLock) {
                    if (stories != null) {
                        for (Story story : response) {
                            if (!stories.contains(story)) {
                                newStories.add(story);
                            }
                        }
                    }
                }
                if (newStories.size() > 0) {
                    try {
                        setLocalsOpened(newStories);
                        InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(newStories);
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


    public void refreshLocals() {
        List<Story> lStories = new ArrayList<>();
        synchronized (storiesLock) {
            if (stories == null) return;
            lStories.addAll(stories);
        }
        synchronized (storiesLock) {
            for (Story story : lStories) {
                story.isOpened = false;
            }
            setLocalsOpened(lStories);
        }
    }

    void setLocalsOpened(List<Story> response) {
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().saveStoriesOpened(response);
    }


    private List<Story> stories = new ArrayList<>();
    public List<Story> favStories = new ArrayList<>();
    public List<FavoriteImage> favoriteImages = new ArrayList<>();
}
