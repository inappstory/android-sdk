package com.inappstory.sdk.stories.cache;

import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASAssetsHolder;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.dataholders.IListsContentHolder;
import com.inappstory.sdk.core.dataholders.IReaderContentHolder;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.usecases.GenerateSlideTaskUseCase;
import com.inappstory.sdk.stories.cache.usecases.LoadSlideUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.utils.LoopedExecutor;
import com.inappstory.sdk.utils.format.StringsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SlidesDownloader {

    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);

    public void init() {
        loopedExecutor.init(queueLoadSlideRunnable);
    }

    public void destroy() {
        loopedExecutor.shutdown();
    }

    public void cleanTasks() {
        synchronized (slideTasksLock) {
            slideTasks.clear();
            firstPriority.clear();
            secondPriority.clear();
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

    public void addStorySlides(
            ContentIdAndType contentIdAndType,
            IReaderContent readerContent,
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
                    if (slideTasks.get(slideTaskKey) == null) {
                        slideTasks.put(
                                slideTaskKey,
                                (new GenerateSlideTaskUseCase(core, readerContent, slideIndex))
                                        .generate()
                                        .forced(forced)
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addSubscriber(IReaderSlideViewModel pageViewModel) {
        synchronized (pageViewModelsLock) {
            for (IReaderSlideViewModel readerSlideViewModel : pageViewModels) {
                if (readerSlideViewModel.externalSubscriber() != null &&
                        readerSlideViewModel.externalSubscriber() == pageViewModel.contentIdAndType().contentId) {
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
            if (pageViewModel.contentIdAndType().equals(contentIdAndType)) {
                pageViewModel.slideLoadError(slideTaskKey.index);
                return;
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

    private final Runnable queueLoadSlideRunnable = new Runnable() {

        @Override
        public void run() {

            final SlideTaskKey key = getMaxPriorityPageTaskKey();
            if (key == null) {
                loopedExecutor.freeExecutor();
                return;
            }
            synchronized (slideTasksLock) {
                Objects.requireNonNull(slideTasks.get(key)).loadType = 1;
            }
            loadSlide(key);
        }
    };

    private void loadSlide(SlideTaskKey slideTaskKey) {
        try {
            SlideTask slideTask;
            synchronized (slideTasksLock) {
                slideTask = slideTasks.get(slideTaskKey);
            }
            Log.e("slidesDownloader", "loadSlide start " + slideTaskKey + " " + slideTask);
            if (slideTask == null) {
                loopedExecutor.freeExecutor();
                return;
            }
            if (!(new LoadSlideUseCase(slideTask, core).loadWithResult())) {
                Log.e("slidesDownloader", "loadSlide error " + slideTaskKey);
                loadSlideError(slideTaskKey);
                return;
            }
            Log.e("slidesDownloader", "loadSlide end sync " + slideTaskKey);
            synchronized (slideTasksLock) {
                slideTask.loadType = 2;
            }
            Log.e("slidesDownloader", "loadSlide end unsync " + slideTaskKey);
            slideLoaded(slideTaskKey);
            loopedExecutor.freeExecutor();
        } catch (Throwable t) {
            Log.e("slidesDownloader", "loadSlide error " + slideTaskKey + " " + t.getMessage());
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


    public void checkBundleResources(
            final IReaderSlideViewModel pageViewModel,
            final int slideIndex
    ) {
        final String page = pageViewModel.contentIdAndType() + " " + slideIndex;
        if (core.assetsHolder().assetsIsDownloaded()) {
            Log.e("slidesDownloader", "slideLoadSuccess sync " + page);
            pageViewModel.slideLoadSuccess(slideIndex);
        } else {
            IASAssetsHolder assetsHolder = core.assetsHolder();
            Log.e("slidesDownloader", "checkBundleResources add async callback " + page);
            assetsHolder.addAssetsIsReadyCallback(new SessionAssetsIsReadyCallback() {
                @Override
                public void isReady() {
                    pageViewModel.slideLoadSuccess(slideIndex);
                    Log.e("slidesDownloader", "slideLoadSuccess async " + page);
                }
            });
            assetsHolder.downloadAssets();
        }
    }

    HashMap<SlideTaskKey, Long> slideErrorDelayed = new HashMap<>();


    private final Object pageViewModelsLock = new Object();
    List<IReaderSlideViewModel> pageViewModels = new ArrayList<>();

    private void slideLoaded(final SlideTaskKey key) {
        ContentIdAndType contentIdAndType = key.contentIdAndType;
        List<IReaderSlideViewModel> checkedPageViewModels = new ArrayList<>();
        synchronized (pageViewModelsLock) {
            for (IReaderSlideViewModel pageViewModel : pageViewModels) {
                if (pageViewModel.contentIdAndType().equals(contentIdAndType)) {
                    checkedPageViewModels.add(pageViewModel);
                }
            }
        }
        for (IReaderSlideViewModel pageViewModel : checkedPageViewModels) {
            checkBundleResources(pageViewModel, key.index);
        }
    }

    private SlideTaskKey getMaxPriorityPageTaskKey() {
        synchronized (slideTasksLock) {
            if (slideTasks == null || slideTasks.size() == 0) return null;
            if (firstPriority == null || secondPriority == null) return null;
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
