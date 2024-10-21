package com.inappstory.sdk.stories.cache;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.dataholders.IReaderContentHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.dataholders.IResource;
import com.inappstory.sdk.core.dataholders.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.usecases.GenerateSlideTaskUseCase;
import com.inappstory.sdk.stories.cache.usecases.LoadSlideUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.utils.LoopedExecutor;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


class SlidesDownloader {
    StoryDownloadManager manager;

    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);

    void init() {
        loopedExecutor.init(queueLoadSlideRunnable);
    }

    void destroy() {
        loopedExecutor.shutdown();
    }

    void cleanTasks() {
        synchronized (pageTasksLock) {
            pageTasks.clear();
            firstPriority.clear();
            secondPriority.clear();
        }
    }


    private final Object pageTasksLock = new Object();
    private final IASCore core;


    SlidesDownloader(
            IASCore core,
            SlideErrorCallback onSlideError,
            StoryDownloadManager manager
    ) {
        this.core = core;
        this.onSlideError = onSlideError;
        this.manager = manager;
    }

    public void removeSlideTasks(ViewContentTaskKey viewContentTaskKey) {
        synchronized (pageTasksLock) {
            Iterator<Map.Entry<SlideTaskKey, SlideTask>> i = pageTasks.entrySet().iterator();
            Map.Entry<SlideTaskKey, SlideTask> key;
            while (i.hasNext()) {
                key = i.next();
                if (Objects.equals(key.getKey().viewContentTaskKey, viewContentTaskKey)) {
                    i.remove();
                }
            }
        }
    }

    int checkIfPageLoaded(SlideTaskKey key) throws IOException { //0 - not loaded, 1 - loaded, -1 - loaded with error
        boolean remove = false;
        LruDiskCache cache = core.contentLoader().getCommonCache();
        LruDiskCache vodCache = core.contentLoader().getVodCache();
        SlideTask slideTask = pageTasks.get(key);
        if (slideTask != null) {
            if (slideTask.loadType == 2) {
                for (IResource object : slideTask.staticResources) {
                    String uniqueKey = StringsUtils.md5(object.getUrl());
                    if (!cache.hasKey(uniqueKey)) {
                        remove = true;
                    } else {
                        if (cache.getFullFile(uniqueKey) == null) {
                            synchronized (pageTasksLock) {
                                slideTask.loadType = 0;
                            }
                            return 0;
                        }
                    }
                }
                for (IResource object : slideTask.vodResources) {
                    String uniqueKey = object.getFileName();
                    if (!vodCache.hasKey(uniqueKey)) {
                        remove = true;
                    } else {
                        if (vodCache.getFileFromKey(uniqueKey) == null) {
                            synchronized (pageTasksLock) {
                                slideTask.loadType = 0;
                            }
                            return 0;
                        }
                    }
                }
                if (remove) {
                    pageTasks.remove(key);
                } else {
                    return 1;
                }
            } else if (slideTask.loadType == -1) {
                return -1;
            }
        }
        return 0;
    }


    List<SlideTaskKey> firstPriority = new ArrayList<>();
    List<SlideTaskKey> secondPriority = new ArrayList<>();

    //adjacent - for next and prev story
    boolean changePriority(ContentIdWithIndex current, List<ContentIdWithIndex> adjacents, ContentType type) {
        synchronized (pageTasksLock) {
            for (int i = firstPriority.size() - 1; i >= 0; i--) {
                if (!secondPriority.contains(firstPriority.get(i))) {
                    secondPriority.add(0, firstPriority.get(i));
                }
            }
            firstPriority.clear();
            int currentId = current.id();
            int currentIndex = current.index();
            IReaderContentHolder readerContentHolder = core.contentHolder().readerContent();
            IReaderContent currentStory = readerContentHolder.getByIdAndType(
                    currentId, type
            );
            if (currentStory == null) return false;
            ViewContentTaskKey storyTaskKey = new ViewContentTaskKey(currentId, type);
            int sc = currentStory.actualSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(storyTaskKey, i);
                secondPriority.remove(kv);
                if (i == currentIndex || i == currentIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentIndex) {
                firstPriority.add(0, new SlideTaskKey(storyTaskKey, currentIndex));
                if (sc > currentIndex + 1) {
                    firstPriority.add(1, new SlideTaskKey(storyTaskKey, currentIndex + 1));
                }
            }
            int ind = Math.min(firstPriority.size(), 2);
            for (ContentIdWithIndex adjacent : adjacents) {
                if (adjacent == null) continue;
                IReaderContent adjacentStory = readerContentHolder.getByIdAndType(adjacent.id(), type);
                ViewContentTaskKey adjacentTaskKey = new ViewContentTaskKey(adjacent.id(), type);
                if (adjacent.index() < adjacentStory.actualSlidesCount() - 1) {
                    SlideTaskKey nk = new SlideTaskKey(adjacentTaskKey, adjacent.index() + 1);
                    secondPriority.remove(nk);
                    firstPriority.add(ind, nk);
                }

                SlideTaskKey ck = new SlideTaskKey(adjacentTaskKey, adjacent.index());
                secondPriority.remove(ck);
                firstPriority.add(ind, ck);
            }
        }
        return true;
    }

    void changePriorityForSingle(ContentIdWithIndex current, ContentType type) {
        int currentId = current.id();
        int currentIndex = current.index();
        synchronized (pageTasksLock) {
            ViewContentTaskKey viewContentTaskKey = new ViewContentTaskKey(currentId, type);
            IReaderContentHolder readerContentHolder = core.contentHolder().readerContent();
            IReaderContent currentStory = readerContentHolder.getByIdAndType(
                    currentId, type
            );
            int sc = currentStory.actualSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(viewContentTaskKey, i);
                firstPriority.remove(kv);
            }

            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(viewContentTaskKey, i);
                if (i == currentIndex || i == currentIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentIndex) {
                firstPriority.add(0, new SlideTaskKey(viewContentTaskKey, currentIndex));
                if (sc > currentIndex + 1) {
                    firstPriority.add(1, new SlideTaskKey(viewContentTaskKey, currentIndex + 1));
                }
            }
        }
    }

    void addStoryPages(ViewContentTaskKey viewContentTaskKey,
                       IReaderContent IReaderContent,
                       int loadType) throws Exception {
        synchronized (pageTasksLock) {
            int slidesCountToCache;
            if (loadType == 3) {
                slidesCountToCache = IReaderContent.actualSlidesCount();
            } else {
                slidesCountToCache = 2;
            }
            for (int slideIndex = 0; slideIndex < slidesCountToCache; slideIndex++) {
                SlideTaskKey slideTaskKey = new SlideTaskKey(viewContentTaskKey, slideIndex);
                if (pageTasks.get(slideTaskKey) == null) {
                    pageTasks.put(
                            slideTaskKey,
                            (new GenerateSlideTaskUseCase(core, IReaderContent, slideIndex))
                                    .generate()
                    );
                }
            }
        }
    }

    private final SlideErrorCallback onSlideError;

    private void loadSlideError(SlideTaskKey key) {
        core.callbacksAPI().useCallback(IASCallbackType.ERROR,
                new UseIASCallback<ErrorCallback>() {
                    @Override
                    public void use(@NonNull ErrorCallback callback) {
                        callback.cacheError();
                    }
                });
        synchronized (pageTasksLock) {
            Objects.requireNonNull(pageTasks.get(key)).loadType = -1;
            onSlideError.invoke(key);
        }
        loopedExecutor.freeExecutor();
    }


    private final Runnable queueLoadSlideRunnable = new Runnable() {

        @Override
        public void run() {

            final SlideTaskKey key = getMaxPriorityPageTaskKey();
            if (key == null) {
                loopedExecutor.freeExecutor();
                return;
            }
            synchronized (pageTasksLock) {
                Objects.requireNonNull(pageTasks.get(key)).loadType = 1;
            }
            loadSlide(key);
        }
    };

    void loadSlide(SlideTaskKey slideTaskKey) {
        try {
            SlideTask slideTask;
            synchronized (pageTasksLock) {
                slideTask = pageTasks.get(slideTaskKey);
            }
            if (slideTask == null) {
                loopedExecutor.freeExecutor();
                return;
            }
            if (!(new LoadSlideUseCase(slideTask, core).loadWithResult())) {
                loadSlideError(slideTaskKey);
                return;
            }
            synchronized (pageTasksLock) {
                slideTask.loadType = 2;
            }
            manager.slideLoaded(slideTaskKey);
            loopedExecutor.freeExecutor();
        } catch (Throwable t) {
            loadSlideError(slideTaskKey);
        }
    }

    private SlideTaskKey getMaxPriorityPageTaskKey() {
        synchronized (pageTasksLock) {
            if (pageTasks == null || pageTasks.size() == 0) return null;
            if (firstPriority == null || secondPriority == null) return null;
            for (SlideTaskKey key : firstPriority) {
                if (!pageTasks.containsKey(key)) continue;
                if (Objects.requireNonNull(pageTasks.get(key)).loadType != 0) continue;
                return key;
            }
            for (SlideTaskKey key : secondPriority) {
                if (!pageTasks.containsKey(key)) continue;
                if (Objects.requireNonNull(pageTasks.get(key)).loadType != 0) continue;
                return key;
            }
            return null;
        }
    }

    private HashMap<SlideTaskKey, SlideTask> pageTasks = new HashMap<>();
}
