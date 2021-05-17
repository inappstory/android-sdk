package com.inappstory.sdk.stories.cache;

import android.os.Handler;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.Callback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.callbacks.DownloadStoryCallback;
import com.inappstory.sdk.stories.events.LoadFavStories;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.stories.ui.list.FavoriteImage;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.inappstory.sdk.stories.cache.StoryDownloadManager.EXPAND_STRING;

class StoryDownloader {
    StoryDownloader(DownloadStoryCallback callback) {
        this.callback = callback;
        this.handler = new Handler();
        this.errorHandler = new Handler();
        handler.postDelayed(queueStoryReadRunnable, 100);
    }

    void init() {
        try {
            if (handler != null) {
                handler.removeCallbacks(queueStoryReadRunnable);
            }
        } catch (Exception e) {}
        handler.postDelayed(queueStoryReadRunnable, 100);
    }

    private DownloadStoryCallback callback;

    private final ExecutorService loader = Executors.newFixedThreadPool(1);

    private final Object storyTasksLock = new Object();
    private HashMap<Integer, StoryTask> storyTasks = new HashMap<>();

    void cleanTasks() {
        synchronized (storyTasksLock) {
            storyTasks.clear();
        }
    }

    void destroy() {
        if (handler != null) {
            handler.removeCallbacks(queueStoryReadRunnable);
        }
    }

    boolean uploadAdditional() {
        synchronized (storyTasksLock) {
            if (storyTasks.isEmpty()) {
                return true;
            } else {
                for (Integer i : storyTasks.keySet()) {
                    if (getStoryLoadType(i) <= 1) return false;
                }
                return true;
            }
        }
    }

    void addStoryTask(int storyId, ArrayList<Integer> addIds) throws Exception {
        synchronized (storyTasksLock) {

            if (storyTasks == null) storyTasks = new HashMap<>();
            for (Integer storyTaskKey : storyTasks.keySet()) {
                storyTasks.get(storyTaskKey).priority += (1 + addIds.size());
                if (storyTasks.get(storyTaskKey).loadType > 0 && storyTasks.get(storyTaskKey).loadType != 3) {
                    storyTasks.get(storyTaskKey).loadType += 3;
                }
            }
            if (storyTasks.get(storyId) != null) {
                if (storyTasks.get(storyId).loadType != 3) {
                    storyTasks.get(storyId).loadType = 1;
                    storyTasks.get(storyId).priority = 0;
                } else {
                    return;
                }
            } else {
                storyTasks.put(storyId, new StoryTask() {{
                    priority = 0;
                    loadType = 1;
                }});
            }
            int i = 1;
            for (Integer storyTaskKey : addIds) {
                if (storyTasks.get(storyTaskKey) != null) {
                    storyTasks.get(storyTaskKey).priority = i;
                    if (storyTasks.get(storyTaskKey).loadType != 3) {
                        storyTasks.get(storyTaskKey).loadType = 4;
                    }
                } else {
                    StoryTask st = new StoryTask();
                    st.priority = i;
                    st.loadType = 4;
                    storyTasks.put(storyTaskKey, st);
                }
                i += 1;
            }

        }
    }


    private Integer getMaxPriorityStoryTaskKey() throws Exception {
        Integer keyRes = null;
        int priority = 100000;
        synchronized (storyTasksLock) {
            if (storyTasks == null || storyTasks.size() == 0) return null;
            Set<Integer> keys = storyTasks.keySet();
            for (Integer key : keys) {
                if (getStoryLoadType(key) != 1 && getStoryLoadType(key) != 4)
                    continue;
                if (storyTasks.get(key).priority < priority) {
                    keyRes = key;
                    priority = storyTasks.get(key).priority;
                }
            }
        }
        return keyRes;
    }

    void setStoryLoadType(int key, int loadType) {
        if (storyTasks.get(key) == null) return;
        storyTasks.get(key).loadType = loadType;
    }

    int getStoryLoadType(int key) {
        if (storyTasks.get(key) == null) return -5;
        return storyTasks.get(key).loadType;
    }


    private Handler handler;
    private Handler errorHandler;

    boolean reloadPage(int storyId, ArrayList<Integer> addIds) {
        synchronized (storyTasksLock) {
            if (storyTasks == null) storyTasks = new HashMap<>();
            if (getStoryLoadType(storyId) == -5 || getStoryLoadType(storyId) == -1) {
                try {
                    addStoryTask(storyId, addIds);
                } catch (Exception e) {

                }
                return false;
            }
        }
        return true;
    }

    private void loadStoryError(final int key) {
        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.CACHE));
        synchronized (storyTasksLock) {
            setStoryLoadType(key, -1);
            errorHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CsEventBus.getDefault().post(new PageTaskLoadErrorEvent(key, -1));
                }
            }, 300);
        }
    }

    private Runnable queueStoryReadRunnable = new Runnable() {
        boolean isRefreshing = false;

        @Override
        public void run() {
            Integer tKey = null;
            try {
                tKey = getMaxPriorityStoryTaskKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tKey == null) {
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            final Integer key = tKey;
            synchronized (storyTasksLock) {
                if (getStoryLoadType(key) == 4) {
                    setStoryLoadType(key, 5);
                } else if (getStoryLoadType(key) == 1) {
                    setStoryLoadType(key, 2);
                }
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
                                loadStoryError(key);
                            }
                        });
                }
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            loader.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    try {
                        Response response = NetworkClient.getApi().getStoryById(Integer.toString(key),
                                StatisticSession.getInstance().id, 1,
                                InAppStoryManager.getInstance().getApiKey(),
                                EXPAND_STRING).execute();
                        if (response.body != null) {
                            Story story = JsonParser.fromJson(response.body, Story.class);
                            int loadType;
                            synchronized (storyTasksLock) {
                                if (getStoryLoadType(key) < 4) {
                                    loadType = 3;
                                    setStoryLoadType(key, loadType);
                                } else {
                                    loadType = 6;
                                    setStoryLoadType(key, loadType);
                                }
                            }
                            if (story != null && callback != null) {
                                callback.onDownload(story, loadType);
                            }
                        }
                        handler.postDelayed(queueStoryReadRunnable, 200);
                        return null;
                    } catch (Throwable t) {
                        loadStoryError(key);
                        handler.postDelayed(queueStoryReadRunnable, 200);
                        return null;
                    }
                }
            });
        }
    };

    void loadStoryFavoriteList(final NetworkCallback<List<Story>> callback) {
        NetworkClient.getApi().getStories(StatisticSession.getInstance().id,
                InAppStoryManager.getInstance().getTestKey(), 1,
                null, "id, background_color, image",
                null).enqueue(callback);
    }

    void loadStoryList(final NetworkCallback<List<Story>> callback, final boolean isFavorite) {
        if (InAppStoryService.isConnected()) {
            SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
                @Override
                public void onSuccess() {
                    NetworkClient.getApi().getStories(StatisticSession.getInstance().id,
                            InAppStoryManager.getInstance().getTestKey(),
                            isFavorite ? 1 : 0,
                            InAppStoryManager.getInstance().getTagsString(),
                            InAppStoryManager.getInstance().getTestKey(), null)
                            .enqueue(callback);
                }

                @Override
                public void onError() {
                    CsEventBus.getDefault().post(new StoriesErrorEvent(NoConnectionEvent.LOAD_LIST));
                }
            });
        } else {
            CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.LOAD_LIST));
        }
    }
}
