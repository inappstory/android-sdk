package com.inappstory.sdk.stories.cache;

import android.os.Handler;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderType;
import com.inappstory.sdk.stories.api.models.ImagePlaceholderValue;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class SlidesDownloader {

    boolean uploadAdditional(boolean sync) {
        synchronized (pageTasksLock) {
            if (!pageTasks.isEmpty() && sync) {
                for (SlideTaskKey pair : pageTasks.keySet()) {
                    if (Objects.requireNonNull(pageTasks.get(pair)).loadType <= 1) return false;
                }
            }
        }
        return sync;
    }

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

    SlidesDownloader(DownloadPageCallback callback, StoryDownloadManager manager) {
        this.callback = callback;
        this.handler = new Handler();
        this.errorHandler = new Handler();
        this.manager = manager;
        handler.postDelayed(queuePageReadRunnable, 100);
    }

    void setCurrentSlide(int storyId, int slideIndex) {
        synchronized (pageTasksLock) {

        }
    }

    public void deleteTask(String remove) {

    }

    boolean checkIfPageLoaded(SlideTaskKey key) throws IOException {
        boolean remove = false;
        if (InAppStoryService.isNull()) return false;
        if (pageTasks.get(key) != null && pageTasks.get(key).loadType == 2) {
            for (String url : pageTasks.get(key).urls) {
                if (!InAppStoryService.getInstance().getCommonCache().hasKey(url)) {
                    remove = true;
                } else {
                    if (InAppStoryService.getInstance().getCommonCache().getFullFile(url) == null) {
                        synchronized (pageTasksLock) {
                            pageTasks.get(key).loadType = 0;
                        }
                        return false;
                    }
                }
            }
            for (String url : pageTasks.get(key).videoUrls) {
                if (!InAppStoryService.getInstance().getCommonCache().hasKey(url)) {
                    remove = true;
                } else {
                    if (InAppStoryService.getInstance().getCommonCache().getFullFile(url) == null) {
                        synchronized (pageTasksLock) {
                            pageTasks.get(key).loadType = 0;
                        }
                        return false;
                    }
                }
            }
            if (remove) {
                pageTasks.remove(key);
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    private static final String VIDEO = "video";
    private static final String IMG_PLACEHOLDER = "image-placeholder";

    List<SlideTaskKey> firstPriority = new ArrayList<>();
    List<SlideTaskKey> secondPriority = new ArrayList<>();

    //adjacent - for next and prev story
    void changePriority(Integer storyId, List<Integer> adjacents, Story.StoryType type) {
        synchronized (pageTasksLock) {
            for (int i = firstPriority.size() - 1; i >= 0; i--) {
                if (!secondPriority.contains(firstPriority.get(i))) {
                    secondPriority.add(0, firstPriority.get(i));
                }
            }
            firstPriority.clear();
            Story currentStory = manager.getStoryById(storyId, type);
            if (currentStory == null) return;
            int sc = currentStory.getSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(storyId, i, type);
                secondPriority.remove(kv);
                //       if (pageTasks.containsKey(kv) && pageTasks.get(kv).loadType != 0)
                //           continue;
                if (i == currentStory.lastIndex || i == currentStory.lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentStory.lastIndex) {
                firstPriority.add(0, new SlideTaskKey(storyId, currentStory.lastIndex, type));
                if (sc > currentStory.lastIndex + 1) {
                    firstPriority.add(1, new SlideTaskKey(storyId, currentStory.lastIndex + 1, type));
                }
            }
            int ind = Math.min(firstPriority.size(), 2);
            for (Integer adjacent : adjacents) {
                Story adjacentStory = manager.getStoryById(adjacent, type);
                if (adjacentStory.lastIndex < adjacentStory.getSlidesCount() - 1) {
                    SlideTaskKey nk = new SlideTaskKey(adjacent, adjacentStory.lastIndex + 1, type);
                    secondPriority.remove(nk);
                    firstPriority.add(ind, nk);
                }

                SlideTaskKey ck = new SlideTaskKey(adjacent, adjacentStory.lastIndex, type);
                secondPriority.remove(ck);
                firstPriority.add(ind, ck);
            }
        }
    }

    void changePriorityForSingle(Integer storyId, Story.StoryType type) {
        synchronized (pageTasksLock) {
            Story currentStory = manager.getStoryById(storyId, type);
            int sc = currentStory.getSlidesCount();
            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(storyId, i, type);
                firstPriority.remove(kv);
            }

            for (int i = 0; i < sc; i++) {
                SlideTaskKey kv = new SlideTaskKey(storyId, i, type);
                if (i == currentStory.lastIndex || i == currentStory.lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentStory.lastIndex) {
                firstPriority.add(0, new SlideTaskKey(storyId, currentStory.lastIndex, type));
                if (sc > currentStory.lastIndex + 1) {
                    firstPriority.add(1, new SlideTaskKey(storyId, currentStory.lastIndex + 1, type));
                }
            }
        }
    }

    void addStoryPages(Story story, int loadType, Story.StoryType type) throws Exception {
        Map<String, ImagePlaceholderValue> imgPlaceholders = new HashMap<>();
        if (InAppStoryService.isNotNull()) {
            imgPlaceholders.putAll(InAppStoryService.getInstance().getImagePlaceholdersValues());
        }
        synchronized (pageTasksLock) {
            int key = story.id;
            int sz;
            if (loadType == 3) {
                sz = story.pages.size();
                for (int i = 0; i < sz; i++) {
                    if (pageTasks.get(new SlideTaskKey(key, i, type)) == null) {
                        StoryPageTask spt = new StoryPageTask();
                        spt.loadType = 0;
                        spt.urls = story.getSrcListUrls(i, null);
                        spt.videoUrls = story.getSrcListUrls(i, VIDEO);
                        List<String> plNames = story.getPlaceholdersListNames(i);
                        for (String plName : plNames) {
                            ImagePlaceholderValue value = imgPlaceholders.get(plName);
                            if (value != null && value.getType() == ImagePlaceholderType.URL) {
                                spt.urls.add(value.getUrl());
                            }
                        }
                        pageTasks.put(new SlideTaskKey(key, i, type), spt);
                    }
                }
            } else {
                sz = 2;
                for (int i = 0; i < sz; i++) {
                    if (pageTasks.get(new SlideTaskKey(key, i, type)) == null) {
                        StoryPageTask spt = new StoryPageTask();
                        spt.loadType = 0;
                        spt.urls = story.getSrcListUrls(i, null);
                        spt.videoUrls = story.getSrcListUrls(i, VIDEO);
                        List<String> plNames = story.getPlaceholdersListNames(i);
                        for (String plName : plNames) {
                            ImagePlaceholderValue value = imgPlaceholders.get(plName);
                            if (value != null && value.getType() == ImagePlaceholderType.URL) {
                                spt.urls.add(value.getUrl());
                            }
                        }
                        pageTasks.put(new SlideTaskKey(key, i, type), spt);
                    }
                }

            }
        }
    }


    DownloadPageCallback callback;

    private void loadPageError(SlideTaskKey key) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().cacheError();
        }
        synchronized (pageTasksLock) {
            Objects.requireNonNull(pageTasks.get(key)).loadType = -1;
            callback.onError(key.storyId);
        }
    }

    private Handler handler;
    private Handler errorHandler;

    private Runnable queuePageReadRunnable = new Runnable() {
        boolean isRefreshing = false;

        @Override
        public void run() {

            final SlideTaskKey key = getMaxPriorityPageTaskKey();
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
                    return loadSlide(key);
                }
            });
        }
    };

    Object loadSlide(SlideTaskKey key) {
        try {
            ArrayList<String> urls = new ArrayList<>();
            ArrayList<String> videoUrls = new ArrayList<>();

            synchronized (pageTasksLock) {
                urls.addAll(pageTasks.get(key).urls);
                videoUrls.addAll(pageTasks.get(key).videoUrls);
            }
            final String storyId = key.storyId != null ? Integer.toString(key.storyId) : null;

            for (String url : urls) {
                if (callback != null) {
                    boolean success = callback.downloadFile(url, storyId, key.index);
                    synchronized (pageTasksLock) {
                        if (!success) pageTasks.get(key).urls.remove(url);
                    }
                }
            }
            for (String url : videoUrls) {
                if (callback != null) {
                    boolean success = callback.downloadFile(url, storyId, key.index);
                    synchronized (pageTasksLock) {
                        if (!success) pageTasks.get(key).videoUrls.remove(url);
                    }
                }
            }
            synchronized (pageTasksLock) {
                pageTasks.get(key).loadType = 2;
            }
            manager.slideLoaded(key.storyId, key.index, key.storyType);
            handler.postDelayed(queuePageReadRunnable, 200);
            return null;

        } catch (Throwable t) {
            loadPageError(key);
            handler.postDelayed(queuePageReadRunnable, 200);
            return null;
        }
    }

    void reloadPage(int storyId, int index, Story.StoryType type) {
        SlideTaskKey key = new SlideTaskKey(storyId, index, type);
        synchronized (pageTasksLock) {
            if (pageTasks == null) pageTasks = new HashMap<>();
            StoryPageTask task = pageTasks.get(key);
            if (task != null && task.loadType == -1) {
                task.loadType = 0;
            }
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

    private HashMap<SlideTaskKey, StoryPageTask> pageTasks = new HashMap<>();
}
