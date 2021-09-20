package com.inappstory.sdk.stories.cache;

import android.content.Intent;
import android.os.Handler;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.events.NoConnectionEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.inappstory.sdk.stories.cache.StoryDownloadManager.EXPAND_STRING;
import static java.util.UUID.randomUUID;

class StoryDownloader {
    StoryDownloader(DownloadStoryCallback callback, StoryDownloadManager manager) {
        this.callback = callback;
        this.handler = new Handler();
        this.errorHandler = new Handler();
        this.manager = manager;
        handler.postDelayed(queueStoryReadRunnable, 100);
    }

    StoryDownloadManager manager;

    void init() {
        try {
            if (handler != null) {
                handler.removeCallbacks(queueStoryReadRunnable);
            }
        } catch (Exception e) {
        }
        handler.postDelayed(queueStoryReadRunnable, 100);
    }

    private DownloadStoryCallback callback;

    private final ExecutorService loader = Executors.newFixedThreadPool(1);

    private final Object storyTasksLock = new Object();
    private HashMap<Integer, StoryTask> storyTasks = new HashMap<>();

    void cleanTasks() {
        synchronized (storyTasksLock) {
            storyTasks.clear();
            firstPriority.clear();
            secondPriority.clear();
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

    ArrayList<Integer> firstPriority = new ArrayList<>();
    ArrayList<Integer> secondPriority = new ArrayList<>();

    void changePriority(int storyId, ArrayList<Integer> addIds) {
        if (secondPriority.contains(storyId)) secondPriority.remove(storyId);
        for (Integer id : addIds) {
            if (secondPriority.contains(id)) secondPriority.remove(id);
        }
        for (Integer id : firstPriority) {
            if (!secondPriority.contains(id))
                secondPriority.add(id);
        }
        firstPriority.clear();
        firstPriority.add(storyId);
        firstPriority.addAll(addIds);
    }

    void addStoryTask(int storyId, ArrayList<Integer> addIds) throws Exception {
        synchronized (storyTasksLock) {

            if (storyTasks == null) storyTasks = new HashMap<>();
            for (Integer storyTaskKey : storyTasks.keySet()) {
                if (storyTasks.get(storyTaskKey).loadType > 0 && storyTasks.get(storyTaskKey).loadType != 3) {
                    storyTasks.get(storyTaskKey).loadType += 3;
                }
            }
            if (storyTasks.get(storyId) != null) {
                if (storyTasks.get(storyId).loadType != 3) {
                    storyTasks.get(storyId).loadType = 1;
                } else {
                    return;
                }
            } else {
                storyTasks.put(storyId, new StoryTask(1));
            }
            for (Integer storyTaskKey : addIds) {
                if (storyTasks.get(storyTaskKey) != null) {
                    if (storyTasks.get(storyTaskKey).loadType != 3) {
                        storyTasks.get(storyTaskKey).loadType = 4;
                    }
                } else {
                    StoryTask st = new StoryTask(4);
                    storyTasks.put(storyTaskKey, st);
                }
            }
            changePriority(storyId, addIds);
        }
    }


    private Integer getMaxPriorityStoryTaskKey() throws Exception {
        synchronized (storyTasksLock) {
            if (storyTasks == null || storyTasks.size() == 0) return null;
            if (firstPriority == null || secondPriority == null) return null;
            for (int key : firstPriority) {
                if (getStoryLoadType(key) != 1 && getStoryLoadType(key) != 4)
                    continue;
                return key;
            }
            for (int key : secondPriority) {
                if (getStoryLoadType(key) != 1 && getStoryLoadType(key) != 4)
                    continue;
                return key;
            }
            return null;
        }
    }

    void setStoryLoadType(int key, int loadType) {
        if (!storyTasks.containsKey(key)) return;
        storyTasks.get(key).loadType = loadType;
    }

    int getStoryLoadType(int key) {
        if (!storyTasks.containsKey(key)) return -5;
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
            if (storyTasks != null)
                storyTasks.remove(key);
            if (firstPriority != null)
                firstPriority.remove(key);
            if (secondPriority != null)
                secondPriority.remove(key);
            setStoryLoadType(key, -1);
            callback.onError(key);
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
                    loadStory(key);
                    return null;
                }
            });
        }
    };

    void loadStory(Integer key) {
        try {
            String storyUID = ProfilingManager.getInstance().addTask("api_story");
            Response response = NetworkClient.getApi().getStoryById(Integer.toString(key),
                    1,
                    EXPAND_STRING).execute();
            ProfilingManager.getInstance().setReady(storyUID);
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
                    if (firstPriority.contains(key)) firstPriority.remove(key);
                    if (secondPriority.contains(key)) firstPriority.remove(key);
                }
                if (story != null) {
                    if (callback != null) {
                        callback.onDownload(story, loadType);
                    }
                }
            } else if (response.errorBody != null) {
                loadStoryError(key);
            }
            handler.postDelayed(queueStoryReadRunnable, 200);
        } catch (Throwable t) {
            loadStoryError(key);
            handler.postDelayed(queueStoryReadRunnable, 200);
        }
    }

    void loadStoryFavoriteList(final NetworkCallback<List<Story>> callback) {
        NetworkClient.getApi().getStories(
                ApiSettings.getInstance().getTestKey(), 1,
                null, "id, background_color, image").enqueue(callback);
    }

    void loadStoryList(final NetworkCallback<List<Story>> callback, final boolean isFavorite) {
        if (InAppStoryService.isNull()) {
            CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
            return;
        }
        if (InAppStoryService.isConnected()) {
            SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
                @Override
                public void onSuccess() {
                    final String loadStoriesUID = ProfilingManager.getInstance().addTask(isFavorite
                            ? "api_favorite_list" : "api_story_list");
                    NetworkClient.getApi().getStories(
                            ApiSettings.getInstance().getTestKey(),
                            isFavorite ? 1 : 0,
                            InAppStoryService.getInstance().getTagsString(),
                            null)
                            .enqueue(new LoadListCallback() {
                                @Override
                                public void onSuccess(List<Story> response) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    callback.onSuccess(response);
                                }

                                @Override
                                public void onTimeout() {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    super.onTimeout();
                                    callback.onTimeout();
                                }

                                @Override
                                public void onError(int code, String message) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    super.onError(code, message);
                                    callback.onError(code, message);
                                }
                            });
                }

                @Override
                public void onError() {
                    CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.LOAD_LIST));
                }
            });
        } else {
            CsEventBus.getDefault().post(new NoConnectionEvent(NoConnectionEvent.LOAD_LIST));
        }
    }
}
