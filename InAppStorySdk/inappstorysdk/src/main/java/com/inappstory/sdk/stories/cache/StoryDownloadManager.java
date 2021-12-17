package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.listwidget.ListLoadedEvent;
import com.inappstory.sdk.listwidget.StoriesWidgetService;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.ExceptionCache;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.events.DebugEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.events.StoryCacheLoadedEvent;
import com.inappstory.sdk.stories.outerevents.SingleLoad;
import com.inappstory.sdk.stories.outerevents.SingleLoadError;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.ui.reader.StoriesActivity;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StoryDownloadManager {
    public List<Story> getStories() {
        return stories;
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

    static final String EXPAND_STRING = "slides_html,slides_structure,layout,slides_duration,src_list";

    public void getFullStoryById(final GetStoryByIdCallback storyByIdCallback, final int id) {
        for (Story story : InAppStoryService.getInstance().getDownloadManager().getStories()) {
            if (story.id == id) {
                if (story.pages != null) {
                    storyByIdCallback.getStory(story);
                    return;
                } else {
                    storyByIdCallback.getStory(story);
                    return;
                }
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

                NetworkClient.getApi().getStoryById(id, StatisticSession.getInstance().id, 1,
                        ApiSettings.getInstance().getApiKey(), EXPAND_STRING
                ).enqueue(new NetworkCallback<Story>() {
                    @Override
                    public void onSuccess(final Story response) {
                        CsEventBus.getDefault().post(new SingleLoad());
                        if (InAppStoryManager.getInstance().getSingleLoadedListener() != null) {
                            InAppStoryManager.getInstance().getSingleLoadedListener().onLoad();
                        }
                        ArrayList<Story> st = new ArrayList<>();
                        st.add(response);

                        if (InAppStoryService.isNull()) return;
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
                    public void onError(int code, String message) {
                        if (InAppStoryManager.getInstance().getSingleLoadedListener() != null) {
                            InAppStoryManager.getInstance().getSingleLoadedListener().onError();
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


    public void cleanTasks() {
        stories.clear();
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void destroy() {
        storyDownloader.destroy();
        slidesDownloader.destroy();
        if (stories != null)
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
                        tmp.slidesCount = tmp.durations.size();
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
        return slidesDownloader.checkIfPageLoaded(new Pair<>(storyId, index));
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
                if (local != null)
                    stories.set(stories.indexOf(local), story);
                setStory(story, story.id);
                CsEventBus.getDefault().post(new StoryCacheLoadedEvent(story.id));
                try {
                    slidesDownloader.addStoryPages(story, loadType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        }, StoryDownloadManager.this);
    }

    public void addStoryTask(int storyId, ArrayList<Integer> addIds) {
        try {
            storyDownloader.addStoryTask(storyId, addIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reloadPage(int storyId, int index, ArrayList<Integer> addIds) {
        if (storyDownloader.reloadPage(storyId, addIds)) {
            slidesDownloader.reloadPage(storyId, index);
        }
    }

    public void setCurrentSlide(int storyId, int slideIndex) {
        slidesDownloader.setCurrentSlide(storyId, slideIndex);
    }

    public Story getStoryByIdWithEmpty(int id) {
        if (stories != null) {
            for (Story story : stories) {
                if (story.id == id) return story;
            }
        }
        return new Story();
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
            cur.slidesCount = story.durations.size();
        } else {
            cur.slidesCount = story.slidesCount;
        }
    }

    private StoryDownloader storyDownloader;
    private SlidesDownloader slidesDownloader;


    public void loadStories(LoadStoriesCallback callback, boolean isFavorite, boolean hasFavorite) {
        loadStoriesCallback = callback;
        final boolean loadFavorite = hasFavorite;
        NetworkCallback loadCallback = new LoadListCallback() {
            @Override
            protected void error424(String message) {
                super.error424(message);
                storyDownloader.loadStoryList(this, false);
            }

            @Override
            public void onSuccess(final List<Story> response) {
                final ArrayList<Story> stories = new ArrayList<>();
                for (int i = 0; i < Math.min(response.size(), 4); i++) {
                    stories.add(response.get(i));
                }
                try {
                    SharedPreferencesAPI.saveString("widgetStories", JsonParser.getJson(stories));
                } catch (Exception e) {
                    e.printStackTrace();
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

                if (InAppStoryService.isNotNull()) {
                    InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(response);
                    List<Story> newStories = new ArrayList<>();
                    if (InAppStoryService.getInstance().getDownloadManager().getStories() != null) {
                        for (Story story : response) {
                            if (!InAppStoryService.getInstance().getDownloadManager().getStories().contains(story)) {
                                newStories.add(story);
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
                }
                if (loadFavorite) {
                    storyDownloader.loadStoryFavoriteList(new NetworkCallback<List<Story>>() {
                        @Override
                        public void onSuccess(List<Story> response2) {
                            favStories.clear();
                            favStories.addAll(response2);
                            favoriteImages.clear();
                            for (Story st : StoryDownloadManager.this.stories) {
                                for (Story st2 : response2) {
                                    if (st2.id == st.id) {
                                        st.isOpened = true;
                                    }
                                }
                            }
                            if (response2 != null && response2.size() > 0) {
                                setLocalsOpened(response2);
                                for (Story story : response2) {
                                    favoriteImages.add(new FavoriteImage(story.id, story.image, story.backgroundColor));
                                }
                                if (loadStoriesCallback != null) {
                                    List<Integer> ids = new ArrayList<>();
                                    for (Story story : response) {
                                        ids.add(story.id);
                                    }
                                    loadStoriesCallback.storiesLoaded(ids);
                                }
                            } else {
                                if (loadStoriesCallback != null) {
                                    List<Integer> ids = new ArrayList<>();
                                    for (Story story : response) {
                                        ids.add(story.id);
                                    }
                                    loadStoriesCallback.storiesLoaded(ids);
                                }
                            }
                        }

                        @Override
                        public Type getType() {
                            return new StoryListType();
                        }

                        @Override
                        public void onError(int code, String m) {
                            if (loadStoriesCallback != null) {
                                List<Integer> ids = new ArrayList<>();
                                for (Story story : response) {
                                    ids.add(story.id);
                                }
                                loadStoriesCallback.storiesLoaded(ids);
                            }
                        }
                    });
                } else {
                    if (loadStoriesCallback != null) {
                        List<Integer> ids = new ArrayList<>();
                        for (Story story : response) {
                            ids.add(story.id);
                        }
                        loadStoriesCallback.storiesLoaded(ids);
                    }
                }
            }
        };

        storyDownloader.loadStoryList(isFavorite ? loadCallbackWithoutFav : loadCallback, isFavorite);
    }

    public void refreshLocals() {
        if (stories == null) return;
        for (Story story : stories) {
            story.isOpened = false;
        }
        setLocalsOpened(stories);
    }

    void setLocalsOpened(List<Story> response) {
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().saveStoriesOpened(response);
    }


    LoadStoriesCallback loadStoriesCallback;

    NetworkCallback loadCallbackWithoutFav = new LoadListCallback() {
        @Override
        protected void error424(String message) {
            super.error424(message);
            storyDownloader.loadStoryList(this, false);
        }


        @Override
        public void onSuccess(final List<Story> response) {
            InAppStoryService.getInstance().getDownloadManager().uploadingAdditional(response);
            List<Story> newStories = new ArrayList<>();
            if (InAppStoryService.getInstance().getDownloadManager().getStories() != null) {
                for (Story story : response) {
                    if (!InAppStoryService.getInstance().getDownloadManager().getStories().contains(story)) {
                        newStories.add(story);
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
            if (loadStoriesCallback != null) {
                List<Integer> ids = new ArrayList<>();
                for (Story story : response) {
                    ids.add(story.id);
                }
                loadStoriesCallback.storiesLoaded(ids);
            }
        }
    };


    private List<Story> stories;
    public List<Story> favStories = new ArrayList<>();
    public List<FavoriteImage> favoriteImages = new ArrayList<>();
}
