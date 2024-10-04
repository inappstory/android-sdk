package com.inappstory.sdk.stories.cache;

import static com.inappstory.sdk.stories.cache.StoryDownloadManager.EXPAND_STRING;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.callbacks.SimpleApiCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadListCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.stories.utils.LoopedExecutor;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

class StoryDownloader {
    private final @NonNull IASCore core;

    StoryDownloader(
            @NonNull IASCore core,
            DownloadStoryCallback callback,
            StoryDownloadManager manager
    ) {
        this.core = core;
        this.callback = callback;
        this.manager = manager;
    }

    StoryDownloadManager manager;


    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);

    void init() {
        loopedExecutor.init(queueStoryReadRunnable);
    }

    private DownloadStoryCallback callback;

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
        loopedExecutor.shutdown();
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


    void reload(int storyId, ArrayList<Integer> addIds, Story.StoryType type) {
        synchronized (storyTasksLock) {
            StoryTaskData key = new StoryTaskData(storyId, type);
            if (storyTasks == null) storyTasks = new HashMap<>();
            storyTasks.remove(key);
        }
        addStoryTask(storyId, addIds, type);
    }


    private void loadStoryError(final StoryTaskData key) {
        core.callbacksAPI().useCallback(IASCallbackType.ERROR,
                new UseIASCallback<ErrorCallback>() {
                    @Override
                    public void use(@NonNull ErrorCallback callback) {
                        callback.cacheError();
                    }
                });

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
                loopedExecutor.freeExecutor();
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
            if (core.sessionManager().getSession().getSessionId().isEmpty()) {
                if (!isRefreshing) {
                    isRefreshing = true;
                    core.sessionManager().openSession(new OpenSessionCallback() {
                        @Override
                        public void onSuccess(String sessionId) {
                            isRefreshing = false;
                        }

                        @Override
                        public void onError() {
                            loadStoryError(key);
                        }
                    });
                }
                loopedExecutor.freeExecutor();
                return;
            }
            loadStory(key);
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
        loopedExecutor.freeExecutor();
    }


    void loadStory(StoryTaskData key) {

        try {
            NetworkClient networkClient = InAppStoryManager.getNetworkClient();
            String storyUID;
            Response response;
            if (key.storyType == Story.StoryType.UGC) {
                storyUID = core.statistic().profiling().addTask("api_story_ugc");
                response = networkClient.execute(
                        networkClient.getApi().getUgcStoryById(
                                Integer.toString(key.storyId),
                                1,
                                EXPAND_STRING
                        )
                );
            } else {
                storyUID = core.statistic().profiling().addTask("api_story");
                response = networkClient.execute(
                        networkClient.getApi().getStoryById(
                                Integer.toString(key.storyId),
                                ApiSettings.getInstance().getTestKey(),
                                0,
                                1,
                                EXPAND_STRING
                        )
                );
            }
            core.statistic().profiling().setReady(storyUID);
            loadStoryResult(key, response);
        } catch (Throwable t) {
            t.printStackTrace();
            loadStoryError(key);
            loopedExecutor.freeExecutor();
        }
    }

    void loadStoryFavoriteList(final NetworkCallback<List<Story>> callback) {
        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            callback.errorDefault("No network client");
            return;
        }
        networkClient.enqueue(
                networkClient.getApi().getStories(
                        ApiSettings.getInstance().getTestKey(),
                        1,
                        null,
                        "id, background_color, image"
                ),
                callback
        );
    }


    public void generateCommonLoadListError(final String feed) {
        core.callbacksAPI().useCallback(
                IASCallbackType.ERROR,
                new UseIASCallback<ErrorCallback>() {
                    @Override
                    public void use(@NonNull ErrorCallback callback) {
                        callback.loadListError(StringsUtils.getNonNull(feed));
                    }
                }
        );
    }

    private static final String UGC_FEED = "UGC";

    void loadUgcStoryList(final SimpleApiCallback<List<Story>> callback, final String payload) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || networkClient == null) {
            generateCommonLoadListError(UGC_FEED);
            callback.onError("");
            return;
        }
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(final String sessionId) {
                final String loadStoriesUID = core.statistic().profiling().addTask("api_ugc_story_list");
                networkClient.enqueue(
                        networkClient.getApi().getUgcStories(
                                payload,
                                null,
                                "slides_count"
                        ),
                        new NetworkCallback<List<Story>>() {
                            @Override
                            public void onSuccess(List<Story> response) {
                                if (response == null) {
                                    generateCommonLoadListError(UGC_FEED);
                                    callback.onError("");
                                } else {
                                    core.statistic().profiling().setReady(loadStoriesUID);
                                    callback.onSuccess(response);
                                }
                            }

                            @Override
                            public Type getType() {
                                return new StoryListType();
                            }

                            @Override
                            public void errorDefault(String message) {
                                core.statistic().profiling().setReady(loadStoriesUID);
                                generateCommonLoadListError(UGC_FEED);
                                callback.onError(message);
                            }


                            @Override
                            public void error424(String message) {
                                core.statistic().profiling().setReady(loadStoriesUID);
                                generateCommonLoadListError(null);
                                callback.onError(message);
                                closeSessionIf424(sessionId);
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
    }

    void loadStoryListByFeed(final String feed, final SimpleApiCallback<List<Story>> callback, final boolean retry) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || networkClient == null) {
            generateCommonLoadListError(feed);
            callback.onError("");
            return;
        }
        if (service.isConnected()) {
            core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
                @Override
                public void onSuccess(final String sessionId) {
                    final String loadStoriesUID = core.statistic().profiling().addTask("api_story_list");
                    networkClient.enqueue(
                            networkClient.getApi().getFeed(
                                    feed,
                                    ApiSettings.getInstance().getTestKey(),
                                    0,
                                    TextUtils.join(",",
                                            ((IASDataSettingsHolder) core.settingsAPI()).tags()),
                                    null,
                                    null
                            ),
                            new LoadFeedCallback() {
                                @Override
                                public void onSuccess(Feed response) {
                                    if (response == null) {
                                        generateCommonLoadListError(feed);
                                        callback.onError("");
                                    } else {
                                        core.statistic().profiling().setReady(loadStoriesUID);
                                        callback.onSuccess(response.stories, response.hasFavorite(), response.getFeedId());
                                    }
                                }

                                @Override
                                public void errorDefault(String message) {
                                    core.statistic().profiling().setReady(loadStoriesUID);
                                    generateCommonLoadListError(feed);
                                    callback.onError(message);
                                }


                                @Override
                                public void error424(String message) {
                                    core.statistic().profiling().setReady(loadStoriesUID);
                                    generateCommonLoadListError(null);
                                    callback.onError(message);
                                    closeSessionIf424(sessionId);
                                    if (retry)
                                        loadStoryListByFeed(feed, callback, false);
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


    void loadStoryList(final SimpleApiCallback<List<Story>> callback, final boolean isFavorite, final boolean retry) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || networkClient == null) {
            generateCommonLoadListError(null);
            callback.onError("");
            return;
        }
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(final String sessionId) {
                final String loadStoriesUID = core.statistic().profiling().addTask(isFavorite
                        ? "api_favorite_list" : "api_story_list");
                networkClient.enqueue(
                        networkClient.getApi().getStories(
                                ApiSettings.getInstance().getTestKey(),
                                isFavorite ? 1 : 0,
                                isFavorite ? null :
                                        TextUtils.join(",",
                                                ((IASDataSettingsHolder) core.settingsAPI()).tags()),
                                null
                        ),
                        new LoadListCallback() {
                            @Override
                            public void onSuccess(List<Story> response) {
                                if (response == null) {
                                    generateCommonLoadListError(null);
                                    callback.onError("");
                                } else {
                                    core.statistic().profiling().setReady(loadStoriesUID);
                                    callback.onSuccess(response);
                                }
                            }

                            @Override
                            public void errorDefault(String message) {
                                core.statistic().profiling().setReady(loadStoriesUID);
                                generateCommonLoadListError(null);
                                callback.onError(message);
                            }


                            @Override
                            public void error424(String message) {
                                core.statistic().profiling().setReady(loadStoriesUID);
                                generateCommonLoadListError(null);
                                callback.onError(message);
                                closeSessionIf424(sessionId);
                                if (retry)
                                    loadStoryList(callback, isFavorite, false);
                            }
                        });
            }

            @Override
            public void onError() {
                generateCommonLoadListError(null);
                callback.onError("");
            }
        });
    }

    private void closeSessionIf424(String sessionId) {
        String oldUserId = "";
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null) {
            oldUserId = inAppStoryManager.getUserId();
            core.sessionManager().closeSession(
                    true,
                    false,
                    inAppStoryManager.getCurrentLocale(),
                    oldUserId,
                    sessionId
            );
        }
    }
}
