package io.casestory.sdk.stories.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.stories.api.models.StatisticSession;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.callbacks.OpenStatisticCallback;
import io.casestory.sdk.stories.api.networkclient.ApiClient;
import io.casestory.sdk.stories.utils.Sizes;
import okhttp3.Request;

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
                this.stories.set(this.stories.indexOf(story), story);
            }
        }
    }

    public static void clearCache() {
        if (getInstance().stories != null)
            getInstance().stories.clear();
        getInstance().pageTasks.clear();
        getInstance().storyTasks.clear();

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
        cur.durations = new ArrayList<Integer>() {{
            addAll(story.durations);
        }};
    }

    public static class StoryPageTask {
        public int priority = 0;
        public List<String> urls = new ArrayList<>();
        public List<String> videoUrls = new ArrayList<>();
        public int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded
    }

    public class StoryTask {
        public int priority;
        public int loadType = 0; //0 - not loaded, 1 - loading, 2 - loaded, 3 - partial
    }

    public HashMap<Pair<Integer, Integer>, StoryPageTask> pageTasks = new HashMap<>();
    public HashMap<Integer, StoryTask> storyTasks = new HashMap<>();

    public void cleanStories() {

    }

    public void addStoryTasks() {
        //  Stories.set(findIndexByStoryId(Story.id), Story);

    }

    public void addStoryPageTasks(Story story) {
        stories.set(findIndexByStoryId(story.id), story);
    }

    private Pair<Integer, Integer> getMaxPriorityPageTaskKey() {
        Pair<Integer, Integer> keyRes = null;
        int priority = 100;
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
        }
        return keyRes;
    }

    private Integer getMaxPriorityStoryTaskKey() {
        Integer keyRes = null;
        int priority = 100;
        synchronized (storyTasksLock) {
            if (storyTasks.size() == 0) return null;
            Set<Integer> keys = storyTasks.keySet();
            for (Integer key : keys) {
                if (storyTasks.get(key).loadType != 0) continue;
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
            ExecutorService executorService = runnableExecutor;
            final Integer key = getInstance().getMaxPriorityStoryTaskKey();
            if (key == null) {
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            if (StatisticSession.needToUpdate()) {
                if (!isRefreshing)
                    isRefreshing = true;
                CaseStoryService.getInstance().openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        isRefreshing = false;
                    }

                    @Override
                    public void onError() {

                    }
                });
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            final Story[] story = new Story[1];
            story[0] = null;
            synchronized (getInstance().storyTasksLock) {
                getInstance().storyTasks.get(key).loadType = 1;
            }
            Log.d("StoryDownload", "id " + Integer.toString(key));
            Log.d("StoryDownload", "index " + Integer.toString(getInstance().findIndexByStoryId(key)));
            final Callable<Story> _ff = new Callable<Story>() {
                @Override
                public Story call() throws Exception {
                    return ApiClient.getApi().getStoryById(Integer.toString(key),
                            StatisticSession.getInstance().id,
                            CaseStoryManager.getInstance().getApiKey(),
                            "slides_html,layout,slides_duration")
                            .execute().body();
                }
            };
            final Future<Story> ff = netExecutor.submit(_ff);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        story[0] = ff.get();
                        synchronized (getInstance().storyTasksLock) {
                            getInstance().storyTasks.get(key).loadType = 2;
                        }
                        if (story[0] != null) {
                            synchronized (getInstance().pageTasksLock) {
                                int c = 0;
                                int pr = 0;
                                Set<Pair<Integer, Integer>> keys = getInstance().pageTasks.keySet();
                                for (Pair<Integer, Integer> taskKey : keys) {
                                    if (getInstance().pageTasks.get(taskKey).loadType != 0)
                                        continue;
                                    if (getInstance().pageTasks.get(taskKey).priority > pr) {
                                        pr = getInstance().pageTasks.get(taskKey).priority;
                                    }
                                }
                                pr += 6;
                                int ind = getInstance().findIndexByStoryId(key);
                                for (String page : story[0].pages) {
                                    if (getInstance().pageTasks.get(new Pair<>(key, c)) == null) {
                                        final int prT;
                                        if (ind == getInstance().currentStoryIndex && c < 2) {
                                            prT = c;
                                        } else if (ind == getInstance().currentStoryIndex + 1 && c < 2) {
                                            prT = c + 2;
                                        } else if (ind == getInstance().currentStoryIndex - 1 && c < 2) {
                                            prT = c + 4;
                                        } else {
                                            prT = pr + c;
                                        }
                                        final List<String> links = HtmlParser.getSrcUrls(page);
                                        final List<String> videos = HtmlParser.getSrcVideoUrls(page);
                                        getInstance().pageTasks.put(new Pair<>(key, c), new StoryPageTask() {{
                                            loadType = 0;
                                            urls = links;
                                            videoUrls = videos;
                                            priority = prT;
                                        }});
                                        c++;
                                    } else {
                                        c++;
                                    }
                                }
                            }
                        }
                        handler.postDelayed(queueStoryReadRunnable, 200);
                    } catch (Throwable t) {
                        synchronized (getInstance().storyTasksLock) {
                            getInstance().storyTasks.get(key).loadType = 0;
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
                CaseStoryService.getInstance().openStatistic(new OpenStatisticCallback() {
                    @Override
                    public void onSuccess() {
                        isRefreshing = false;
                    }

                    @Override
                    public void onError() {

                    }
                });
                handler.postDelayed(queueStoryReadRunnable, 100);
                return;
            }
            synchronized (getInstance().pageTasksLock) {
                getInstance().pageTasks.get(key).loadType = 1;

                final Callable _ff = new Callable() {
                    @Override
                    public Object call() throws Exception {
                        for (String url : getInstance().pageTasks.get(key).urls) {
                            downloadImageByUrl(getInstance().context, url, key.first, key.second);
                        }
                        for (String url : getInstance().pageTasks.get(key).videoUrls) {
                            downloadVideoByUrl(getInstance().context, url, key.first, key.second);
                        }
                        return null;
                    }
                };
                final Future<Story> ff = imageExecutor.submit(_ff);
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ff.get();
                            getInstance().pageTasks.get(key).loadType = 2;
                            handler.postDelayed(queuePageReadRunnable, 200);
                        } catch (Throwable t) {
                            getInstance().pageTasks.get(key).loadType = 0;
                            handler.postDelayed(queuePageReadRunnable, 200);
                        }
                    }
                });
            }
        }
    };


    private static Handler handler = new Handler();


    public int findIndexByStoryId(int id) {
        if (stories != null) {
            for (int i = 0; i < stories.size(); i++) {
                if (stories.get(i).id == id) return i;
            }
        }
        return -1;
    }

    public Story findItemByStoryId(int id) {
       /* if (onboardStories != null) {
            for (int i = 0; i < onboardStories.size(); i++) {
                if (onboardStories.get(i).id == id) return onboardStories.get(i);
            }
        }*/
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
            INSTANCE = new StoryDownloader(CaseStoryManager.getInstance().getContext());
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

    public static void downloadVideoByUrl(final Context context, final String url, final int StoryId, int ind) {
        final Future<File> ff = imageExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return Downloader.downVideo(context, url, FileType.STORY_IMAGE, StoryId, Sizes.getScreenSize());
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
        });

    }

    public static void downloadImageByUrl(final Context context, final String url, final int StoryId, int ind) {
        final Future<File> ff = imageExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return Downloader.downAndCompressImg(context, url, FileType.STORY_IMAGE, StoryId, Sizes.getScreenSize());
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
        });

    }

    @WorkerThread
    public void startedLoading(final List<Story> stories) {
        if (StatisticSession.needToUpdate()) {
            CaseStoryService.getInstance().openStatistic(new OpenStatisticCallback() {
                @Override
                public void onSuccess() {
                    startedLoading(stories);
                }

                @Override
                public void onError() {

                }
            });
            return;
        }
        ExecutorService executorService = startNetExecutor;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    int currentPriority = 0;
                    synchronized (storyTasksLock) {
                        for (int i = 0; i < Math.min(stories.size(), 4); i++) {
                            if (findIndexByStoryId(stories.get(i).id) == -1) continue;
                            if (storyTasks.get(stories.get(i).id) != null) continue;
                            Story storyResponse = ApiClient.getApi().getStoryById(Integer.toString(
                                    stories.get(i).id),
                                    StatisticSession.getInstance().id,
                                    CaseStoryManager.getInstance().getApiKey(),
                                    "slides_html,layout,slides_duration")
                                    .execute().body();


                            stories.set(findIndexByStoryId(storyResponse.id), storyResponse);
                            final int it = i;
                            storyTasks.put(stories.get(i).id, new StoryTask() {{
                                priority = it;
                                loadType = 3;
                            }});
                        }
                    }
                    synchronized (pageTasksLock) {
                        for (int i = 0; i < Math.min(stories.size(), 4); i++) {
                            for (int j = 0; j < Math.min(stories.get(i).pages.size(), 2); j++) {
                                final int cPriority = currentPriority;
                                final List<String> links = HtmlParser.getSrcUrls(stories.get(i).pages.get(j));
                                final List<String> videos = HtmlParser.getSrcVideoUrls(stories.get(i).pages.get(j));
                                pageTasks.put(new Pair<>(stories.get(i).id, j), new StoryPageTask() {{
                                    loadType = 0;
                                    urls = links;
                                    videoUrls = videos;
                                    priority = cPriority;
                                }});
                                currentPriority++;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @WorkerThread
    public void uploadingAdditional(final List<Story> newStories) {
        boolean sync = false;
        addStories(newStories);
        synchronized (pageTasksLock) {
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
        synchronized (pageTasksLock) {
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
        stopStartedLoading = true;
        if (this.stories == null || this.stories.isEmpty()) {
            this.stories = new ArrayList<>();
            this.stories.addAll(stories);
        } else {
            for (int i = 0; i < stories.size(); i++) {
                boolean newNar = true;
                for (int j = 0; j < this.stories.size(); j++) {
                    if (this.stories.get(j).id == stories.get(i).id) {
                        this.stories.get(j).isReaded = stories.get(i).isReaded;
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
        synchronized (storyTasksLock) {
            int i = 0;
            int c = 1;
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
                if (storyTasks.get(story.id) == null) {
                    final int pr = i;
                    storyTasks.put(story.id, new StoryTask() {{
                        loadType = 0;
                        priority = pr;
                    }});
                } else if (storyTasks.get(story.id).loadType == 3) {

                    storyTasks.get(story.id).priority = i;
                    storyTasks.get(story.id).loadType = 0;
                }
            }
        }
    }

    public void renewPriorities(final int currentStoryIndex) throws IOException, InterruptedException {
        this.currentStoryIndex = currentStoryIndex;
        runnableExecutor.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (pageTasksLock) {
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
                        if (storyTasks.get(story.id) != null) {
                            if (storyTasks.get(story.id).loadType == 0) {
                                storyTasks.get(story.id).priority = i;
                            } else if (storyTasks.get(story.id).loadType == 3) {
                                storyTasks.get(story.id).priority = i;
                                storyTasks.get(story.id).loadType = 0;
                            }
                        }
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
                synchronized (storyTasksLock) {
                    int i = 0;
                    int c = 1;
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
                                    Point size) throws IOException {
        FileCache cache = FileCache.INSTANCE;

        File img = cache.getStoredFile(con, cropUrl(url), FileType.STORY_IMAGE, sourceId, null);
        if (img.exists()) {
            return img;
        }
        byte[] bytes = null;
        Log.e("StoryImageUrl", url);
        bytes = ApiClient.getImageApiOk().newCall(
                new Request.Builder().url(url).build()).execute().body().bytes();
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
            throw new IOException("опять они всё сломали");
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
