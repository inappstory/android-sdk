package com.inappstory.sdk.stories.cache;

import android.os.Handler;
import android.util.Pair;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.ImagePlaceholderMappingObjectDTO;
import com.inappstory.sdk.core.repository.stories.dto.ResourceMappingObjectDTO;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.callbacks.CallbackManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class SlidesDownloader {
    StoryDownloadManager manager;

    void init() {
        try {
            if (handler != null) {
                handler.removeCallbacks(queuePageReadRunnable);
            }
        } catch (Exception ignored) {
        }
        handler.postDelayed(queuePageReadRunnable, 100);
    }

    void destroy() {
        if (handler != null) {
            handler.removeCallbacks(queuePageReadRunnable);
        }
    }

    void cleanTasks() {
        synchronized (pageTasksLock) {
            pageTasks.clear();
            firstPriority.clear();
            secondPriority.clear();
        }
    }


    private final Object pageTasksLock = new Object();
    private final ExecutorService loader = Executors.newFixedThreadPool(1);

    SlidesDownloader(IDownloadPageFile callback, StoryDownloadManager manager) {
        this.callback = callback;
        this.handler = new Handler();
        this.errorHandler = new Handler();
        this.manager = manager;
        handler.postDelayed(queuePageReadRunnable, 100);
    }

    public void removeSlideTasks(StoryTaskData storyTaskData) {
        synchronized (pageTasksLock) {
            Iterator<Map.Entry<SlideTaskData, SlideTask>> i = pageTasks.entrySet().iterator();
            Map.Entry<SlideTaskData, SlideTask> key;
            while (i.hasNext()) {
                key = i.next();
                if (Objects.equals(key.getKey().storyId, storyTaskData.storyId)
                        && key.getKey().storyType == storyTaskData.storyType
                ) {
                    i.remove();
                }
            }
        }
    }

    int checkIfPageLoaded(SlideTaskData key) throws IOException { //0 - not loaded, 1 - loaded, -1 - loaded with error
        SlideTask slideTask = pageTasks.get(key);
        if (slideTask != null) {
            if (slideTask.loadType == 2) {
                for (ResourceMappingObjectDTO resource : slideTask.resources) {
                    if (IASCore.getInstance().filesRepository.getLocalStoryFile(
                            resource.getUrl()
                    ) == null) {
                        synchronized (pageTasksLock) {
                            slideTask.loadType = 0;
                        }
                        return 0;
                    }
                }
                return 1;
            } else if (slideTask.loadType == -1) {
                return -1;
            }
        }
        return 0;
    }

    private static final String VIDEO = "video";
    private static final String IMG_PLACEHOLDER = "image-placeholder";

    List<SlideTaskData> firstPriority = new ArrayList<>();
    List<SlideTaskData> secondPriority = new ArrayList<>();

    //adjacent - for next and prev story
    void changePriority(Integer storyId, List<Integer> adjacents, StoryType type) {
        IStoriesRepository storiesRepository = IASCore.getInstance().getStoriesRepository(type);
        IPreviewStoryDTO currentStory = storiesRepository.getStoryPreviewById(storyId);
        int lastIndex = storiesRepository.getStoryLastIndex(storyId);
        synchronized (pageTasksLock) {
            for (int i = firstPriority.size() - 1; i >= 0; i--) {
                if (!secondPriority.contains(firstPriority.get(i))) {
                    secondPriority.add(0, firstPriority.get(i));
                }
            }
            firstPriority.clear();
            if (currentStory == null) return;

            int sc = currentStory.getSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskData kv = new SlideTaskData(storyId, i, type);
                secondPriority.remove(kv);
                if (i == storiesRepository.getStoryLastIndex(storyId) || i == lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > lastIndex) {
                firstPriority.add(0, new SlideTaskData(storyId, lastIndex, type));
                if (sc > lastIndex + 1) {
                    firstPriority.add(1, new SlideTaskData(storyId, lastIndex + 1, type));
                }
            }
            int ind = Math.min(firstPriority.size(), 2);
            for (Integer adjacent : adjacents) {
                IPreviewStoryDTO adjacentStory = storiesRepository.getStoryPreviewById(adjacent);
                int adjacentLastIndex = storiesRepository.getStoryLastIndex(storyId);
                if (adjacentLastIndex < adjacentStory.getSlidesCount() - 1) {
                    SlideTaskData nk = new SlideTaskData(adjacent, adjacentLastIndex + 1, type);
                    secondPriority.remove(nk);
                    firstPriority.add(ind, nk);
                }

                SlideTaskData ck = new SlideTaskData(adjacent, adjacentLastIndex, type);
                secondPriority.remove(ck);
                firstPriority.add(ind, ck);
            }
        }
    }

    void changePriorityForSingle(Integer storyId, StoryType type) {
        synchronized (pageTasksLock) {
            IStoriesRepository repository = IASCore.getInstance().getStoriesRepository(type);
            IStoryDTO currentStory = repository.getStoryById(storyId);
            int lastIndex = repository.getStoryLastIndex(storyId);
            int sc = currentStory.getSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskData kv = new SlideTaskData(storyId, i, type);
                firstPriority.remove(kv);
            }

            for (int i = 0; i < sc; i++) {
                SlideTaskData kv = new SlideTaskData(storyId, i, type);
                if (i == lastIndex || i == lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > lastIndex) {
                firstPriority.add(0, new SlideTaskData(storyId, lastIndex, type));
                if (sc > lastIndex + 1) {
                    firstPriority.add(1, new SlideTaskData(storyId, lastIndex + 1, type));
                }
            }
        }
    }

    void addStoryPages(IStoryDTO story, int loadType, StoryType type) throws Exception {
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders =
                new HashMap<>(IASCore.getInstance().getImagePlaceholdersValuesWithDefaults());
        synchronized (pageTasksLock) {
            int key = story.getId();
            int sz;
            if (loadType == 3) {
                sz = story.getPages().size();
            } else {
                sz = 2;
            }
            for (int i = 0; i < sz; i++) {
                if (pageTasks.get(new SlideTaskData(key, i, type)) == null) {
                    SlideTask spt = new SlideTask();
                    spt.loadType = 0;
                    spt.resources = new ArrayList<>(story.getSrcList(i));
                    List<ImagePlaceholderMappingObjectDTO> placeholdersList =
                            story.getImagePlaceholdersList(i);
                    for (ImagePlaceholderMappingObjectDTO placeholder : placeholdersList) {
                        Pair<ImagePlaceholderValue, ImagePlaceholderValue> value =
                                imgPlaceholders.get(placeholder.getKey());
                        if (value != null
                                && value.first != null
                                && value.first.getType() == ImagePlaceholderType.URL) {
                            if (value.second != null
                                    && value.second.getType() == ImagePlaceholderType.URL) {
                                spt.placeholders.add(
                                        new UrlWithAlter(
                                                value.first.getUrl(),
                                                value.second.getUrl()
                                        )
                                );
                            } else {
                                spt.placeholders.add(
                                        new UrlWithAlter(
                                                value.first.getUrl()
                                        )
                                );
                            }
                        }
                    }
                    pageTasks.put(new SlideTaskData(key, i, type), spt);
                }
            }
        }
    }


    IDownloadPageFile callback;

    private void loadPageError(SlideTaskData key) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().cacheError();
        }
        handler.postDelayed(queuePageReadRunnable, 200);
        synchronized (pageTasksLock) {
            Objects.requireNonNull(pageTasks.get(key)).loadType = -1;
            if (callback != null)
                callback.onSlideError(key);
        }
    }

    private Handler handler;
    private Handler errorHandler;

    private Runnable queuePageReadRunnable = new Runnable() {
        boolean isRefreshing = false;

        @Override
        public void run() {

            final SlideTaskData key = getMaxPriorityPageTaskKey();
            if (key == null) {
                handler.postDelayed(queuePageReadRunnable, 100);
                return;
            }
            synchronized (pageTasksLock) {
                Objects.requireNonNull(pageTasks.get(key)).loadType = 1;
            }
            loader.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    try {
                        return loadSlide(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception();
                    }
                }
            });
        }
    };

    private void downloadUrls(
            final SlideTaskData slideTaskData,
            List<UrlWithAlter> urlWithAlters,
            final ISlideTaskFilesLoad loadCallback
    ) {
        final Iterator<UrlWithAlter> urlIterator = urlWithAlters.iterator();
        if (!urlIterator.hasNext()) {
            loadCallback.onLoaded();
            return;
        }
        IDownloadPageFileCallback fileCallback = new IDownloadPageFileCallback() {
            @Override
            public void download(UrlWithAlter source, DownloadPageFileStatus status) {
                if (!source.isSkippable() && status != DownloadPageFileStatus.SUCCESS) {
                    loadPageError(slideTaskData);
                } else {
                    if (urlIterator.hasNext()) {
                        callback.downloadFile(
                                urlIterator.next(),
                                this
                        );
                    } else {
                        loadCallback.onLoaded();
                    }
                }
            }
        };
        if (callback != null) {
            callback.downloadFile(
                    urlIterator.next(),
                    fileCallback
            );
        }
    }

    Object loadSlide(final SlideTaskData slideTaskData) {
        try {
            ArrayList<UrlWithAlter> allUrls = new ArrayList<>();
            final SlideTask slideTask;
            synchronized (pageTasksLock) {
                slideTask = pageTasks.get(slideTaskData);
            }
            if (slideTask == null) {
                handler.postDelayed(queuePageReadRunnable, 100);
                return null;
            }
            for (ResourceMappingObjectDTO resource : slideTask.resources) {
                allUrls.add(new UrlWithAlter(resource.getUrl()));
            }
            allUrls.addAll(slideTask.placeholders);
            downloadUrls(
                    slideTaskData,
                    allUrls,
                    new ISlideTaskFilesLoad() {
                        @Override
                        public void onLoaded() {
                            synchronized (pageTasksLock) {
                                slideTask.loadType = 2;
                            }
                            handler.postDelayed(queuePageReadRunnable, 100);
                            manager.slideLoaded(slideTaskData);
                        }
                    }
            );
            return null;
        } catch (Throwable t) {
            loadPageError(slideTaskData);
            return null;
        }
    }

    void reloadPage(int storyId, int index, StoryType type) {
        SlideTaskData key = new SlideTaskData(storyId, index, type);
        synchronized (pageTasksLock) {
            if (pageTasks == null) pageTasks = new HashMap<>();
            SlideTask task = pageTasks.get(key);
            if (task != null && task.loadType == -1) {
                task.loadType = 0;
            }
        }
    }

    private SlideTaskData getMaxPriorityPageTaskKey() {
        synchronized (pageTasksLock) {
            if (pageTasks == null || pageTasks.size() == 0) return null;
            if (firstPriority == null || secondPriority == null) return null;
            for (SlideTaskData key : firstPriority) {
                if (!pageTasks.containsKey(key)) continue;
                if (Objects.requireNonNull(pageTasks.get(key)).loadType != 0) continue;
                return key;
            }
            for (SlideTaskData key : secondPriority) {
                if (!pageTasks.containsKey(key)) continue;
                if (Objects.requireNonNull(pageTasks.get(key)).loadType != 0) continue;
                return key;
            }
            return null;
        }
    }

    private HashMap<SlideTaskData, SlideTask> pageTasks = new HashMap<>();
}
