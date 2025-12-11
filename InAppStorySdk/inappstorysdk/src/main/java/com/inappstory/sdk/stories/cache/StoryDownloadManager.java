package com.inappstory.sdk.stories.cache;

import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IFavoriteItem;
import com.inappstory.sdk.core.dataholders.IContentHolder;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.core.network.content.callbacks.LoadFavoritesCallback;
import com.inappstory.sdk.core.network.content.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.SimpleListCallback;
import com.inappstory.sdk.stories.ui.list.StoryFavoriteImage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryDownloadManager {
    private final IASCore core;

    public void clearLocalData() {
        //    core.contentHolder().favoriteItems().clearByType(ContentType.STORY);
        core.contentHolder().readerContent().clearByType(ContentType.STORY);
        core.contentHolder().readerContent().clearByType(ContentType.UGC);
        //   core.contentHolder().listsContent().clearByType(ContentType.STORY);
        //   core.contentHolder().listsContent().clearByType(ContentType.UGC);
    }

    static final String EXPAND_STRING = "slides,layout";

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

    public void destroy() {
        storyDownloader.destroy();
        slidesDownloader.destroy();
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void cleanTasks() {
        cleanTasks(true);
    }

    public void cleanTasks(boolean cleanStories) {
        if (cleanStories) {
            core.contentHolder().readerContent().clearByType(ContentType.STORY);
            core.contentHolder().readerContent().clearByType(ContentType.UGC);
            //  core.contentHolder().listsContent().clearByType(ContentType.STORY);
            //   core.contentHolder().listsContent().clearByType(ContentType.UGC);
        }
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }


    public void clearCache() throws IOException {
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    private final Object lock = new Object();
    List<IReaderSlideViewModel> pageViewModels = new ArrayList<>();

    public void addSubscriber(IReaderSlideViewModel pageViewModel) {
        synchronized (lock) {
            pageViewModels.add(pageViewModel);
        }
        Long errorTime = storyErrorDelayed.remove(pageViewModel.contentIdAndType());
        if (errorTime != null) {
            pageViewModel.contentLoadError();
        }
        slidesDownloader.addSubscriber(pageViewModel);
    }

    public void removeSubscriber(IReaderSlideViewModel pageViewModel) {
        synchronized (lock) {
            pageViewModels.remove(pageViewModel);
        }
        slidesDownloader.removeSubscriber(pageViewModel);
    }


    HashMap<ContentIdAndType, Long> storyErrorDelayed = new HashMap<>();

    void storyError(ContentIdAndType contentIdAndType) {
        List<IReaderSlideViewModel> locals = new ArrayList<>();
        synchronized (lock) {
            locals.addAll(pageViewModels);
        }
        if (locals.isEmpty()) {
            storyErrorDelayed.put(
                    contentIdAndType,
                    System.currentTimeMillis()
            );
            return;
        }
        for (IReaderSlideViewModel pageViewModel : locals) {
            if (pageViewModel.contentIdAndType().equals(contentIdAndType)) {
                pageViewModel.contentLoadError();
                return;
            }
        }
    }

    void storyLoaded(IReaderContent story, ContentType type) {
        List<IReaderSlideViewModel> locals = new ArrayList<>();
        synchronized (lock) {
            locals.addAll(pageViewModels);
        }
        for (IReaderSlideViewModel pageViewModel : locals) {
            if (
                    pageViewModel.contentIdAndType().equals(
                            new ContentIdAndType(story.id(), type)
                    )
            ) {
                pageViewModel.contentLoadSuccess(story);
                return;
            }
        }
    }

    public int isSlideLoaded(int id, int index, ContentType type) {
        try {
            return slidesDownloader.isSlideLoaded(
                    new SlideTaskKey(
                            new ContentIdAndType(id, type),
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
                storyLoaded(story, type);
                try {
                    slidesDownloader.addStorySlides(
                            new ContentIdAndType(story.id(), type),
                            story,
                            loadType,
                            false
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ContentIdAndType contentIdAndType) {
                storyError(contentIdAndType);
            }
        });

        this.slidesDownloader = new SlidesDownloader(
                core,
                new SlideErrorCallback() {
                    @Override
                    public void invoke(SlideTaskKey taskData) {
                        storyDownloader.setStoryLoadType(
                                taskData.contentIdAndType,
                                -2
                        );
                    }
                }
        );

        storyDownloader.init();
        slidesDownloader.init();
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
        slidesDownloader.removeSlideTasks(new ContentIdAndType(current.id(), type));
        storyDownloader.reload(current, new ArrayList<ContentIdWithIndex>(), type);
    }


    private final StoryDownloader storyDownloader;
    private final SlidesDownloader slidesDownloader;

    public void addCompletedStoryTask(IReaderContent story, ContentType type) {
        if (storyDownloader != null) {
            storyDownloader.addCompletedStoryTask(story.id(), type);
            storyLoaded(story, type);
            try {
                slidesDownloader.addStorySlides(
                        new ContentIdAndType(story.id(), type),
                        story,
                        3,
                        false
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
                for (IListItemContent story : response) {
                    if (story == null) continue;
                    core.contentHolder().listsContent().setByIdAndType(
                            story,
                            story.id(),
                            ContentType.UGC
                    );
                    ids.add(story.id());
                }
                setLocalsOpened(ContentType.UGC);
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
            public void onSuccess(final List<Story> stories, Object... args) {

                String feedId = null;
                final ContentType type = ContentType.STORY;
                final IContentHolder contentHolder = core.contentHolder();
                for (int i = 0; i < stories.size(); i++) {
                    IListItemContent listItemContent = stories.get(i);
                    contentHolder.listsContent().setByIdAndType(
                            listItemContent, listItemContent.id(), type
                    );
                    contentHolder.like(listItemContent.id(), type, listItemContent.like());
                    contentHolder.favorite(listItemContent.id(), type, listItemContent.favorite());
                }
                setLocalsOpened(ContentType.STORY);
                boolean loadFav = loadFavorite;
                IASDataSettingsHolder dataSettingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                RequestLocalParameters requestLocalParameters = new RequestLocalParameters()
                        .sessionId(dataSettingsHolder.sessionIdOrEmpty())
                        .userId(dataSettingsHolder.userId())
                        .sendStatistic(dataSettingsHolder.sendStatistic())
                        .anonymous(dataSettingsHolder.anonymous())
                        .locale(dataSettingsHolder.lang());
                if (args != null && args.length > 0) {
                    int shift = 0;
                    if (args[0] instanceof RequestLocalParameters) {
                        requestLocalParameters = (RequestLocalParameters) args[0];
                        shift = 1;
                    }
                    if (args.length > shift) {
                        loadFav &= (boolean) args[shift];
                    }
                }
                final String sFeedId = feedId;
                if (loadFav) {
                    final String loadFavUID = core.statistic().profiling().addTask("api_favorite_item");

                    storyDownloader.loadStoryFavoriteList(
                            new NetworkCallback<List<Story>>() {
                                @Override
                                public void onSuccess(List<Story> favorites) {
                                    core.statistic().profiling().setReady(loadFavUID);
                                    contentHolder.clearAllFavorites(type);
                                    List<IFavoriteItem> newFavItems = new ArrayList<>();
                                    for (int i = 0; i < favorites.size(); i++) {
                                        IListItemContent listItemContent = favorites.get(i);
                                        newFavItems.add(new StoryFavoriteImage(
                                                listItemContent.id(),
                                                listItemContent.imageCoverByQuality(Image.QUALITY_MEDIUM),
                                                listItemContent.backgroundColor()
                                        ));

                                        contentHolder.favorite(listItemContent.id(), type, true);
                                    }
                                    contentHolder.favoriteItems().setByType(
                                            newFavItems,
                                            ContentType.STORY
                                    );
                                    setLocalsOpened(ContentType.STORY);
                                    if (favorites.size() > 0) {
                                        if (callback != null) {
                                            List<Integer> ids = new ArrayList<>();
                                            for (Story story : stories) {
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
                                            for (Story story : stories) {
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
                                        for (Story story : stories) {
                                            if (story == null) continue;
                                            ids.add(story.id);
                                        }
                                        callback.setFeedId(sFeedId);
                                        callback.storiesLoaded(ids);
                                    }
                                }
                            },
                            requestLocalParameters
                    );
                } else {
                    if (callback != null) {
                        List<Integer> ids = new ArrayList<>();
                        for (Story story : stories) {
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
                final ContentType type = ContentType.STORY;
                final IContentHolder contentHolder = core.contentHolder();
                for (int i = 0; i < response.size(); i++) {
                    IListItemContent listItemContent = response.get(i);
                    contentHolder.listsContent().setByIdAndType(
                            listItemContent, listItemContent.id(), type
                    );
                    contentHolder.like(listItemContent.id(), type, listItemContent.like());
                    contentHolder.favorite(listItemContent.id(), type, listItemContent.favorite());
                }
                setLocalsOpened(ContentType.STORY);
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
        List<IListItemContent> listContent = core.contentHolder().listsContent().getByType(type);
        for (IListItemContent listItemContent : listContent) {
            listItemContent.setOpened(false);
        }
        setLocalsOpened(type);
    }

    void setLocalsOpened(ContentType type) {
        core.storyListCache().saveStoriesOpened(type);
    }

    private List<Story> stories = new ArrayList<>();
    private List<Story> ugcStories = new ArrayList<>();


}
