package com.inappstory.sdk.stories.cache;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.dataholders.IContentHolder;
import com.inappstory.sdk.core.dataholders.IListItemContent;
import com.inappstory.sdk.core.dataholders.IReaderContent;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFavoritesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.callbacks.SimpleListCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.ui.list.StoryFavoriteImage;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryDownloadManager {
    private final IASCore core;

    public void clearLocalData() {
        core.contentHolder().favoriteItems().clear();
        core.contentHolder().readerContent().clear();
        core.contentHolder().listsContent().clear();
    }

    static final String EXPAND_STRING = "slides_html,slides_structure,layout,slides_duration,src_list,img_placeholder_src_list,slides_screenshot_share,slides_payload";

    final Object storiesLock = new Object();

    public void getFullStoryByStringId(
            final GetStoryByIdCallback storyByIdCallback,
            final String id,
            final ContentType type,
            final boolean showOnce,
            final SourceType readerSource
    ) {

        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(final String sessionId) {
                final String storyUID = core.statistic().profiling().addTask("api_story");
                core.network().enqueue(
                        core.network().getApi().getStoryById(
                                id,
                                ApiSettings.getInstance().getTestKey(),
                                showOnce ? 1 : 0,
                                1,
                                EXPAND_STRING
                        ),
                        new NetworkCallback<Story>() {
                            @Override
                            public void onSuccess(final Story response) {
                                core.statistic().profiling().setReady(storyUID);
                                core.callbacksAPI().useCallback(
                                        IASCallbackType.SINGLE,
                                        new UseIASCallback<SingleLoadCallback>() {
                                            @Override
                                            public void use(@NonNull SingleLoadCallback callback) {
                                                callback.singleLoadSuccess(
                                                        StoryData.getStoryData(
                                                                response,
                                                                null,
                                                                readerSource,
                                                                type
                                                        )
                                                );
                                            }
                                        }
                                );
                                updateListItem(response, type);
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

                                core.statistic().profiling().setReady(storyUID);
                                core.callbacksAPI().useCallback(
                                        IASCallbackType.SINGLE,
                                        new UseIASCallback<SingleLoadCallback>() {
                                            @Override
                                            public void use(@NonNull SingleLoadCallback callback) {
                                                callback.singleLoadError(id, "Can't load story");
                                            }
                                        }
                                );
                                if (storyByIdCallback != null)
                                    storyByIdCallback.loadError(-1);
                            }
                        });
            }

            @Override
            public void onError() {
                core.callbacksAPI().useCallback(
                        IASCallbackType.SINGLE,
                        new UseIASCallback<SingleLoadCallback>() {
                            @Override
                            public void use(@NonNull SingleLoadCallback callback) {
                                callback.singleLoadError(id, "Can't open session");
                            }
                        }
                );
                if (storyByIdCallback != null)
                    storyByIdCallback.loadError(-1);
            }

        });
    }

    public boolean changePriority(ContentIdWithIndex storyId,
                                  List<ContentIdWithIndex> adjacent,
                                  ContentType type) {
        if (slidesDownloader != null)
            return slidesDownloader.changePriority(storyId, adjacent, type);
        return false;
    }

    public void changePriorityForSingle(ContentIdWithIndex storyId, ContentType type) {
        if (slidesDownloader != null)
            slidesDownloader.changePriorityForSingle(storyId, type);

    }

    public void init() {
        destroy();
        storyDownloader.init();
        slidesDownloader.init();
    }

    public void destroy() {
        storyDownloader.destroy();
        slidesDownloader.destroy();
        getStoriesListByType(ContentType.UGC).clear();
        getStoriesListByType(ContentType.STORY).clear();
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void cleanTasks() {
        cleanTasks(true);
    }

    public void cleanTasks(boolean cleanStories) {
        if (cleanStories) {
            getStoriesListByType(ContentType.UGC).clear();
            getStoriesListByType(ContentType.STORY).clear();
        }
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }


    public void clearCache() throws IOException {
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
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


    private void checkBundleResources(final ReaderPageManager subscriber, final SlideTaskKey key) {
        ISessionHolder sessionHolder = core.sessionManager().getSession();
        if (sessionHolder.checkIfSessionAssetsIsReadySync()) {
            subscriber.slideLoadedInCache(key.index);
        } else {
            sessionHolder.addSessionAssetsIsReadyCallback(new SessionAssetsIsReadyCallback() {
                @Override
                public void isReady() {
                    subscriber.slideLoadedInCache(key.index);
                }
            });
            core.contentPreload().downloadSessionAssets(sessionHolder.getSessionAssets());
        }
    }

    void slideLoaded(final SlideTaskKey key) {
        ViewContentTaskKey viewContentTaskKey = key.viewContentTaskKey;
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == viewContentTaskKey.contentId &&
                        subscriber.getViewContentType() == viewContentTaskKey.contentType) {
                    checkBundleResources(subscriber, key);
                    return;
                }
            }
        }
    }

    HashMap<ViewContentTaskKey, Long> storyErrorDelayed = new HashMap<>();
    HashMap<SlideTaskKey, Long> slideErrorDelayed = new HashMap<>();

    void storyError(ViewContentTaskKey viewContentTaskKey) {
        synchronized (lock) {
            if (subscribers.isEmpty()) {
                storyErrorDelayed.put(
                        viewContentTaskKey,
                        System.currentTimeMillis()
                );
                return;
            }
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == viewContentTaskKey.contentId) {
                    subscriber.storyLoadError();
                    return;
                }
            }
        }
    }

    void slideError(SlideTaskKey slideTaskKey) {
        synchronized (lock) {
            if (subscribers.isEmpty()) {
                slideErrorDelayed.put(
                        slideTaskKey,
                        System.currentTimeMillis()
                );
                return;
            }
            ViewContentTaskKey viewContentTaskKey = slideTaskKey.viewContentTaskKey;
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.isCorrectSubscriber(viewContentTaskKey)) {
                    subscriber.slideLoadError(slideTaskKey.index);
                    return;
                }
            }
        }
    }

    void storyLoaded(IReaderContent story, ContentType type) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == story.id() &&
                        subscriber.getViewContentType() == type) {
                    subscriber.storyLoadedInCache(story);
                    return;
                }
            }
        }
    }

    @WorkerThread
    public void uploadingAdditional(List<Story> storiesToAdd, ContentType type) {
        List<Story> stories = getStoriesListByType(type);
        for (Story story : storiesToAdd) {
            if (story == null) continue;
            if (!stories.contains(story))
                stories.add(story);
            else {
                Story tmp = story;
                int ind = stories.indexOf(story);
                if (ind >= 0) {
                    Story byInd = stories.get(ind);
                    if (tmp.getPages() == null & byInd.getPages() != null) {
                        tmp.pages(byInd.pages);
                    }
                    if (tmp.layout() == null & byInd.layout() != null) {
                        tmp.layout(byInd.layout());
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

    public List<Story> getStoriesListByType(ContentType type) {
        if (type == ContentType.STORY) {
            return this.stories;
        } else {
            return this.ugcStories;
        }
    }

    public int checkIfPageLoaded(int storyId, int index, ContentType type) {
        try {
            return slidesDownloader.checkIfPageLoaded(
                    new SlideTaskKey(
                            new ViewContentTaskKey(storyId, type),
                            index
                    )
            );
        } catch (IOException e) {
            return 0;
        }
    }

    public StoryDownloadManager(
            final @NonNull IASCore core
    ) {
        this.core = core;

        this.storyDownloader = new StoryDownloader(core, new DownloadStoryCallback() {
            @Override
            public void onDownload(IReaderContent story, int loadType, ContentType type) {
                updateListItem(story, type);
                storyLoaded(story, type);
                try {
                    slidesDownloader.addStoryPages(
                            new ViewContentTaskKey(story.id(), type),
                            story,
                            loadType
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ViewContentTaskKey viewContentTaskKey) {
                storyError(viewContentTaskKey);
            }
        }, StoryDownloadManager.this);

        this.slidesDownloader = new SlidesDownloader(
                core,
                new SlideErrorCallback() {
                    @Override
                    public void invoke(SlideTaskKey taskData) {
                        slideError(taskData);
                        storyDownloader.setStoryLoadType(
                                taskData.viewContentTaskKey,
                                -2
                        );
                    }
                },
                StoryDownloadManager.this
        );
    }

    public void addStoryTask(ContentIdWithIndex storyId, ArrayList<ContentIdWithIndex> addIds, ContentType type) {
        try {
            storyDownloader.addStoryTask(storyId, addIds, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reloadStory(ContentIdWithIndex current, ContentType type) {
        if (current == null) return;
        slidesDownloader.removeSlideTasks(new ViewContentTaskKey(current.id(), type));
        storyDownloader.reload(current, new ArrayList<ContentIdWithIndex>(), type);
    }


    public Story getStoryById(int id, ContentType type) {
        List<Story> stories = getStoriesListByType(type);
        synchronized (storiesLock) {
            for (Story story : stories) {
                if (story == null) continue;
                if (story.id == id) return story;
            }
        }
        return null;
    }

    private void updateListItem(final IReaderContent readerContent, ContentType type) {
        if (!(readerContent instanceof Story)) return;
        Story story = (Story) readerContent;
        IListItemContent listItemContent = core.contentHolder()
                .listsContent().getByIdAndType(readerContent.id(), type);
        if (listItemContent == null) {
            listItemContent = story;
            core.contentHolder().listsContent().setByIdAndType(listItemContent,
                    readerContent.id(),
                    type
            );
        } else {
            listItemContent.setOpened(listItemContent.isOpened() || story.isOpened());
        }
    }

    private void setStory(final IReaderContent story, int id, ContentType type) {
        updateListItem(story, type);
    }

    private StoryDownloader storyDownloader;
    private SlidesDownloader slidesDownloader;

    public void addCompletedStoryTask(IReaderContent story, ContentType type) {
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
            stories.set(stories.indexOf(local), story);
            setStory(story, story.id, type);
            storyLoaded(story, type);
            try {
                slidesDownloader.addStoryPages(
                        new ViewContentTaskKey(story.id, type),
                        story,
                        3
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void loadUgcStories(final LoadStoriesCallback callback, final String payload) {
        SimpleListCallback loadCallback = new SimpleListCallback() {

            @Override
            public void onSuccess(final List<Story> response, Object... args) {
                List<Integer> ids = new ArrayList<>();
                for (IListItemContent story: response) {
                    if (story == null) continue;
                    core.contentHolder().listsContent().setByIdAndType(
                            story,
                            story.id(),
                            ContentType.UGC
                    );
                    ids.add(story.id());
                }
                setLocalsOpened(
                        core.contentHolder().listsContent().getByType(ContentType.UGC),
                        ContentType.UGC
                );
                if (callback != null) {
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
                setLocalsOpened(response, ContentType.STORY);
                uploadingAdditional(response, ContentType.STORY);
                List<Story> newStories = new ArrayList<>();
                List<Story> stories = getStoriesListByType(ContentType.STORY);
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
                        uploadingAdditional(newStories, ContentType.STORY);
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
                    final String loadFavUID = core.statistic().profiling().addTask("api_favorite_item");

                    storyDownloader.loadStoryFavoriteList(new NetworkCallback<List<Story>>() {
                        @Override
                        public void onSuccess(List<Story> response2) {
                            core.statistic().profiling().setReady(loadFavUID);
                            IContentHolder contentHolder = core.contentHolder();
                            contentHolder.clearAllFavorites(ContentType.STORY);
                            for (Story story : response2) {
                                contentHolder.favoriteItems().setByIdAndType(
                                        new StoryFavoriteImage(
                                                story.id,
                                                story.imageCoverByQuality(Image.QUALITY_MEDIUM),
                                                story.backgroundColor
                                        ),
                                        story.id(),
                                        ContentType.STORY
                                );
                                contentHolder.favorite(story.id(), ContentType.STORY, true);
                            }
                            if (response2.size() > 0) {
                                setLocalsOpened(response2, ContentType.STORY);
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
                                    favCallback.success(
                                            contentHolder
                                                    .favoriteItems()
                                                    .getByType(ContentType.STORY)
                                    );
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
                            core.statistic().profiling().setReady(loadFavUID);
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
                uploadingAdditional(response, ContentType.STORY);
                List<Story> newStories = new ArrayList<>();
                List<Story> stories = getStoriesListByType(ContentType.STORY);
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
                        setLocalsOpened(newStories, ContentType.STORY);
                        uploadingAdditional(newStories, ContentType.STORY);
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


    public void refreshLocals(ContentType type) {
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

    void setLocalsOpened(List<IListItemContent> response, ContentType type) {
        core.storyListCache().saveStoriesOpened(response, type);
    }

    private List<Story> stories = new ArrayList<>();
    private List<Story> ugcStories = new ArrayList<>();


}
