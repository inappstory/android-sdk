package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Request;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.ResourceMappingObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenStatisticCallback;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.events.StoryCacheLoadedEvent;
import com.inappstory.sdk.stories.utils.Sizes;

import static com.inappstory.sdk.InAppStoryService.EXPAND_STRING;

public class StoryDownloader {
    private static StoryDownloader INSTANCE;


    private Object storyTasksLock = new Object();

    private Object pageTasksLock = new Object();

    private static final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService startNetExecutor = Executors.newFixedThreadPool(1);
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
    }

    public static class StoryPageTask {
        public int priority = 0;
        public List<String> urls = new ArrayList<>();
        public List<String> urlKeys = new ArrayList<>();
        public List<String> videoUrls = new ArrayList<>();
        public int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded
    }

    public class StoryTask {
        public int priority;
        public int loadType = 0; //1 - not loaded, 2 - loading, 3 - loaded, 4 - not loaded partial, 5 - loading partial, 6 - loaded partial
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

    int currentId = 0;

    public void setCurrentSlide(int index) {

    }

    public void reloadPage(int storyId, int index, ArrayList<Integer> addIds) {
        synchronized (storyTasksLock) {
            if (storyTasks.get(storyId) == null || storyTasks.get(storyId).loadType == -1) {
                addStoryTask(storyId, addIds);
                return;
            }
        }

        Pair<Integer, Integer> key = new Pair<>(storyId, index);
        synchronized (pageTasksLock) {
            if (pageTasks.get(key) != null && pageTasks.get(key).loadType == -1) {
                pageTasks.get(key).priority = 0;
                pageTasks.get(key).loadType = 0;
            }
        }
    }

    public void addStoryTask(int storyId, ArrayList<Integer> addIds) {
        synchronized (storyTasksLock) {

            currentId = storyId;
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
        //  Stories.set(findIndexByStoryId(Story.id), Story);

    }

    public void addStoryPageTasks(Story story) {
        Story local = findItemByStoryId(story.id);
        story.isOpened = local.isOpened;
        stories.set(findIndexByStoryId(story.id), story);
    }

    private Pair<Integer, Integer> getMaxPriorityPageTaskKey() {
        Pair<Integer, Integer> keyRes = null;
        int priority = 100000;
        synchronized (pageTasksLock) {
            if (pageTasks.size() == 0) return null;
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

    private Integer getMaxPriorityStoryTaskKey() {
        Integer keyRes = null;
        int priority = 100000;
        synchronized (storyTasksLock) {
            if (storyTasks.size() == 0) return null;
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
                    InAppStoryService.getInstance().openStatistic(new OpenStatisticCallback() {
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
                        int cellPriority;
                        synchronized (getInstance().storyTasksLock) {
                            if (getInstance().storyTasks.get(key).loadType < 4) {
                                loadType = getInstance().storyTasks.get(key).loadType = 3;
                                cellPriority = getInstance().storyTasks.get(key).priority;
                                getInstance().addStoryPageTasks(story[0]);
                            } else {
                                loadType = getInstance().storyTasks.get(key).loadType = 6;
                                cellPriority = getInstance().storyTasks.get(key).priority;
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
                                    int skipPr = getInstance().getStoryById(getInstance().currentId).pages.size() + (cellPriority - 1) * 2;
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
                if (!isRefreshing)
                    isRefreshing = true;
                InAppStoryService.getInstance().openStatistic(new OpenStatisticCallback() {
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
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            synchronized (getInstance().pageTasksLock) {
                getInstance().pageTasks.get(key).loadType = 1;
            }
            final Callable _ff = new Callable() {
                @Override
                public Object call() throws Exception {
                    synchronized (getInstance().pageTasksLock) {
                        for (int i = 0; i < getInstance().pageTasks.get(key).urls.size(); i++) {
                            String url = getInstance().pageTasks.get(key).urls.get(i);
                            downloadImageByUrl(getInstance().context, url, key.first, key.second);
                        }

                        for (String url : getInstance().pageTasks.get(key).videoUrls) {
                            downloadVideoByUrl(getInstance().context, url, key.first, key.second);
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

    private static Handler handler = new Handler();
    private static Handler errorHandler = new Handler();


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


    public void insert(int storyId, int pageId) {

    }

    public boolean stopStartedLoading = false;

    public static StoryDownloader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StoryDownloader(InAppStoryManager.getInstance().getContext());
        }
        return INSTANCE;
    }

    public StoryDownloader() {

    }

    public StoryDownloader(Context context) {
        this.context = context;
        thread = new HandlerThread("StoryContentDownloaderThread" + System.currentTimeMillis());
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.postDelayed(queueStoryReadRunnable, 100);
        handler.postDelayed(queuePageReadRunnable, 100);
    }

    private static HandlerThread thread;

    public static void destroy() {
        handler.removeCallbacks(queueStoryReadRunnable);
        handler.removeCallbacks(queuePageReadRunnable);
        if (thread != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                thread.quitSafely();
            } else {
                thread.quit();
            }
        INSTANCE = null;
    }

    public static void downloadVideoByUrl(final Context context, final String url, final int StoryId, int ind) throws Exception {

        Downloader.downVideo(context, url, FileType.STORY_IMAGE, StoryId, Sizes.getScreenSize());
       /* final Future<File> ff = imageExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return
            }
        });
        runnableExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ff.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    public static void downloadImageByUrl(final Context context, final String url, final int StoryId, int ind) throws Exception {
        Downloader.downAndCompressImg(context, url, FileType.STORY_IMAGE, StoryId, Sizes.getScreenSize());
    }

    @WorkerThread
    public void startedLoading(final List<Story> stories) {
        if (1 == 1) return;
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
            if (pageTasks.isEmpty()) {
                sync = sync && true;
            } else {
                if (sync) {
                    for (Pair<Integer, Integer> pair : pageTasks.keySet()) {
                        if (pageTasks.get(pair).loadType <= 1) sync = false;
                    }
                }
            }
        }
        if (sync) {
            startedLoading(newStories);
            return;
        }
        loadStories(stories, 0);
    }

    @WorkerThread
    public void uploadingAdditional(final List<Story> stories, final List<Story> newStories) {
        boolean sync = false;
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
            if (pageTasks.isEmpty()) {
                sync = sync && true;
            } else {
                if (sync) {
                    for (Pair<Integer, Integer> pair : pageTasks.keySet()) {
                        if (pageTasks.get(pair).loadType <= 1) sync = false;
                    }
                }
            }
        }
        if (sync) {
            startedLoading(newStories);
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

    public void renewPriorities(final int currentStoryIndex) throws IOException, InterruptedException {
        this.currentStoryIndex = currentStoryIndex;
        runnableExecutor.submit(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                int c = 1;
                int t = 6;
                boolean f1 = false;
                boolean f2 = false;
                while (!f1 || !f2) {
                    c *= -1;
                    i++;
                    int ind = currentStoryIndex + ((i) / 2) * c;
                    if (ind < 0) {
                        f1 = true;
                        continue;
                    }
                    if (ind > stories.size() - 1) {
                        f2 = true;
                        continue;
                    }
                    Story story = stories.get(ind);
                    synchronized (storyTasksLock) {
                        if (storyTasks.get(story.id) != null) {
                            if (storyTasks.get(story.id).loadType == 0) {
                                storyTasks.get(story.id).priority = i;
                            } else if (storyTasks.get(story.id).loadType == 3) {
                                storyTasks.get(story.id).priority = i;
                                storyTasks.get(story.id).loadType = 0;
                            }
                        }
                    }

                    synchronized (pageTasksLock) {
                        for (int j = 0; j < stories.get(ind).pages.size(); j++) {
                            if (pageTasks.get(new Pair<>(story.id, j)) != null) {
                                if (ind == currentStoryIndex && j < 2) {
                                    pageTasks.get(new Pair<>(story.id, j)).priority = j;
                                } else if (ind == currentStoryIndex + 1 && j < 2) {
                                    pageTasks.get(new Pair<>(story.id, j)).priority = j + 2;
                                } else if (ind == currentStoryIndex - 1 && j < 2) {
                                    pageTasks.get(new Pair<>(story.id, j)).priority = j + 4;
                                } else {
                                    pageTasks.get(new Pair<>(story.id, j)).priority = t;
                                    t++;
                                }
                            }
                        }
                    }
                }

                i = 0;
                c = 1;
                f1 = false;
                f2 = false;
                while (!f1 || !f2) {
                    c *= -1;
                    i++;
                    int ind = currentStoryIndex + ((i) / 2) * c;
                    if (ind < 0) {
                        f1 = true;
                        continue;
                    }
                    if (ind > stories.size() - 1) {
                        f2 = true;
                        continue;
                    }
                    Story story = stories.get(ind);

                    synchronized (storyTasksLock) {
                        if (storyTasks.get(story.id) != null) {
                            if (storyTasks.get(story.id).loadType == 0) {
                                storyTasks.get(story.id).priority = i;
                            } else if (storyTasks.get(story.id).loadType == 3) {
                                storyTasks.get(story.id).priority = i;
                                storyTasks.get(story.id).loadType = 0;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     *
     */
    @WorkerThread
    private void downloadImages(Context context, String content, Integer storyId, Point size) throws IOException, InterruptedException {
        for (String url : HtmlParser.getSrcUrls(content)) {
            try {
                downAndCompressImg(context, url, storyId, size);
            } catch (Exception e) {

            }
        }
    }

    private String cropUrl(String url) {
        //int pos = url.indexOf("?");
        return url.split("\\?")[0];
    }

    @NonNull
    @WorkerThread
    private File downAndCompressImg(Context con,
                                    @NonNull String url,
                                    Integer sourceId,
                                    Point size) throws Exception {
        FileCache cache = FileCache.INSTANCE;

        File img = cache.getStoredFile(con, cropUrl(url), FileType.STORY_IMAGE, sourceId, null);
        if (img.exists()) {
            return img;
        }
        byte[] bytes = null;

        Response response = new Request.Builder().get().url(url).build().execute();
        bytes = response.body.getBytes();
        File file = saveCompressedImg(bytes, img, size);
        return file;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromResource(byte[] res, int size) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0, res.length, options);
        double coeff = Math.sqrt(res.length / size);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, (int) (options.outWidth / coeff), (int) (options.outHeight / coeff));

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, 0, res.length, options);
    }


    private File saveCompressedImg(byte[] bytes, File img, Point limitedSize) throws IOException {
        if (bytes == null) return null;
        img.getParentFile().mkdirs();
        if (!img.exists())
            img.createNewFile();
        FileOutputStream fos = new FileOutputStream(img);
        Bitmap bm;
        if (bytes.length > 3 && bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            fos.close();
            throw new IOException("It's a GIF file");
        }
        if (bytes.length > 500000)
            try {

                bm = decodeSampledBitmapFromResource(bytes, 50000);
            } catch (Exception e) {
                bm = decodeSampledBitmapFromResource(bytes, 150000);
            }
        else
            try {
                bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (Exception e) {
                bm = decodeSampledBitmapFromResource(bytes, 150000);
            }
        if (bm == null) {
            throw new IOException("wrong bitmap");
        }
        if (limitedSize != null && (bm.getWidth() > limitedSize.x || bm.getHeight() > limitedSize.y)) {
            Bitmap outBm = Bitmap.createScaledBitmap(bm, limitedSize.x, bm.getHeight() * limitedSize.x / bm.getWidth(), true);
            outBm.compress(Bitmap.CompressFormat.JPEG, 90,
                    new BufferedOutputStream(fos, 1024));
            bm.recycle();
            outBm.recycle();
        } else {
            bm.compress(Bitmap.CompressFormat.JPEG, 90,
                    new BufferedOutputStream(fos, 1024));
            bm.recycle();
        }
        fos.close();
        return img;
    }

}
