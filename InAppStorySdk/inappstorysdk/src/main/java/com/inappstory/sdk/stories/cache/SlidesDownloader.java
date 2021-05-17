package com.inappstory.sdk.stories.cache;

import android.os.Handler;
import android.util.Pair;

import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.callbacks.DownloadPageCallback;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
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

    void init() {
        try {
            if (handler != null) {
                handler.removeCallbacks(queuePageReadRunnable);
            }
        } catch (Exception e) {}
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
        }
    }


    private final Object pageTasksLock = new Object();
    private final ExecutorService loader = Executors.newFixedThreadPool(1);

    SlidesDownloader(DownloadPageCallback callback) {
        this.callback = callback;
        this.handler = new Handler();
        this.errorHandler = new Handler();
        handler.postDelayed(queuePageReadRunnable, 100);
    }

    void setCurrentSlide(int storyId, int slideIndex) {
        synchronized (pageTasksLock) {

        }
    }

    boolean checkIfPageLoaded(Pair<Integer, Integer> key) {
        if (pageTasks.get(key) != null && pageTasks.get(key).loadType == 2) {
            return true;
        } else {
            return false;
        }

    }

    private static final String VIDEO = "video";

    void addStoryPages(Story story, int loadType) throws Exception {
        synchronized (pageTasksLock) {
            int key = story.id;
            Set<Pair<Integer, Integer>> keys = pageTasks.keySet();
            int sz;
            if (loadType == 3) {
                sz = story.pages.size();
                for (Pair<Integer, Integer> taskKey : keys) {
                    if (pageTasks.get(taskKey).loadType != 0)
                        continue;
                    pageTasks.get(taskKey).priority += story.pages.size() + 4;
                }
                for (int i = 0; i < sz; i++) {
                    if (pageTasks.get(new Pair<>(key, i)) == null) {
                        StoryPageTask spt = new StoryPageTask();
                        spt.loadType = 0;
                        spt.urls = story.getSrcListUrls(i, null);
                        spt.videoUrls = story.getSrcListUrls(i, VIDEO);
                        spt.priority = i < 2 ? i : i + 4;
                        pageTasks.put(new Pair<>(key, i), spt);
                    } else {
                        pageTasks.get(new Pair<>(key, i)).priority = i < 2 ? i : i + 4;
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
                        spt.priority = i + 2;
                        pageTasks.put(new Pair<>(key, i), spt);
                    } else {
                        pageTasks.get(new Pair<>(key, i)).priority = i + 2;
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
                    try {
                        ArrayList<String> urls = new ArrayList<>();
                        ArrayList<String> videoUrls = new ArrayList<>();

                        synchronized (pageTasksLock) {
                            urls.addAll(pageTasks.get(key).urls);
                            videoUrls.addAll(pageTasks.get(key).videoUrls);
                        }
                        final String storyId = key.first != null ? Integer.toString(key.first) : null;
                        for (String url : urls) {
                            if (callback != null) callback.downloadFile(url, storyId, key.second);
                        }
                        for (String url : videoUrls) {
                            if (callback != null) callback.downloadFile(url, storyId, key.second);
                        }
                        synchronized (pageTasksLock) {
                            pageTasks.get(key).loadType = 2;
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
            });
        }
    };


    void reloadPage(int storyId, int index) {
        Pair<Integer, Integer> key = new Pair<>(storyId, index);
        synchronized (pageTasksLock) {
            if (pageTasks == null) pageTasks = new HashMap<>();
            if (pageTasks.get(key) != null && pageTasks.get(key).loadType == -1) {
                pageTasks.get(key).priority = 0;
                pageTasks.get(key).loadType = 0;
            }
        }
    }

    private Pair<Integer, Integer> getMaxPriorityPageTaskKey() {
        Pair<Integer, Integer> keyRes = null;
        int priority = 100000;
        synchronized (pageTasksLock) {
            if (pageTasks == null || pageTasks.size() == 0) return null;
            Set<Pair<Integer, Integer>> keys = pageTasks.keySet();
            for (Pair<Integer, Integer> key : keys) {
                if (pageTasks.get(key).loadType != 0) continue;
                if (pageTasks.get(key).priority < priority) {
                    keyRes = key;
                    priority = pageTasks.get(key).priority;
                }
            }
            return keyRes;
        }
    }

    private HashMap<Pair<Integer, Integer>, StoryPageTask> pageTasks = new HashMap<>();
}
