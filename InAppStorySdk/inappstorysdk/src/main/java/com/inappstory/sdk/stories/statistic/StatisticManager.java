package com.inappstory.sdk.stories.statistic;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.CurrentState;
import com.inappstory.sdk.stories.api.models.StatisticSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class StatisticManager {
    private static StatisticManager INSTANCE;

    private static final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    public static final String NEXT = "next";
    public static final String APPCLOSE = "app-close";
    public static final String PREV = "prev";
    public static final String ONBOARDING = "onboarding";
    public static final String LIST = "list";
    public static final String DIRECT = "direct";
    public static final String FAVORITE = "favorite";


    public static final String AUTO = "auto-close";
    public static final String CLICK = "button-close";
    public static final String BACK = "back";
    public static final String SWIPE = "swipe-close";
    public static final String CUSTOM = "custom-close";

    private Object statisticTasksLock = new Object();

    public ArrayList<StatisticTask> getTasks() {
        return tasks;
    }

    public ArrayList<StatisticTask> getFaketasks() {
        return faketasks;
    }

    private ArrayList<StatisticTask> tasks = new ArrayList<>();
    private ArrayList<StatisticTask> faketasks = new ArrayList<>();


    public void addTask(StatisticTask task) {
        addTask(task, false);
    }


    public void addTask(StatisticTask task, boolean force) {
        if (!force && InAppStoryService.isNotNull() &&
                !InAppStoryService.getInstance().getSendNewStatistic()) return;
        synchronized (statisticTasksLock) {
            tasks.add(task);
            saveTasksSP();
        }

    }


    public void addFakeTask(StatisticTask task) {
        if (InAppStoryService.isNotNull() &&
                !InAppStoryService.getInstance().getSendNewStatistic()) return;
        synchronized (statisticTasksLock) {
            faketasks.add(task);
            saveFakeTasksSP();
        }
    }

    public static void saveTasksSP() {
        try {
            ArrayList<StatisticTask> ltasks = new ArrayList<>();
            ltasks.addAll(getInstance().tasks);
            SharedPreferencesAPI.saveString(TASKS_KEY, JsonParser.getJson(ltasks));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveFakeTasksSP() {
        try {
            ArrayList<StatisticTask> ltasks = new ArrayList<>();
            ltasks.addAll(getInstance().faketasks);
            SharedPreferencesAPI.saveString(FAKE_TASKS_KEY, JsonParser.getJson(ltasks));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StatisticManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatisticManager();
            INSTANCE.init();
        }
        return INSTANCE;
    }

    public static final String TASKS_KEY = "statisticTasks";
    public static final String FAKE_TASKS_KEY = "fakeStatisticTasks";

    public void cleanTasks() {
        synchronized (statisticTasksLock) {
            tasks.clear();
            SharedPreferencesAPI.remove(TASKS_KEY);
            faketasks.clear();
            SharedPreferencesAPI.remove(FAKE_TASKS_KEY);
        }
    }


    private Handler handler = new Handler();
    private HandlerThread thread;

    public void pauseStoryEvent(boolean withBg) {
        if (INSTANCE != this) return;
        if (withBg) {
            isBackgroundPause = true;
            pauseTimer = System.currentTimeMillis();
        }
    }

    long pauseTimer = -1;
    boolean isBackgroundPause = false;

    boolean backPaused = false;

    public void resumeStoryEvent(boolean withBg) {
        if (INSTANCE != this) return;
        if (withBg) {
            if (isBackgroundPause) {
                pauseTime += (System.currentTimeMillis() - pauseTimer);
            }
            isBackgroundPause = false;
        } else {
        }
    }




    public void init() {
        thread = new HandlerThread("SSMThread" + System.currentTimeMillis());
        thread.start();
        handler = new Handler(thread.getLooper());
        String tasksJson = SharedPreferencesAPI.getString(TASKS_KEY);
        String fakeTasksJson = SharedPreferencesAPI.getString(FAKE_TASKS_KEY);
        synchronized (statisticTasksLock) {
            if (tasksJson != null) {
                tasks = JsonParser.listFromJson(tasksJson, StatisticTask.class);
            } else {
                tasks = new ArrayList<>();
            }
            if (fakeTasksJson != null) {
                tasks.addAll(JsonParser.listFromJson(fakeTasksJson, StatisticTask.class));
            }
            for (StatisticTask task : tasks) {
                task.isFake = false;
            }

            SharedPreferencesAPI.remove(FAKE_TASKS_KEY);
        }
        handler.postDelayed(queueTasksRunnable, 100);
    }

    public StatisticManager() {

    }

    public void cleanFakeEvents() {
        synchronized (statisticTasksLock) {
            faketasks.clear();
            SharedPreferencesAPI.remove(FAKE_TASKS_KEY);
        }
    }

    private Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            if (getInstance().tasks == null || getInstance().tasks.size() == 0 || InAppStoryService.isNull()
                    || !InAppStoryService.isConnected()) {
                handler.postDelayed(queueTasksRunnable, 100);
                return;
            }
            StatisticTask task;
            synchronized (getInstance().statisticTasksLock) {
                task = getInstance().tasks.get(0);
                getInstance().tasks.remove(0);
                saveTasksSP();
            }
            if (task != null) {
                sendTask(task);
            }
        }
    };


    public ArrayList<Integer> viewed = new ArrayList<>();

    String prefix = "";

    public void sendViewStory(final int i, final String w) {
        if (!viewed.contains(i)) {
            StatisticTask task = new StatisticTask();
            task.event = prefix + "view";
            task.storyId = Integer.toString(i);
            task.whence = w;
            generateBase(task);
            addTask(task);
            viewed.add(i);
        }
    }


    public void sendGoodsOpen(final int i, final int si, final String wi) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "w-goods-open";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.widgetId = wi;
        generateBase(task);
        addTask(task, true);
    }

    public void sendGoodsClick(final int i, final int si,
                               final String wi, final String sku) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "w-goods-click";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.widgetId = wi;
        task.widgetValue = sku;
        generateBase(task);
        addTask(task, true);
    }

    public void sendViewStory(ArrayList<Integer> ids, final String w) {
        ArrayList<String> localIds = new ArrayList<>();
        for (int i : ids) {
            if (!viewed.contains(i)) {
                localIds.add(Integer.toString(i));
                viewed.add(i);
            }
        }
        if (localIds.size() > 0) {
            StatisticTask task = new StatisticTask();
            task.event = prefix + "view";
            task.storyId = TextUtils.join(",", localIds);
            task.whence = w;
            generateBase(task);
            addTask(task);
        }
    }

    HashMap<Integer, Long> cTimes;

    public void sendOpenStory(final int i, final String w) {
        if (cTimes == null) cTimes = new HashMap<>();
        cTimes.put(i, System.currentTimeMillis());
        pauseTime = 0;
        StatisticTask task = new StatisticTask();
        task.event = prefix + "open";
        task.storyId = Integer.toString(i);
        task.whence = w;
        generateBase(task);
        addTask(task);
    }

    public void generateBase(StatisticTask task) {
        task.sessionId = StatisticSession.getInstance().id;
        if (InAppStoryService.isNotNull())
            task.userId = InAppStoryService.getInstance().getUserId();
        task.timestamp = System.currentTimeMillis() / 1000;
    }

    public void sendReadStory(final int i) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "read";
        task.storyId = Integer.toString(i);

        generateBase(task);
        addTask(task);

    }

    public void sendCloseStory(final int i, final String c, final Integer si, final Integer st) {
        sendCurrentState();
        if (cTimes == null) cTimes = new HashMap<>();
        Long tm = cTimes.get(i) != null ? cTimes.get(i) : 0;
        StatisticTask task = new StatisticTask();
        task.event = prefix + "close";
        task.storyId = Integer.toString(i);
        task.cause = c;
        task.slideIndex = si;
        task.slideTotal = st;
        task.durationMs = System.currentTimeMillis() - tm - pauseTime;
        generateBase(task);
        addTask(task);
        pauseTime = 0;
    }

    public CurrentState currentState;

    static Object csLock = new Object();

    public void sendCurrentState() {
        synchronized (csLock) {
            if (currentState != null) {
                sendViewSlide(currentState.storyId, currentState.slideIndex, System.currentTimeMillis() - currentState.startTime - currentState.storyPause);
            }
            currentState = null;
        }
    }

    public void createCurrentState(final int stId, final int ind) {
        synchronized (csLock) {
            pauseTime = 0;
            currentState = new CurrentState();
            currentState.storyId = stId;
            currentState.slideIndex = ind;
            currentState.startTime = System.currentTimeMillis();
        }
    }


    public void sendCloseStory(final int i, final String c, final Integer si, final Integer st, final Long t) {
        sendCurrentState();
        if (cTimes == null) cTimes = new HashMap<>();
        Long tm = cTimes.get(i) != null ? cTimes.get(i) : 0;
        StatisticTask task = new StatisticTask();
        task.event = prefix + "close";
        task.storyId = Integer.toString(i);
        task.cause = c;
        task.slideIndex = si;
        task.slideTotal = st;
        task.durationMs = System.currentTimeMillis() - tm - t;
        generateBase(task);
        addTask(task);
        pauseTime = 0;

    }

    public void addFakeEvents(final int i, final Integer si, final Integer st) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "slide";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.durationMs = System.currentTimeMillis() - (currentState != null ? currentState.startTime : 0);
        task.isFake = true;
        generateBase(task);
        addFakeTask(task);

        if (cTimes == null) cTimes = new HashMap<>();
        Long tm = cTimes.get(i) != null ? cTimes.get(i) : 0;
        StatisticTask task2 = new StatisticTask();
        task2.event = prefix + "close";
        task2.storyId = Integer.toString(i);
        task2.cause = StatisticManager.APPCLOSE;
        task2.slideIndex = si;
        task2.isFake = true;
        task2.slideTotal = st;
        task2.durationMs = System.currentTimeMillis() - tm - pauseTime;
        generateBase(task2);
        addFakeTask(task2);
    }

    public void sendDeeplinkStory(final int i, String link) {

        StatisticTask task = new StatisticTask();
        task.event = prefix + "link";
        task.storyId = Integer.toString(i);
        task.target = link;
        generateBase(task);
        addTask(task);

    }


    public void sendClickLink(int storyId) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "w-link";

        generateBase(task);
        addTask(task);
    }

    public void sendLikeStory(final int i, final int si) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "like";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        generateBase(task);
        addTask(task);

    }

    public void sendDislikeStory(final int i, final int si) {

        StatisticTask task = new StatisticTask();
        task.event = prefix + "dislike";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        generateBase(task);
        addTask(task);

    }

    public void sendFavoriteStory(final int i, final int si) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "favorite";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        generateBase(task);
        addTask(task);
    }

    public long currentTime = -1;
    public long pauseTime = 0;


    public void sendViewSlide(final int i, final int si, final Long t) {
        if (t <= 0) return;
        StatisticTask task = new StatisticTask();
        task.event = prefix + "slide";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.durationMs = t;
        generateBase(task);
        addTask(task);

    }

    public void sendShareStory(final int i, final int si) {
        StatisticTask task = new StatisticTask();
        task.event = prefix + "share";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        generateBase(task);
        addTask(task);

    }


    public void sendWidgetStoryEvent(final String name, final String data) {
        StatisticTask task = JsonParser.fromJson(data, StatisticTask.class);
        task.event = name;
        generateBase(task);
        addTask(task);
    }

    public void sendGameEvent(final String name, final String data) {
        StatisticTask task = JsonParser.fromJson(data, StatisticTask.class);
        task.event = name;
        generateBase(task);
        addTask(task);
    }


    private void sendTask(final StatisticTask task) {
        try {
            final Callable<Boolean> _ff = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Response response = NetworkClient.getStatApi().sendStat(
                            task.event,
                            task.sessionId,
                            task.userId,
                            task.timestamp,
                            task.storyId,
                            task.whence,
                            task.cause,
                            task.slideIndex,
                            task.slideTotal,
                            task.durationMs,
                            task.widgetId,
                            task.widgetLabel,
                            task.widgetValue,
                            task.widgetAnswer,
                            task.widgetAnswerLabel,
                            task.widgetAnswerScore,
                            task.layoutIndex,
                            task.target).execute();
                    if (response.code > 199 && response.code < 210) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            final Future<Boolean> ff = netExecutor.submit(_ff);
            runnableExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        ff.get();
                        handler.postDelayed(queueTasksRunnable, 100);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        handler.postDelayed(queueTasksRunnable, 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        handler.postDelayed(queueTasksRunnable, 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.postDelayed(queueTasksRunnable, 100);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.postDelayed(queueTasksRunnable, 100);
        }
    }


}
