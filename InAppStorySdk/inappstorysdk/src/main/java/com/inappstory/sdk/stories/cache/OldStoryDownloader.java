package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;

import androidx.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ResourceMappingObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.events.StoryCacheLoadedEvent;
import com.inappstory.sdk.stories.utils.SessionManager;

import static com.inappstory.sdk.stories.cache.StoryDownloadManager.EXPAND_STRING;


public class OldStoryDownloader {
    private static OldStoryDownloader INSTANCE;


    private final Object storyTasksLock = new Object();

    private final Object pageTasksLock = new Object();

    private static final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService imageExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);
    public int currentStoryIndex = 0;


    public ArrayList<Story> getStories() {
        return stories;
    }

    private ArrayList<Story> stories = new ArrayList<>();

    public Context context;


    public Story getStoryById(int id) {
        for (Story story : stories) {
            if (story.id == id) return story;
        }
        return null;
    }

    public void addStories(List<Story> stories) {
        for (Story story : stories) {
            if (!this.stories.contains(story))
                this.stories.add(story);
            else {
                Story tmp = story;
                int ind = this.stories.indexOf(story);
                if (tmp.pages == null & this.stories.get(ind).pages != null) {
                    tmp.pages = new ArrayList<>();
                    tmp.pages.addAll(this.stories.get(ind).pages);
                }
                if (tmp.durations == null & this.stories.get(ind).durations != null) {
                    tmp.durations = new ArrayList<>();
                    tmp.durations.addAll(this.stories.get(ind).durations);
                    tmp.slidesCount = tmp.durations.size();
                }
                if (tmp.layout == null & this.stories.get(ind).layout != null) {
                    tmp.layout = this.stories.get(ind).layout;
                }
                if (tmp.srcList == null & this.stories.get(ind).srcList != null) {
                    tmp.srcList = new ArrayList<>();
                    tmp.srcList.addAll(this.stories.get(ind).srcList);
                }
                this.stories.set(ind, tmp);
            }
        }
    }

    public static void clearCache() {
        synchronized (getInstance().pageTasksLock) {
            getInstance().pageTasks.clear();
        }
        synchronized (getInstance().storyTasksLock) {
            getInstance().storyTasks.clear();
        }
        FileCache.INSTANCE.deleteFolderRecursive(getInstance().context.getFilesDir(), false);
    }

    public void setStory(final Story story, int id) {
        Story cur = findItemByStoryId(id);
        if (cur == null) return;
        cur.loadedPages = new ArrayList<>();
        cur.pages = new ArrayList<String>() {{
            addAll(story.pages);
        }};
        for (int i = 0; i < cur.pages.size(); i++) {
            cur.loadedPages.add(false);
        }
        cur.id = id;
        cur.layout = story.layout;
        cur.title = story.title;
        cur.srcList = new ArrayList<ResourceMappingObject>() {{
            addAll(story.getSrcList());
        }};
        cur.durations = new ArrayList<Integer>() {{
            addAll(story.durations);
        }};
        if (!cur.durations.isEmpty()) {
            cur.slidesCount = story.durations.size();
        } else {
            cur.slidesCount = story.slidesCount;
        }
    }





    public HashMap<Pair<Integer, Integer>, StoryPageTask> pageTasks = new HashMap<>();
    public HashMap<Integer, StoryTask> storyTasks = new HashMap<>();

    public void cleanTasks() {
        synchronized (storyTasksLock) {
            storyTasks.clear();
        }
        synchronized (pageTasksLock) {
            pageTasks.clear();
        }
    }

    //Test
    public void reloadPage(int storyId, int index, ArrayList<Integer> addIds) {
        synchronized (storyTasksLock) {
            if (storyTasks == null) storyTasks = new HashMap<>();
            if (storyTasks.get(storyId) == null || storyTasks.get(storyId).loadType == -1) {
                addStoryTask(storyId, addIds);
                return;
            }
        }

        Pair<Integer, Integer> key = new Pair<>(storyId, index);
        synchronized (pageTasksLock) {
            if (pageTasks == null) pageTasks = new HashMap<>();
            if (pageTasks.get(key) != null && pageTasks.get(key).loadType == -1) {
                pageTasks.get(key).priority = 0;
                pageTasks.get(key).loadType = 0;
            }
        }
    }

    //Test
    public void addStoryTask(int storyId, ArrayList<Integer> addIds) {
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

    public void addStoryPageTasks(Story story) {
        Story local = findItemByStoryId(story.id);
        story.isOpened = local.isOpened;
        stories.set(findIndexByStoryId(story.id), story);
    }

    //Test
    public Pair<Integer, Integer> getMaxPriorityPageTaskKey() {
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

    //Test
    public Integer getMaxPriorityStoryTaskKey() {
        Integer keyRes = null;
        int priority = 100000;
        synchronized (storyTasksLock) {
            if (storyTasks == null || storyTasks.size() == 0) return null;
            Set<Integer> keys = storyTasks.keySet();
            for (Integer key : keys) {
                if (storyTasks.get(key).loadType != 1 && storyTasks.get(key).loadType != 4)
                    continue;

                if (storyTasks.get(key).priority < priority) {
                    keyRes = key;
                    priority = storyTasks.get(key).priority;
                }
            }
        }
        return keyRes;
    }

    private static Runnable queueStoryReadRunnable = new Runnable() {
        boolean isRefreshing = false;

        @Override
        public void run() {
            final Integer key = getInstance().getMaxPriorityStoryTaskKey();
            if (key == null) {
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            synchronized (getInstance().storyTasksLock) {
                if (getInstance().storyTasks.get(key).loadType == 4) {
                    getInstance().storyTasks.get(key).loadType = 5;
                } else if (getInstance().storyTasks.get(key).loadType == 1) {
                    getInstance().storyTasks.get(key).loadType = 2;
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

                                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.CACHE));
                                synchronized (getInstance().pageTasksLock) {
                                    getInstance().pageTasks.get(key).loadType = -1;
                                    errorHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            CsEventBus.getDefault().post(new PageTaskLoadErrorEvent(key, -1));
                                        }
                                    }, 300);
                                }
                            }
                        });
                }
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            final Story[] story = new Story[1];
            ExecutorService executorService = runnableExecutor;

            final Callable<Story> _ff = new Callable<Story>() {
                @Override
                public Story call() throws Exception {
                    Response response = NetworkClient.getApi().getStoryById(Integer.toString(key),
                            StatisticSession.getInstance().id, 1,
                            InAppStoryManager.getInstance().getApiKey(),
                            EXPAND_STRING).execute();
                    if (response.body != null) {
                        Story story1 = JsonParser.fromJson(response.body, Story.class);
                        return story1;
                    } else {
                        return null;
                    }
                }
            };
            final Future<Story> ff = netExecutor.submit(_ff);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        story[0] = ff.get();
                        int loadType;
                        synchronized (getInstance().storyTasksLock) {
                            if (getInstance().storyTasks.get(key).loadType < 4) {
                                loadType = getInstance().storyTasks.get(key).loadType = 3;
                                getInstance().addStoryPageTasks(story[0]);
                            } else {
                                loadType = getInstance().storyTasks.get(key).loadType = 6;
                                getInstance().addStoryPageTasks(story[0]);
                            }
                        }

                        if (story[0] != null) {
                            getInstance().setStory(story[0], story[0].id);
                            CsEventBus.getDefault().post(new StoryCacheLoadedEvent(story[0].id));
                            synchronized (getInstance().pageTasksLock) {
                                Set<Pair<Integer, Integer>> keys = getInstance().pageTasks.keySet();
                                int sz;
                                if (loadType == 3) {
                                    sz = story[0].pages.size();
                                    for (Pair<Integer, Integer> taskKey : keys) {
                                        if (getInstance().pageTasks.get(taskKey).loadType != 0)
                                            continue;
                                        getInstance().pageTasks.get(taskKey).priority += story[0].pages.size() + 4;
                                    }
                                    for (int i = 0; i < sz; i++) {
                                        if (getInstance().pageTasks.get(new Pair<>(key, i)) == null) {
                                            StoryPageTask spt = new StoryPageTask();
                                            spt.loadType = 0;
                                            spt.urls = story[0].getSrcListUrls(i, null);
                                            spt.videoUrls = story[0].getSrcListUrls(i, "video");
                                            spt.priority = i < 2 ? i : i + 4;
                                            getInstance().pageTasks.put(new Pair<>(key, i), spt);
                                        } else {
                                            getInstance().pageTasks.get(new Pair<>(key, i)).priority = i < 2 ? i : i + 4;
                                        }
                                    }
                                } else {
                                    sz = 2;
                                    for (int i = 0; i < sz; i++) {
                                        if (getInstance().pageTasks.get(new Pair<>(key, i)) == null) {
                                            StoryPageTask spt = new StoryPageTask();
                                            spt.loadType = 0;
                                            spt.urls = story[0].getSrcListUrls(i, null);
                                            spt.videoUrls = story[0].getSrcListUrls(i, "video");
                                            spt.priority = i + 2;
                                            getInstance().pageTasks.put(new Pair<>(key, i), spt);
                                        } else {
                                            getInstance().pageTasks.get(new Pair<>(key, i)).priority = i + 2;
                                        }
                                    }

                                }
                            }
                        }
                        handler.postDelayed(queueStoryReadRunnable, 200);
                    } catch (Throwable t) {

                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.CACHE));
                        synchronized (getInstance().storyTasksLock) {
                            errorHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CsEventBus.getDefault().post(new PageTaskLoadErrorEvent(key, -1));
                                }
                            }, 300);
                            getInstance().storyTasks.get(key).loadType = -1;
                        }
                        handler.postDelayed(queueStoryReadRunnable, 200);
                    }
                }
            });


        }
    };

    public static final String COVER_VIDEO_FOLDER_ID = "-9999";

    public static void downloadCoverVideo(final String url) {
        ExecutorService executorService = runnableExecutor;
        final Callable _ff = new Callable() {
            @Override
            public Object call() throws Exception {
                downloadVideoByUrl(getInstance().context, url, COVER_VIDEO_FOLDER_ID, -1);
                return null;
            }
        };
        final Future<Story> ff = imageExecutor.submit(_ff);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ff.get();
                } catch (Throwable t) {

                }
            }
        });
    }

    private static Runnable queuePageReadRunnable = new Runnable() {
        boolean isRefreshing = false;

        @Override
        public void run() {

            ExecutorService executorService = runnableExecutor;
            final Pair<Integer, Integer> key = getInstance().getMaxPriorityPageTaskKey();
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
                                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.CACHE));
                                synchronized (getInstance().pageTasksLock) {
                                    getInstance().pageTasks.get(key).loadType = -1;
                                    errorHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            CsEventBus.getDefault().post(new PageTaskLoadErrorEvent(key.first, key.second));
                                        }
                                    }, 300);
                                }
                            }
                        });
                }
                handler.postDelayed(queuePageReadRunnable, 100);
                return;
            }
            synchronized (getInstance().pageTasksLock) {
                getInstance().pageTasks.get(key).loadType = 1;
            }
            final Callable _ff = new Callable() {
                @Override
                public Object call() throws Exception {
                    synchronized (getInstance().pageTasksLock) {
                        final String storyId = key.first != null ? Integer.toString(key.first) : null;
                        for (int i = 0; i < getInstance().pageTasks.get(key).urls.size(); i++) {
                            String url = getInstance().pageTasks.get(key).urls.get(i);
                            downloadImageByUrl(getInstance().context, url, storyId, key.second);
                        }

                        for (String url : getInstance().pageTasks.get(key).videoUrls) {
                            downloadVideoByUrl(getInstance().context, url, storyId, key.second);
                        }
                        return null;
                    }
                }
            };
            final Future<Story> ff = imageExecutor.submit(_ff);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        ff.get();
                        synchronized (getInstance().pageTasksLock) {
                            getInstance().pageTasks.get(key).loadType = 2;
                            CsEventBus.getDefault().post(new PageTaskLoadedEvent(key.first, key.second));
                        }
                        handler.postDelayed(queuePageReadRunnable, 200);
                    } catch (Throwable t) {
                        CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.CACHE));
                        synchronized (getInstance().pageTasksLock) {
                            getInstance().pageTasks.get(key).loadType = -1;
                            errorHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CsEventBus.getDefault().post(new PageTaskLoadErrorEvent(key.first, key.second));
                                }
                            }, 300);
                        }
                        handler.postDelayed(queuePageReadRunnable, 200);
                    }
                }
            });
        }
    };

    public void reloadTask(int id, int index) {
        if (index >= 0) {
            Pair<Integer, Integer> pair = new Pair(id, index);
            if (getInstance().pageTasks.containsKey(pair)) {
                getInstance().pageTasks.get(pair).priority = 0;
                getInstance().pageTasks.get(pair).loadType = 0;
            }
        } else {
            getInstance().storyTasks.get(id).priority = 0;
            getInstance().storyTasks.get(id).loadType = 0;
        }
    }


    public boolean checkIfPageLoaded(Pair<Integer, Integer> key) {
        if (getInstance().pageTasks.get(key) != null && getInstance().pageTasks.get(key).loadType == 2) {
            return true;
        } else {
            return false;
        }

    }

    private static Handler handler;
    private static Handler errorHandler;


    public int findIndexByStoryId(int id) {
        if (stories != null) {
            for (int i = 0; i < stories.size(); i++) {
                if (stories.get(i).id == id) return i;
            }
        }
        return -1;
    }

    public Story findItemByStoryId(int id) {

        if (stories != null) {
            for (int i = 0; i < stories.size(); i++) {
                if (stories.get(i).id == id) return stories.get(i);
            }
        }

        return null;
    }

    public static OldStoryDownloader getInstance() {
        if (INSTANCE == null) {
            if (InAppStoryManager.getInstance() == null) return null;
            INSTANCE = new OldStoryDownloader(InAppStoryManager.getInstance().getContext());
        }
        return INSTANCE;
    }

    public OldStoryDownloader() {

    }

    public OldStoryDownloader(Context context) {
        this.context = context;
        thread = new HandlerThread("StoryContentDownloaderThread" + System.currentTimeMillis());
        thread.start();
        handler = new Handler(thread.getLooper());
        errorHandler = new Handler(thread.getLooper());
        handler.postDelayed(queueStoryReadRunnable, 100);
        handler.postDelayed(queuePageReadRunnable, 100);
    }

    private static HandlerThread thread;

    public static void destroy() {
        if (handler != null) {
            handler.removeCallbacks(queueStoryReadRunnable);
            handler.removeCallbacks(queuePageReadRunnable);
        }
        if (thread != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                thread.quitSafely();
            } else {
                thread.quit();
            }
        INSTANCE = null;
    }

    public static void downloadVideoByUrl(final Context context, final String url, final String storyId, int ind) throws Exception {

        Downloader.downloadFile(context, url, FileType.STORY_FILE, storyId);
    }

    public static void downloadImageByUrl(final Context context, final String url, final String storyId, int ind) throws Exception {
        Downloader.downloadFile(context, url, FileType.STORY_FILE, storyId);
    }


    @WorkerThread
    public void uploadingAdditional(final List<Story> newStories) {
        boolean sync = false;
        addStories(newStories);
        synchronized (storyTasksLock) {
            if (storyTasks.isEmpty()) {
                sync = true;
            } else {
                sync = true;
                for (Integer i : storyTasks.keySet()) {
                    if (storyTasks.get(i).loadType <= 1) sync = false;
                }
            }
        }
        synchronized (pageTasksLock) {
            if (!pageTasks.isEmpty() && sync) {
                for (Pair<Integer, Integer> pair : pageTasks.keySet()) {
                    if (pageTasks.get(pair).loadType <= 1) sync = false;
                }
            }
        }
        if (sync) {
            return;
        }
        loadStories(stories, 0);
    }


    public void loadStories(List<Story> stories,
                            int currentStory) {
        //stopStartedLoading = true;
        if (this.stories == null || this.stories.isEmpty()) {
            this.stories = new ArrayList<>();
            this.stories.addAll(stories);
        } else {
            for (int i = 0; i < stories.size(); i++) {
                boolean newNar = true;
                for (int j = 0; j < this.stories.size(); j++) {
                    if (this.stories.get(j).id == stories.get(i).id) {
                        this.stories.get(j).isOpened = stories.get(i).isOpened;
                        newNar = false;
                        this.stories.set(j, stories.get(i));
                    }
                }
                if (newNar) {
                    this.stories.add(stories.get(i));
                }
            }
        }
        this.currentStoryIndex = findIndexByStoryId(currentStory);
        if (currentStoryIndex == -1) return;
    }
}
