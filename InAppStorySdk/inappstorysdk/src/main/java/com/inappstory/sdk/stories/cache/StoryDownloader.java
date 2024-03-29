package com.inappstory.sdk.stories.cache;

import static com.inappstory.sdk.stories.cache.StoryDownloadManager.EXPAND_STRING;

import android.os.Handler;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.network.SimpleApiCallback;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadListCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.utils.SessionManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private HashMap<StoryTaskData, StoryTaskWithPriority> storyTasks = new HashMap<>();

    void addCompletedStoryTask(int storyId, Story.StoryType type) {
        synchronized (storyTasksLock) {
            storyTasks.put(new StoryTaskData(storyId, type), new StoryTaskWithPriority(-1, 3));
        }
    }

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
            if (!storyTasks.isEmpty()) {
                for (StoryTaskData i : storyTasks.keySet()) {
                    if (getStoryLoadType(i) <= 1) return false;
                }
            }
            return true;
        }
    }

    ArrayList<StoryTaskData> firstPriority = new ArrayList<>();
    ArrayList<StoryTaskData> secondPriority = new ArrayList<>();

    void changePriority(StoryTaskData storyId, ArrayList<Integer> addIds, Story.StoryType type) {
        secondPriority.remove(storyId);
        for (Integer id : addIds) {
            StoryTaskData key = new StoryTaskData(id, type);
            secondPriority.remove(key);
        }
        for (StoryTaskData id : firstPriority) {
            if (!secondPriority.contains(id))
                secondPriority.add(id);
        }
        firstPriority.clear();
        firstPriority.add(storyId);
        for (Integer id : addIds) {
            StoryTaskData key = new StoryTaskData(id, type);
            firstPriority.add(key);
        }

    }

    void addStoryTask(int storyId, ArrayList<Integer> addIds, Story.StoryType type) {
        synchronized (storyTasksLock) {
            if (storyTasks == null) storyTasks = new HashMap<>();
            for (StoryTaskData storyTaskKey : storyTasks.keySet()) {
                StoryTaskWithPriority storyTaskWithPriority = storyTasks.get(storyTaskKey);
                if (storyTaskWithPriority != null &&
                        storyTaskWithPriority.loadType > 0 &&
                        storyTaskWithPriority.loadType != 3 &&
                        storyTaskWithPriority.loadType != 6
                ) {
                    storyTaskWithPriority.loadType += 3;
                }
            }
            for (Integer storyIntKey : addIds) {
                StoryTaskData key = new StoryTaskData(storyIntKey, type);
                StoryTaskWithPriority task = storyTasks.get(key);
                if (task != null) {
                    if (task.loadType != 3 &&
                            task.loadType != 6) {
                        task.loadType = 4;
                    }
                } else {
                    StoryTaskWithPriority st = new StoryTaskWithPriority(4);
                    storyTasks.put(key, st);
                }
            }
            StoryTaskData keyByStoryId = new StoryTaskData(storyId, type);
            StoryTaskWithPriority taskByStoryId = storyTasks.get(keyByStoryId);
            if (taskByStoryId != null) {
                if (taskByStoryId.loadType != 3) {
                    if (taskByStoryId.loadType == 6) {
                        taskByStoryId.loadType = 3;
                        if (callback != null)
                            callback.onDownload(manager.getStoryById(storyId, type), 3, type);
                    } else if (taskByStoryId.loadType == 5) {
                        taskByStoryId.loadType = 2;
                    } else {
                        taskByStoryId.loadType = 1;
                    }
                } else {
                    return;
                }
            } else {
                storyTasks.put(keyByStoryId, new StoryTaskWithPriority(1));
            }
            changePriority(keyByStoryId, addIds, type);
        }
    }


    private StoryTaskData getMaxPriorityStoryTaskKey() throws Exception {
        synchronized (storyTasksLock) {
            if (storyTasks == null || storyTasks.size() == 0) return null;
            if (firstPriority == null || secondPriority == null) return null;
            for (StoryTaskData key : firstPriority) {
                if (getStoryLoadType(key) != 1 && getStoryLoadType(key) != 4)
                    continue;
                return key;
            }
            for (StoryTaskData key : secondPriority) {
                if (getStoryLoadType(key) != 1 && getStoryLoadType(key) != 4)
                    continue;
                return key;
            }
            return null;
        }
    }

    void setStoryLoadType(StoryTaskData key, int loadType) {
        if (!storyTasks.containsKey(key)) return;
        Objects.requireNonNull(storyTasks.get(key)).loadType = loadType;
    }

    int getStoryLoadType(StoryTaskData key) {
        if (!storyTasks.containsKey(key)) return -5;
        return Objects.requireNonNull(storyTasks.get(key)).loadType;
    }


    private Handler handler;
    private Handler errorHandler;

    void reload(int storyId, ArrayList<Integer> addIds, Story.StoryType type) {
        synchronized (storyTasksLock) {
            StoryTaskData key = new StoryTaskData(storyId, type);
            if (storyTasks == null) storyTasks = new HashMap<>();
            storyTasks.remove(key);
        }
        addStoryTask(storyId, addIds, type);
    }


    private void loadStoryError(final StoryTaskData key) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().cacheError();
        }
        synchronized (storyTasksLock) {
            if (storyTasks != null)
                storyTasks.remove(key);
            if (firstPriority != null)
                firstPriority.remove(key);
            if (secondPriority != null)
                secondPriority.remove(key);
            setStoryLoadType(key, -2);
            callback.onError(key);
        }
    }

    private Runnable queueStoryReadRunnable = new Runnable() {
        boolean isRefreshing = false;

        @Override
        public void run() {
            StoryTaskData tKey = null;
            try {
                tKey = getMaxPriorityStoryTaskKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tKey == null) {
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            final StoryTaskData key = tKey;
            synchronized (storyTasksLock) {
                if (getStoryLoadType(key) == 4) {
                    setStoryLoadType(key, 5);
                } else if (getStoryLoadType(key) == 1) {
                    setStoryLoadType(key, 2);
                }
            }
            if (Session.needToUpdate()) {
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


    void loadStoryResult(StoryTaskData key, Response response) {
        if (response.body != null) {
            Story story = JsonParser.fromJson(response.body, Story.class);
            int loadType;
            synchronized (storyTasksLock) {
                if (getStoryLoadType(key) < 4) {
                    loadType = 3;
                } else {
                    loadType = 6;
                }
                setStoryLoadType(key, loadType);
                firstPriority.remove(key);
                secondPriority.remove(key);
            }
            if (story != null) {
                if (callback != null) {
                    callback.onDownload(story, loadType, key.storyType);
                }
            }
        } else if (response.errorBody != null) {
            loadStoryError(key);
        }
        handler.postDelayed(queueStoryReadRunnable, 200);
    }


    void loadStory(StoryTaskData key) {
        try {
            String storyUID;
            Response response;
            if (key.storyType == Story.StoryType.UGC) {
                storyUID = ProfilingManager.getInstance().addTask("api_story_ugc");
                response = NetworkClient.getApi().getUgcStoryById(Integer.toString(key.storyId), 1,
                        EXPAND_STRING).execute();
            } else {
                storyUID = ProfilingManager.getInstance().addTask("api_story");
                response = NetworkClient.getApi().getStoryById(Integer.toString(key.storyId),
                        1,
                        EXPAND_STRING).execute();
            }
            ProfilingManager.getInstance().setReady(storyUID);
            loadStoryResult(key, response);
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


    public static void generateCommonLoadListError(String feed) {
        if (CallbackManager.getInstance().getErrorCallback() != null) {
            CallbackManager.getInstance().getErrorCallback().loadListError(StringsUtils.getNonNull(feed));
        }
    }

    private static final String UGC_FEED = "UGC";

    void loadUgcStoryList(final SimpleApiCallback<List<Story>> callback, final String payload) {
        if (InAppStoryService.isNull()) {
            generateCommonLoadListError(UGC_FEED);
            callback.onError("");
            return;
        }
        if (InAppStoryService.isConnected()) {
            SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
                @Override
                public void onSuccess() {
                    if (InAppStoryService.isNull()) return;
                    final String loadStoriesUID = ProfilingManager.getInstance().addTask("api_ugc_story_list");
                    NetworkClient.getApi().getUgcStories(
                                    payload,
                                    null,
                                    "slides_count")
                            .enqueue(new NetworkCallback<List<Story>>() {
                                @Override
                                public void onSuccess(List<Story> response) {
                                    if (InAppStoryService.isNull() || response == null) {
                                        generateCommonLoadListError(UGC_FEED);
                                        callback.onError("");
                                    } else {
                                        ProfilingManager.getInstance().setReady(loadStoriesUID);
                                        callback.onSuccess(response);
                                    }
                                }

                                @Override
                                public Type getType() {
                                    return new StoryListType();
                                }

                                @Override
                                public void onTimeout() {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(UGC_FEED);
                                    callback.onError("");
                                }

                                @Override
                                public void onError(int code, String message) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(UGC_FEED);
                                    callback.onError(message);
                                }


                                @Override
                                public void error424(String message) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(null);
                                    callback.onError(message);
                                    SessionManager.getInstance().closeSession(true, false);
                                    loadUgcStoryList(callback, payload);
                                }
                            });
                }

                @Override
                public void onError() {
                    generateCommonLoadListError(UGC_FEED);
                    callback.onError("");
                }
            });
        } else {
            generateCommonLoadListError(UGC_FEED);
            callback.onError("");
        }
    }

    void loadStoryListByFeed(final String feed, final SimpleApiCallback<List<Story>> callback) {
        if (InAppStoryService.isNull()) {
            generateCommonLoadListError(feed);
            callback.onError("");
            return;
        }
        if (InAppStoryService.isConnected()) {
            SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
                @Override
                public void onSuccess() {
                    if (InAppStoryService.isNull()) return;
                    final String loadStoriesUID = ProfilingManager.getInstance().addTask("api_story_list");
                    NetworkClient.getApi().getFeed(
                                    feed,
                                    ApiSettings.getInstance().getTestKey(),
                                    0,
                                    InAppStoryService.getInstance().getTagsString(),
                                    null)
                            .enqueue(new LoadFeedCallback() {
                                @Override
                                public void onSuccess(Feed response) {
                                    if (InAppStoryService.isNull() || response == null) {
                                        generateCommonLoadListError(feed);
                                        callback.onError("");
                                    } else {
                                        ProfilingManager.getInstance().setReady(loadStoriesUID);
                                        callback.onSuccess(response.stories, response.hasFavorite(), response.getFeedId());
                                    }
                                }

                                @Override
                                public void onTimeout() {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(feed);
                                    callback.onError("");
                                }

                                @Override
                                public void onError(int code, String message) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(feed);
                                    callback.onError(message);
                                }


                                @Override
                                public void error424(String message) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(null);
                                    callback.onError(message);
                                    SessionManager.getInstance().closeSession(true, false);
                                    loadStoryListByFeed(feed, callback);
                                }
                            });
                }

                @Override
                public void onError() {
                    generateCommonLoadListError(feed);
                    callback.onError("");
                }
            });
        } else {
            generateCommonLoadListError(feed);
            callback.onError("");
        }
    }


    void loadStoryList(final SimpleApiCallback<List<Story>> callback, final boolean isFavorite) {
        if (InAppStoryService.isNull()) {
            generateCommonLoadListError(null);
            callback.onError("");
            return;
        }
        if (InAppStoryService.isConnected()) {
            SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
                @Override
                public void onSuccess() {
                    if (InAppStoryService.isNull()) return;
                    final String loadStoriesUID = ProfilingManager.getInstance().addTask(isFavorite
                            ? "api_favorite_list" : "api_story_list");
                    NetworkClient.getApi().getStories(
                                    ApiSettings.getInstance().getTestKey(),
                                    isFavorite ? 1 : 0,
                                    isFavorite ? null : InAppStoryService.getInstance().getTagsString(),
                                    null)
                            .enqueue(new LoadListCallback() {
                                @Override
                                public void onSuccess(List<Story> response) {
                                    if (InAppStoryService.isNull()) {
                                        generateCommonLoadListError(null);
                                        callback.onError("");
                                    } else {
                                        ProfilingManager.getInstance().setReady(loadStoriesUID);
                                        callback.onSuccess(response);
                                    }
                                }

                                @Override
                                public void onTimeout() {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(null);
                                    callback.onError("");
                                }

                                @Override
                                public void onError(int code, String message) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(null);
                                    callback.onError(message);
                                }


                                @Override
                                public void error424(String message) {
                                    ProfilingManager.getInstance().setReady(loadStoriesUID);
                                    generateCommonLoadListError(null);
                                    callback.onError(message);
                                    SessionManager.getInstance().closeSession(true, false);
                                    loadStoryList(callback, isFavorite);
                                }
                            });
                }

                @Override
                public void onError() {
                    generateCommonLoadListError(null);
                    callback.onError("");
                }
            });
        } else {
            generateCommonLoadListError(null);
            callback.onError("");
        }
    }
}
