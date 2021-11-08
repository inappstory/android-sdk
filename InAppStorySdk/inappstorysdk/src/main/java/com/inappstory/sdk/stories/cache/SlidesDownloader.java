package com.inappstory.sdk.stories.cache;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class SlidesDownloader {

    boolean uploadAdditional(boolean sync) {
        synchronized (pageTasksLock) {
            if (!pageTasks.isEmpty() && sync) {
                for (Pair<Integer, Integer> pair : pageTasks.keySet()) {
                    if (pageTasks.get(pair).loadType <= 1) return false;
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

    boolean checkIfPageLoaded(Pair<Integer, Integer> key) {
        boolean remove = false;
        if (pageTasks.get(key) != null && pageTasks.get(key).loadType == 2) {
            for (String url : pageTasks.get(key).urls) {
                if (!InAppStoryService.getInstance().getCommonCache().hasKey(url)) {
                    remove = true;
                }
            }
            for (String url : pageTasks.get(key).videoUrls) {
                if (!InAppStoryService.getInstance().getCommonCache().hasKey(url)) {
                    remove = true;
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

    List<Pair<Integer, Integer>> firstPriority = new ArrayList<>();
    List<Pair<Integer, Integer>> secondPriority = new ArrayList<>();

    //adjacent - for next and prev story
    void changePriority(Integer storyId, List<Integer> adjacents) {
       // Log.e("changePriority", "multiple");
        synchronized (pageTasksLock) {
            for (int i = firstPriority.size() - 1; i >= 0; i--) {
                if (!secondPriority.contains(firstPriority.get(i))) {
                    secondPriority.add(0, firstPriority.get(i));
                }
            }
            firstPriority.clear();
            Story currentStory = manager.getStoryById(storyId);
            if (currentStory == null) return;
            int sc = currentStory.slidesCount;
            for (int i = 0; i < sc; i++) {
                Pair<Integer, Integer> kv = new Pair<>(storyId, i);
                secondPriority.remove(kv);
         //       if (pageTasks.containsKey(kv) && pageTasks.get(kv).loadType != 0)
         //           continue;
                if (i == currentStory.lastIndex || i == currentStory.lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentStory.lastIndex) {
                firstPriority.add(0, new Pair<>(storyId, currentStory.lastIndex));
                if (sc > currentStory.lastIndex + 1) {
                    firstPriority.add(1, new Pair<>(storyId, currentStory.lastIndex+1));
                }
            }
            int ind = Math.min(firstPriority.size(), 2);
            for (Integer adjacent : adjacents) {
                Story adjacentStory = manager.getStoryById(adjacent);
                if (adjacentStory == null) continue;
                if (adjacentStory.lastIndex < adjacentStory.slidesCount - 1) {
                    Pair<Integer, Integer> nk = new Pair<>(adjacent, adjacentStory.lastIndex + 1);
                    secondPriority.remove(nk);

        //            if (!(pageTasks.containsKey(nk) && pageTasks.get(nk).loadType != 0))
                        firstPriority.add(ind, nk);
                }

                Pair<Integer, Integer> ck = new Pair<>(adjacent, adjacentStory.lastIndex);
                secondPriority.remove(ck);

           //     if (!(pageTasks.containsKey(ck) && pageTasks.get(ck).loadType != 0))
                    firstPriority.add(ind, ck);
            }
        }
    }

    void changePriorityForSingle(Integer storyId) {
      //  Log.e("changePriority", "single");
        synchronized (pageTasksLock) {
            Story currentStory = manager.getStoryById(storyId);
            if (currentStory == null) return;
            int sc = currentStory.slidesCount;
            for (int i = 0; i < sc; i++) {
                Pair<Integer, Integer> kv = new Pair<>(storyId, i);
                firstPriority.remove(kv);
            }

            for (int i = 0; i < sc; i++) {
                Pair<Integer, Integer> kv = new Pair<>(storyId, i);
           //     if (pageTasks.containsKey(kv) && pageTasks.get(kv).loadType != 0)
           //         continue;
                if (i == currentStory.lastIndex || i == currentStory.lastIndex + 1)
                    continue;
                firstPriority.add(kv);
            }
            if (sc > currentStory.lastIndex) {
                firstPriority.add(0, new Pair<>(storyId, currentStory.lastIndex));
                if (sc > currentStory.lastIndex + 1) {
                    firstPriority.add(1, new Pair<>(storyId, currentStory.lastIndex+1));
                }
            }
        }
    }

    void addStoryPages(Story story, int loadType) throws Exception {
        synchronized (pageTasksLock) {
            int key = story.id;
            int sz;
            if (loadType == 3) {
                sz = story.pages.size();
                for (int i = 0; i < sz; i++) {
                    if (pageTasks.get(new Pair<>(key, i)) == null) {
                        StoryPageTask spt = new StoryPageTask();
                        spt.loadType = 0;
                        spt.urls = story.getSrcListUrls(i, null);
                        spt.videoUrls = story.getSrcListUrls(i, VIDEO);
                        pageTasks.put(new Pair<>(key, i), spt);
                    }
                }
            } else {
                sz = 2;
                for (int i = 0; i < sz; i++) {
                    if (pageTasks.get(new Pair<>(key, i)) == null) {
                        StoryPageTask spt = new StoryPageTask();
                        spt.loadType = 0;
                        spt.urls = story.getSrcListUrls(i, null);
                        spt.videoUrls = story.getSrcListUrls(i, VIDEO);
                        pageTasks.put(new Pair<>(key, i), spt);
                    }
                }

            }
        }
    }

    DownloadPageCallback callback;

    private void loadPageError(final int storyId, final int index) {
        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.CACHE));
        synchronized (pageTasksLock) {
            pageTasks.get(new Pair<>(storyId, index)).loadType = -1;
            errorHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CsEventBus.getDefault().post(new PageTaskLoadErrorEvent(storyId, index));
                }
            }, 300);
        }
    }

    private Handler handler;
    private Handler errorHandler;

    private Runnable queuePageReadRunnable = new Runnable() {
        boolean isRefreshing = false;

        @Override
        public void run() {

            final Pair<Integer, Integer> key = getMaxPriorityPageTaskKey();
            if (key == null) {
                handler.postDelayed(queuePageReadRunnable, 100);
                return;
            }
            synchronized (pageTasksLock) {
              //  firstPriority.remove(key);
             //   secondPriority.remove(key);
            }
            if (StatisticSession.needToUpdate()) {
                if (!isRefreshing) {
                    isRefreshing = true;
                    if (SessionManager.getInstance() != null)
                        SessionManager.getInstance().openSession(new OpenSessionCallback() {
                            @Override
                            public void onSuccess() {
                                isRefreshing = false;
                            }

                            @Override
                            public void onError() {
                                loadPageError(key.first, key.second);
                            }
                        });
                }
                handler.postDelayed(queuePageReadRunnable, 100);
                return;
            }
            synchronized (pageTasksLock) {
                pageTasks.get(key).loadType = 1;
            }
            loader.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    return loadSlide(key);
                }
            });
        }
    };

    Object loadSlide(Pair<Integer, Integer> key) {
        try {
            ArrayList<String> urls = new ArrayList<>();
            ArrayList<String> videoUrls = new ArrayList<>();

            synchronized (pageTasksLock) {
                urls.addAll(pageTasks.get(key).urls);
                videoUrls.addAll(pageTasks.get(key).videoUrls);
            }
            final String storyId = key.first != null ? Integer.toString(key.first) : null;
            for (String url : urls) {
                if (callback != null) {
                    boolean success = callback.downloadFile(url, storyId, key.second);
                    synchronized (pageTasksLock) {
                        if (!success) pageTasks.get(key).urls.remove(url);
                    }
                }
            }
            for (String url : videoUrls) {
                if (callback != null) {
                    boolean success = callback.downloadFile(url, storyId, key.second);
                    synchronized (pageTasksLock) {
                        if (!success) pageTasks.get(key).videoUrls.remove(url);
                    }
                }
            }
            synchronized (pageTasksLock) {
                pageTasks.get(key).loadType = 2;
                //Log.e("changePriority", key + " ");
            }
            CsEventBus.getDefault().post(new PageTaskLoadedEvent(key.first, key.second));
            handler.postDelayed(queuePageReadRunnable, 200);
            return null;

        } catch (Throwable t) {
            loadPageError(key.first, key.second);
            handler.postDelayed(queuePageReadRunnable, 200);
            return null;
        }
    }

    void reloadPage(int storyId, int index) {
        Pair<Integer, Integer> key = new Pair<>(storyId, index);
        synchronized (pageTasksLock) {
            if (pageTasks == null) pageTasks = new HashMap<>();
            if (pageTasks.get(key) != null && pageTasks.get(key).loadType == -1) {
                pageTasks.get(key).loadType = 0;
            }
        }
    }

    private Pair<Integer, Integer> getMaxPriorityPageTaskKey() {
        synchronized (pageTasksLock) {
            if (pageTasks == null || pageTasks.size() == 0) return null;
            if (firstPriority == null || secondPriority == null) return null;
            for (Pair<Integer, Integer> key : firstPriority) {
                if (!pageTasks.containsKey(key)) continue;
                if (pageTasks.get(key).loadType != 0) continue;
                return key;
            }
            for (Pair<Integer, Integer> key : secondPriority) {
                if (!pageTasks.containsKey(key)) continue;
                if (pageTasks.get(key).loadType != 0) continue;
                return key;
            }
            return null;
        }
    }

    private HashMap<Pair<Integer, Integer>, StoryPageTask> pageTasks = new HashMap<>();
}
