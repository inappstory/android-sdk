package com.inappstory.sdk.stories.cache;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASAssetsHolder;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASLayoutHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.dataholders.IListsContentHolder;
import com.inappstory.sdk.core.dataholders.IReaderContentHolder;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.usecases.GenerateSlideTaskUseCase;
import com.inappstory.sdk.stories.cache.usecases.LoadSlideUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.utils.LoopedExecutor;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SlidesDownloader {

    private final LoopedExecutor loopedExecutor = new LoopedExecutor(10, 20, getClass().getName());
    private final ExecutorService loadSlideExecutor = Executors.newSingleThreadExecutor();

    private boolean initialized = false;

    public void init() {
        if (initialized) return;
        initialized = true;
        loopedExecutor.task(queueLoadSlideRunnable);
    }

    public void destroy() {
        loopedExecutor.shutdown();
    }

    public void cleanTasks() {
        synchronized (slideTasksLock) {
            slideTasks.clear();
            firstPriority.clear();
            secondPriority.clear();
            maxPriority.clear();
        }
    }


    private final Object slideTasksLock = new Object();
    private final IASCore core;


    public SlidesDownloader(
            IASCore core,
            SlideErrorCallback onSlideError
    ) {
        this.core = core;
        this.onSlideError = onSlideError;
    }

    public void removeSlideTasks(ContentIdAndType contentIdAndType) {
        synchronized (slideTasksLock) {
            Iterator<Map.Entry<SlideTaskKey, SlideTask>> i = slideTasks.entrySet().iterator();
            Map.Entry<SlideTaskKey, SlideTask> key;
            while (i.hasNext()) {
                key = i.next();
                if (Objects.equals(key.getKey().contentIdAndType, contentIdAndType)) {
                    i.remove();
                }
            }
        }
    }

    public int isSlidesLoaded(ContentIdAndType contentIdAndType) throws IOException {
        Set<SlideTaskKey> keys;
        synchronized (slideTasksLock) {
            keys = new HashSet<>(slideTasks.keySet());
        }
        for (SlideTaskKey key : keys) {
            if (Objects.equals(key.contentIdAndType, contentIdAndType)) {
                int res = isSlideLoaded(key);
                if (res != 1) return res;
            }
        }
        return 1;
    }

    public int isSlideLoaded(SlideTaskKey key) throws IOException { //0 - not loaded, 1 - loaded, -1 - loaded with error
        boolean remove = false;
        LruDiskCache cache = core.contentLoader().getCommonCache();
        LruDiskCache vodCache = core.contentLoader().getVodCache();
        SlideTask slideTask = slideTasks.get(key);
        if (slideTask != null) {
            if (slideTask.loadType == 2) {
                for (IResource object : slideTask.staticResources) {
                    String uniqueKey = StringsUtils.md5(object.getUrl());
                    if (!cache.hasKey(uniqueKey)) {
                        remove = true;
                    } else {
                        if (cache.getFullFile(uniqueKey) == null) {
                            synchronized (slideTasksLock) {
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
                            synchronized (slideTasksLock) {
                                slideTask.loadType = 0;
                            }
                            return 0;
                        }
                    }
                }
                if (remove) {
                    slideTasks.remove(key);
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
    List<SlideTaskKey> maxPriority = new ArrayList<>();
    List<SlideTaskKey> secondPriority = new ArrayList<>();

    //adjacent - for next and prev story
    boolean changePriority(
            ContentIdWithIndex current,
            List<ContentIdWithIndex> adjacents,
            ContentType type
    ) {
        synchronized (slideTasksLock) {
            for (int i = firstPriority.size() - 1; i >= 0; i--) {
                if (!secondPriority.contains(firstPriority.get(i))) {
                    secondPriority.add(0, firstPriority.get(i));
                }
            }
            firstPriority.clear();
            int currentId = current.id();
            int currentIndex = current.index();
            IListsContentHolder readerContentHolder = core.contentHolder().listsContent();
            IListItemContent currentStory = readerContentHolder.getByIdAndType(
                    currentId, type
            );
            if (currentStory == null) return false;
            ContentIdAndType storyTaskKey = new ContentIdAndType(currentId, type);
            int sc = currentStory.slidesCount();
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
            try {
                for (ContentIdWithIndex adjacent : adjacents) {
                    if (adjacent == null) continue;
                    IListItemContent adjacentStory = readerContentHolder.getByIdAndType(adjacent.id(), type);
                    ContentIdAndType adjacentTaskKey = new ContentIdAndType(adjacent.id(), type);
                    if (adjacent.index() < adjacentStory.slidesCount() - 1) {
                        SlideTaskKey nk = new SlideTaskKey(adjacentTaskKey, adjacent.index() + 1);
                        secondPriority.remove(nk);
                        firstPriority.add(ind, nk);
                    }

                    SlideTaskKey ck = new SlideTaskKey(adjacentTaskKey, adjacent.index());
                    secondPriority.remove(ck);
                    firstPriority.add(ind, ck);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    public void changePriorityForSingle(ContentIdWithIndex current, ContentType type) {
        int currentId = current.id();
        int currentIndex = current.index();
        synchronized (slideTasksLock) {
            ContentIdAndType contentIdAndType = new ContentIdAndType(currentId, type);
            IReaderContentHolder readerContentHolder = core.contentHolder().readerContent();
            IReaderContent currentStory = readerContentHolder.getByIdAndType(
                    currentId, type
            );
            int sc = currentStory.actualSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(contentIdAndType, i);
                firstPriority.remove(kv);
            }

            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(contentIdAndType, i);
                if (i == currentIndex || i == currentIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentIndex) {
                firstPriority.add(0, new SlideTaskKey(contentIdAndType, currentIndex));
                if (sc > currentIndex + 1) {
                    firstPriority.add(1, new SlideTaskKey(contentIdAndType, currentIndex + 1));
                }
            }
        }
    }

    public void changePriorityForSingleWithLoop(
            ContentIdWithIndex current,
            ContentType type
    ) {
        int currentId = current.id();
        int currentIndex = current.index();
        synchronized (slideTasksLock) {
            ContentIdAndType contentIdAndType = new ContentIdAndType(currentId, type);
            IReaderContentHolder readerContentHolder = core.contentHolder().readerContent();
            IReaderContent currentStory = readerContentHolder.getByIdAndType(
                    currentId, type
            );
            int sc = currentStory.actualSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(contentIdAndType, i);
                firstPriority.remove(kv);
            }
            Set<Integer> loopedIndexes = new HashSet<>();
            loopedIndexes.add(currentIndex);
            loopedIndexes.add(currentIndex + 1 % sc);
            loopedIndexes.add((currentIndex - 1 + sc) % sc);
            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(contentIdAndType, i);
                if (loopedIndexes.contains(i))
                    continue;
                firstPriority.add(kv);
            }
            for (Integer ind : loopedIndexes) {
                firstPriority.add(ind == currentIndex ? 0 : 1, new SlideTaskKey(contentIdAndType, ind));
            }
        }
    }

    public void setMaxPriority(ContentIdAndType contentIdAndType, int slideIndex, boolean firstPosition) {
        SlideTaskKey slideTaskKey = new SlideTaskKey(contentIdAndType, slideIndex);
        synchronized (slideTasksLock) {
            if (maxPriority.contains(slideTaskKey)) {
                if (firstPosition) {
                    maxPriority.remove(slideTaskKey);
                    maxPriority.add(0, slideTaskKey);
                }
                return;
            }
            if (firstPosition)
                maxPriority.add(0, slideTaskKey);
            else
                maxPriority.add(slideTaskKey);
        }
    }

    public void addSlidesHighPriority(
            ContentIdAndType contentIdAndType,
            IReaderContent readerContent,
            Set<Integer> priorityIndexes,
            int loadType,
            boolean forced
    ) {
        synchronized (slideTasksLock) {
            int slidesCountToCache;
            if (loadType == 3) {
                slidesCountToCache = readerContent.actualSlidesCount();
            } else {
                slidesCountToCache = Math.min(2, readerContent.actualSlidesCount());
            }
            try {
                for (int slideIndex = 0; slideIndex < slidesCountToCache; slideIndex++) {
                    SlideTaskKey slideTaskKey = new SlideTaskKey(contentIdAndType, slideIndex);
                    SlideTask slideTask = slideTasks.get(slideTaskKey);
                    if (slideTask == null || slideTask.loadType == 0) {
                        slideTasks.put(
                                slideTaskKey,
                                (new GenerateSlideTaskUseCase(core, readerContent, slideIndex))
                                        .generate()
                                        .forced(forced)
                        );
                        if (priorityIndexes.contains(slideIndex)) {
                            if (maxPriority.contains(slideTaskKey)) {
                                maxPriority.remove(slideTaskKey);
                            }
                            maxPriority.add(0, slideTaskKey);
                        }
                    } else if (slideTask.loadType == -1) {
                        loadSlideError(slideTaskKey);
                    } else if (slideTask.loadType == 2 && slideTask.forced) {
                        slideLoaded(slideTaskKey, slideTask.assetKeys);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        init();
    }

    public void addSlides(
            ContentIdAndType contentIdAndType,
            IReaderContent readerContent,
            int loadType,
            boolean forced
    ) {
        addSlidesHighPriority(
                contentIdAndType,
                readerContent,
                new HashSet<>(),
                loadType,
                forced
        );
    }

    public void addSubscriber(IReaderSlideViewModel pageViewModel) {
        synchronized (pageViewModelsLock) {
            for (IReaderSlideViewModel readerSlideViewModel : pageViewModels) {
                if (readerSlideViewModel.contentIdAndType() == null) continue;
                if (pageViewModel.externalSubscriber() != null &&
                        pageViewModel.externalSubscriber() == readerSlideViewModel.contentIdAndType().contentId) {
                    return;
                }
            }
            pageViewModels.add(pageViewModel);
        }
    }

    public void removeSubscriber(IReaderSlideViewModel pageViewModel) {
        synchronized (pageViewModelsLock) {
            pageViewModels.remove(pageViewModel);
        }
    }

    public void clearSubscribers() {
        synchronized (pageViewModelsLock) {
            Iterator<IReaderSlideViewModel> i = pageViewModels.iterator();
            while (i.hasNext()) {
                IReaderSlideViewModel viewModel = i.next();
                if (viewModel.externalSubscriber() != null)
                    i.remove();
            }
        }
    }


    private final SlideErrorCallback onSlideError;

    private void loadSlideError(SlideTaskKey slideTaskKey) {
        synchronized (slideTasksLock) {
            Objects.requireNonNull(slideTasks.get(slideTaskKey)).loadType = -1;
        }
        List<IReaderSlideViewModel> pageViewModelsCopy = new ArrayList<>();
        synchronized (pageViewModelsLock) {
            pageViewModelsCopy.addAll(pageViewModels);
        }
        if (pageViewModelsCopy.isEmpty()) {
            slideErrorDelayed.put(
                    slideTaskKey,
                    System.currentTimeMillis()
            );
            return;
        }
        ContentIdAndType contentIdAndType = slideTaskKey.contentIdAndType;
        for (IReaderSlideViewModel pageViewModel : pageViewModelsCopy) {
            if (Objects.equals(pageViewModel.contentIdAndType(), contentIdAndType)) {
                pageViewModel.slideLoadError(slideTaskKey.index);
            }
        }
        core.callbacksAPI().useCallback(
                IASCallbackType.ERROR,
                new UseIASCallback<ErrorCallback>() {
                    @Override
                    public void use(@NonNull ErrorCallback callback) {
                        callback.cacheError();
                    }
                }
        );
        if (onSlideError != null)
            onSlideError.invoke(slideTaskKey);
        loopedExecutor.freeExecutor();
    }

    private boolean locked = false;

    private final Runnable queueLoadSlideRunnable = new Runnable() {

        @Override
        public void run() {

            final SlideTaskKey key = getMaxPriorityPageTaskKey();
            if (key == null) {
                loopedExecutor.cancelTask();
                initialized = false;
                return;
            }
            synchronized (slideTasksLock) {
                if (locked) {
                    return;
                }
                locked = true;
                Objects.requireNonNull(slideTasks.get(key)).loadType = 1;
            }
            loadSlide(key);
            synchronized (slideTasksLock) {
                locked = false;
            }
        }
    };

    private void loadSlide(SlideTaskKey slideTaskKey) {
        try {
            SlideTask slideTask;
            synchronized (slideTasksLock) {
                slideTask = slideTasks.get(slideTaskKey);
            }
            if (slideTask == null) {
                loopedExecutor.freeExecutor();
                return;
            }
            if (!(new LoadSlideUseCase(slideTask, core).loadWithResult())) {
                loadSlideError(slideTaskKey);
                return;
            }
            synchronized (slideTasksLock) {
                slideTask.loadType = 2;
            }
            slideLoaded(slideTaskKey, slideTask.assetKeys);
            loopedExecutor.freeExecutor();
        } catch (Throwable t) {
            loadSlideError(slideTaskKey);
        }
    }

    public boolean allSlidesLoaded(
            IReaderContent readerContent,
            ContentType type
    ) {
        int slides = readerContent.actualSlidesCount();
        for (int i = 0; i < slides; i++) {
            SlideTaskKey key =
                    new SlideTaskKey(new ContentIdAndType(readerContent.id(), type), i);
            SlideTask task;
            synchronized (slideTasksLock) {
                task = slideTasks.get(key);
            }
            if (task == null || task.loadType != 2) {
                return false;
            }
        }
        return true;
    }

    public boolean concreteSlidesLoaded(
            IReaderContent readerContent,
            ContentType type,
            Set<Integer> slidesIndexes
    ) {
        int slides = readerContent.actualSlidesCount();
        for (int i = 0; i < slides; i++) {
            if (!slidesIndexes.contains(i)) continue;
            SlideTaskKey key =
                    new SlideTaskKey(new ContentIdAndType(readerContent.id(), type), i);
            SlideTask task;
            synchronized (slideTasksLock) {
                task = slideTasks.get(key);
            }
            if (task == null || task.loadType != 2) {
                return false;
            }
        }
        return true;
    }

    public void checkLayout(
            final IReaderSlideViewModel pageViewModel,
            final int slideIndex
    ) {
        IASLayoutHolder layoutHolder = core.layoutHolder();
        layoutHolder.checkOrAddLayoutIsReadyCallback(new LayoutIsReadyCallback() {
            @Override
            public void isReady() {
                core.layoutHolder().removeLayoutIsReadyCallback(this);
                pageViewModel.slideLoadSuccess(slideIndex);
            }

            @Override
            public void layoutIsLoading() {

            }

            @Override
            public void error() {
                core.layoutHolder().removeLayoutIsReadyCallback(this);
                pageViewModel.slideLoadError(slideIndex);
            }
        });
    }

    public void checkBundleResources(
            final IReaderSlideViewModel pageViewModel,
            final int slideIndex,
            final Set<String> assetKeys
    ) {
        IASAssetsHolder assetsHolder = core.assetsHolder();
        if (assetsHolder.assetsIsDownloaded(assetKeys)) {
            checkLayout(pageViewModel, slideIndex);
        } else {
            assetsHolder.addAssetsIsReadyCallback(new SessionAssetsIsReadyCallback() {
                @Override
                public void isReady() {
                    checkLayout(pageViewModel, slideIndex);
                }

                @Override
                public void assetsIsLoading() {

                }

                @Override
                public void error() {
                    pageViewModel.slideLoadError(slideIndex);
                }

                @Override
                public Set<String> usedAssets() {
                    return assetKeys;
                }
            });
            assetsHolder.downloadAssets();
        }
    }

    HashMap<SlideTaskKey, Long> slideErrorDelayed = new HashMap<>();


    private final Object pageViewModelsLock = new Object();
    List<IReaderSlideViewModel> pageViewModels = new ArrayList<>();

    private void slideLoaded(final SlideTaskKey key, Set<String> assetKeys) {
        ContentIdAndType contentIdAndType = key.contentIdAndType;
        List<IReaderSlideViewModel> checkedPageViewModels = new ArrayList<>();
        synchronized (pageViewModelsLock) {
            for (IReaderSlideViewModel pageViewModel : pageViewModels) {
                if (Objects.equals(pageViewModel.contentIdAndType(), contentIdAndType)) {
                    checkedPageViewModels.add(pageViewModel);
                }
            }
        }
        for (IReaderSlideViewModel pageViewModel : checkedPageViewModels) {
            checkBundleResources(pageViewModel, key.index, assetKeys);
        }
    }

    private SlideTaskKey getMaxPriorityPageTaskKey() {
        synchronized (slideTasksLock) {
            if (slideTasks == null || slideTasks.size() == 0) return null;
            if (firstPriority == null || secondPriority == null) return null;
            for (SlideTaskKey key : maxPriority) {
                if (!slideTasks.containsKey(key)) continue;
                if (Objects.requireNonNull(slideTasks.get(key)).loadType != 0) continue;
                return key;
            }
            for (SlideTaskKey key : firstPriority) {
                if (!slideTasks.containsKey(key)) continue;
                if (Objects.requireNonNull(slideTasks.get(key)).loadType != 0) continue;
                return key;
            }
            for (SlideTaskKey key : secondPriority) {
                if (!slideTasks.containsKey(key)) continue;
                if (Objects.requireNonNull(slideTasks.get(key)).loadType != 0) continue;
                return key;
            }
            for (Map.Entry<SlideTaskKey, SlideTask> entry : slideTasks.entrySet()) {
                if (Objects.requireNonNull(entry.getValue()).loadType != 0) continue;
                if (!Objects.requireNonNull(entry.getValue()).forced) continue;
                return entry.getKey();
            }
            return null;
        }
    }

    private HashMap<SlideTaskKey, SlideTask> slideTasks = new HashMap<>();
}
