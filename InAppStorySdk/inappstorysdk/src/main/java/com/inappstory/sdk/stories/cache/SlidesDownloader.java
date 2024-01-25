package com.inappstory.sdk.stories.cache;

import android.util.Pair;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

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
        loopedExecutor.init(queuePageReadRunnable);
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

    SlidesDownloader(DownloadPageCallback callback, StoryDownloadManager manager) {
        this.callback = callback;
        this.manager = manager;
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
        boolean remove = false;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return 0;
        LruDiskCache cache = service.getCommonCache();
        SlideTask slideTask = pageTasks.get(key);
        if (slideTask != null) {
            if (slideTask.loadType == 2) {
                ArrayList<String> allUrls = new ArrayList<>();
                allUrls.addAll(slideTask.urls);
                allUrls.addAll(slideTask.videoUrls);
                for (String url : allUrls) {
                    String croppedUrl = Downloader.deleteQueryArgumentsFromUrlOld(url, true);
                    if (!cache.hasKey(croppedUrl)) {
                        remove = true;
                    } else {
                        if (cache.getFullFile(croppedUrl) == null) {
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

    private static final String VIDEO = "video";
    private static final String IMG_PLACEHOLDER = "image-placeholder";

    List<SlideTaskData> firstPriority = new ArrayList<>();
    List<SlideTaskData> secondPriority = new ArrayList<>();

    //adjacent - for next and prev story
    boolean changePriority(Integer storyId, List<Integer> adjacents, Story.StoryType type) {
        synchronized (pageTasksLock) {
            for (int i = firstPriority.size() - 1; i >= 0; i--) {
                if (!secondPriority.contains(firstPriority.get(i))) {
                    secondPriority.add(0, firstPriority.get(i));
                }
            }
            firstPriority.clear();
            Story currentStory = manager.getStoryById(storyId, type);
            if (currentStory == null) return false;
            int sc = currentStory.getSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskData kv = new SlideTaskData(storyId, i, type);
                secondPriority.remove(kv);
                //       if (pageTasks.containsKey(kv) && pageTasks.get(kv).loadType != 0)
                //           continue;
                if (i == currentStory.lastIndex || i == currentStory.lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentStory.lastIndex) {
                firstPriority.add(0, new SlideTaskData(storyId, currentStory.lastIndex, type));
                if (sc > currentStory.lastIndex + 1) {
                    firstPriority.add(1, new SlideTaskData(storyId, currentStory.lastIndex + 1, type));
                }
            }
            int ind = Math.min(firstPriority.size(), 2);
            for (Integer adjacent : adjacents) {
                Story adjacentStory = manager.getStoryById(adjacent, type);
                if (adjacentStory.lastIndex < adjacentStory.getSlidesCount() - 1) {
                    SlideTaskData nk = new SlideTaskData(adjacent, adjacentStory.lastIndex + 1, type);
                    secondPriority.remove(nk);
                    firstPriority.add(ind, nk);
                }

                SlideTaskData ck = new SlideTaskData(adjacent, adjacentStory.lastIndex, type);
                secondPriority.remove(ck);
                firstPriority.add(ind, ck);
            }
        }
        return true;
    }

    void changePriorityForSingle(Integer storyId, Story.StoryType type) {
        synchronized (pageTasksLock) {
            Story currentStory = manager.getStoryById(storyId, type);
            int sc = currentStory.getSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskData kv = new SlideTaskData(storyId, i, type);
                firstPriority.remove(kv);
            }

            for (int i = 0; i < sc; i++) {
                SlideTaskData kv = new SlideTaskData(storyId, i, type);
                if (i == currentStory.lastIndex || i == currentStory.lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentStory.lastIndex) {
                firstPriority.add(0, new SlideTaskData(storyId, currentStory.lastIndex, type));
                if (sc > currentStory.lastIndex + 1) {
                    firstPriority.add(1, new SlideTaskData(storyId, currentStory.lastIndex + 1, type));
                }
            }
        }
    }

    void addStoryPages(Story story, int loadType, Story.StoryType type) throws Exception {
        Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> imgPlaceholders = new HashMap<>();
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            imgPlaceholders.putAll(service.getImagePlaceholdersValuesWithDefaults());
        }
        synchronized (pageTasksLock) {
            int key = story.id;
            int sz;
            if (loadType == 3) {
                sz = story.pages.size();
            } else {
                sz = 2;
            }
            for (int i = 0; i < sz; i++) {
                if (pageTasks.get(new SlideTaskData(key, i, type)) == null) {
                    SlideTask spt = new SlideTask();
                    spt.loadType = 0;
                    spt.urls = story.getSrcListUrls(i, null);
                    spt.videoUrls = story.getSrcListUrls(i, VIDEO);
                    List<String> plNames = story.getPlaceholdersListNames(i);
                    for (String plName : plNames) {
                        Pair<ImagePlaceholderValue, ImagePlaceholderValue> value =
                                imgPlaceholders.get(plName);
                        if (value != null
                                && value.first != null
                                && value.first.getType() == ImagePlaceholderType.URL) {
                            if (value.second != null
                                    && value.second.getType() == ImagePlaceholderType.URL) {
                                spt.urlsWithAlter.add(
                                        new UrlWithAlter(
                                                value.first.getUrl(),
                                                value.second.getUrl()
                                        )
                                );
                            } else {
                                spt.urlsWithAlter.add(
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


    DownloadPageCallback callback;

    private void loadPageError(SlideTaskData key) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().cacheError();
        }
        synchronized (pageTasksLock) {
            Objects.requireNonNull(pageTasks.get(key)).loadType = -1;
            callback.onSlideError(key);
        }
        loopedExecutor.freeExecutor();
    }


    private final Runnable queuePageReadRunnable = new Runnable() {

        @Override
        public void run() {

            final SlideTaskData key = getMaxPriorityPageTaskKey();
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

    Object loadSlide(SlideTaskData slideTaskData) {
        try {
            ArrayList<String> allUrls = new ArrayList<>();
            SlideTask slideTask = pageTasks.get(slideTaskData);
            if (slideTask == null) {
                loopedExecutor.freeExecutor();
                return null;
            }
            synchronized (pageTasksLock) {
                allUrls.addAll(slideTask.videoUrls);
                allUrls.addAll(slideTask.urls);
            }
            DownloadPageFileStatus status = DownloadPageFileStatus.SUCCESS;
            for (String url : allUrls) {
                if (callback != null) {
                    status = callback.downloadFile(new UrlWithAlter(url), slideTaskData);
                    if (status != DownloadPageFileStatus.SUCCESS)
                        break;
                }
            }
            if (status != DownloadPageFileStatus.SUCCESS) {
                loadPageError(slideTaskData);
                return null;
            }
            for (UrlWithAlter urlWithAlter : slideTask.urlsWithAlter) {
                if (callback != null) {
                    callback.downloadFile(urlWithAlter, slideTaskData);
                }
            }
            synchronized (pageTasksLock) {
                slideTask.loadType = 2;
            }
            manager.slideLoaded(slideTaskData);
            loopedExecutor.freeExecutor();
            return null;

        } catch (Throwable t) {
            loadPageError(slideTaskData);
            return null;
        }
    }

    void reloadPage(int storyId, int index, Story.StoryType type) {
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
