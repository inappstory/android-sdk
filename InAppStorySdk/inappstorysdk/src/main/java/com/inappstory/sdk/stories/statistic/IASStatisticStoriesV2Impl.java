package com.inappstory.sdk.stories.statistic;

import android.text.TextUtils;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticStoriesV2;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.CurrentV2StatisticState;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class IASStatisticStoriesV2Impl implements IASStatisticStoriesV2 {
    private static IASStatisticStoriesV2Impl INSTANCE;
    private final IASCore core;

    public void disabled(boolean disabled) {
        this.disabled = disabled;
    }

    private boolean disabled;

    public IASStatisticStoriesV2Impl(IASCore core) {
        this.core = core;
        init();
    }

    private final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

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

    private final Object statisticTasksLock = new Object();

    public ArrayList<StoryStatisticV2Task> getTasks() {
        return tasks;
    }

    public ArrayList<StoryStatisticV2Task> getFaketasks() {
        return faketasks;
    }

    private ArrayList<StoryStatisticV2Task> tasks = new ArrayList<>();
    private ArrayList<StoryStatisticV2Task> faketasks = new ArrayList<>();


    private void addTask(StoryStatisticV2Task task) {
        addTask(task, false);
    }


    private void addTask(StoryStatisticV2Task task, boolean force) {
        if (!force && disabled) return;
        synchronized (statisticTasksLock) {
            tasks.add(task);
            saveTasksSP();
        }
    }


    private void addFakeTask(StoryStatisticV2Task task) {
        if (disabled) return;
        synchronized (statisticTasksLock) {
            faketasks.add(task);
            saveFakeTasksSP();
        }
    }

    private void saveTasksSP() {
        try {
            ArrayList<StoryStatisticV2Task> ltasks = new ArrayList<>();
            ltasks.addAll(tasks);
            core.sharedPreferencesAPI().saveString(TASKS_KEY, JsonParser.getJson(ltasks));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFakeTasksSP() {
        try {
            ArrayList<StoryStatisticV2Task> ltasks = new ArrayList<>();
            ltasks.addAll(faketasks);
            core.sharedPreferencesAPI().saveString(FAKE_TASKS_KEY, JsonParser.getJson(ltasks));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String TASKS_KEY = "statisticTasks";
    private final String FAKE_TASKS_KEY = "fakeStatisticTasks";

    @Override
    public boolean disabled() {
        return false;
    }

    public void cleanTasks() {
        synchronized (statisticTasksLock) {
            tasks.clear();
            core.sharedPreferencesAPI().remove(TASKS_KEY);
            faketasks.clear();
            core.sharedPreferencesAPI().remove(FAKE_TASKS_KEY);
        }
    }


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

    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);


    private void init() {
        String tasksJson = core.sharedPreferencesAPI().getString(TASKS_KEY);
        String fakeTasksJson = core.sharedPreferencesAPI().getString(FAKE_TASKS_KEY);
        synchronized (statisticTasksLock) {
            if (tasksJson != null) {
                tasks = JsonParser.listFromJson(tasksJson, StoryStatisticV2Task.class);
            } else {
                tasks = new ArrayList<>();
            }
            if (fakeTasksJson != null) {
                tasks.addAll(JsonParser.listFromJson(fakeTasksJson, StoryStatisticV2Task.class));
            }
            for (StoryStatisticV2Task task : tasks) {
                task.isFake = false;
            }

            core.sharedPreferencesAPI().remove(FAKE_TASKS_KEY);
        }
        loopedExecutor.init(queueTasksRunnable);
    }

    public void cleanFakeEvents() {
        synchronized (statisticTasksLock) {
            faketasks.clear();
            core.sharedPreferencesAPI().remove(FAKE_TASKS_KEY);
        }
    }


    private final Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            if (tasks == null || tasks.size() == 0) {
                loopedExecutor.freeExecutor();
                return;
            }
            StoryStatisticV2Task task;
            synchronized (statisticTasksLock) {
                task = tasks.get(0);
                tasks.remove(0);
                saveTasksSP();
            }
            if (task != null) {
                sendTask(task);
            }
        }
    };


    public ArrayList<Integer> viewed = new ArrayList<>();

    String prefix = "";

    public void sendViewStory(final int storyId, final String whence, final String feedId) {
        if (!viewed.contains(storyId)) {
            StoryStatisticV2Task task = new StoryStatisticV2Task();
            task.event = prefix + "view";
            task.storyId = Integer.toString(storyId);
            task.feedId = feedId;
            task.whence = whence;
            generateBase(task);
            addTask(task);
            viewed.add(storyId);
        }
    }


    public void sendGoodsOpen(final int storyId,
                              final int slideIndex,
                              final String widgetId,
                              final String feedId) {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "w-goods-open";
        task.storyId = Integer.toString(storyId);
        task.slideIndex = slideIndex;
        task.widgetId = widgetId;
        task.feedId = feedId;
        generateBase(task);
        addTask(task, InAppStoryManager.getInstance() != null &&
                InAppStoryManager.getInstance().isSendStatistic());
    }

    public void sendGoodsClick(final int i, final int si,
                               final String wi, final String sku,
                               final String feedId) {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "w-goods-click";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.feedId = feedId;
        task.widgetId = wi;
        task.widgetValue = sku;
        generateBase(task);
        addTask(task, InAppStoryManager.getInstance() != null &&
                InAppStoryManager.getInstance().isSendStatistic());
    }

    public void sendViewStory(List<Integer> ids, final String w,
                              final String feedId) {
        ArrayList<String> localIds = new ArrayList<>();
        for (int i : ids) {
            if (!viewed.contains(i)) {
                localIds.add(Integer.toString(i));
                viewed.add(i);
            }
        }
        if (localIds.size() > 0) {
            StoryStatisticV2Task task = new StoryStatisticV2Task();
            task.feedId = feedId;
            task.event = prefix + "view";
            task.storyId = TextUtils.join(",", localIds);
            task.whence = w;
            generateBase(task);
            addTask(task);
        }
    }

    Map<Integer, Long> cTimes;

    public void sendOpenStory(final int i, final String w,
                              final String feedId) {
        if (cTimes == null) cTimes = new HashMap<>();
        cTimes.put(i, System.currentTimeMillis());
        pauseTime = 0;
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.feedId = feedId;
        task.event = prefix + "open";
        task.storyId = Integer.toString(i);
        task.whence = w;
        generateBase(task);
        addTask(task);
    }

    private void generateBase(StoryStatisticV2Task task) {

        task.userId = ((IASDataSettingsHolder) core.settingsAPI()).userId();
        task.sessionId = core.sessionManager().getSession().getSessionId();
        task.timestamp = System.currentTimeMillis() / 1000;
    }

    public void sendCloseStory(final int i,
                               final String c,
                               final Integer si,
                               final Integer st,
                               final String feedId) {
        sendCurrentState();
        if (cTimes == null) cTimes = new HashMap<>();
        Long tm = cTimes.get(i) != null ? cTimes.get(i) : 0L;
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "close";
        task.storyId = Integer.toString(i);
        task.cause = c;
        task.slideIndex = si;
        task.feedId = feedId;
        task.slideTotal = st;
        task.durationMs = System.currentTimeMillis() - tm - pauseTime;
        generateBase(task);
        addTask(task);
        pauseTime = 0;
    }

    private CurrentV2StatisticState currentState;

    static Object csLock = new Object();

    public void sendCurrentState() {
        synchronized (csLock) {
            if (currentState != null) {
                sendViewSlide(currentState.storyId,
                        currentState.slideIndex,
                        System.currentTimeMillis() - currentState.startTime - currentState.storyPause,
                        currentState.feedId);
            }
            currentState = null;
        }
    }

    public void createCurrentState(final int stId,
                                   final int ind,
                                   final String feedId) {
        synchronized (csLock) {
            pauseTime = 0;
            currentState = new CurrentV2StatisticState();
            currentState.storyId = stId;
            currentState.slideIndex = ind;
            currentState.feedId = feedId;
            currentState.startTime = System.currentTimeMillis();
        }
    }

    public void addFakeEvents(final int i,
                              final Integer si,
                              final Integer st,
                              final String feedId) {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "slide";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.durationMs = System.currentTimeMillis() - (currentState != null ? currentState.startTime : 0);
        task.isFake = true;
        task.feedId = feedId;
        generateBase(task);
        addFakeTask(task);

        if (cTimes == null) cTimes = new HashMap<>();
        Long tm = cTimes.get(i) != null ? cTimes.get(i) : 0L;
        StoryStatisticV2Task task2 = new StoryStatisticV2Task();
        task2.event = prefix + "close";
        task2.storyId = Integer.toString(i);
        task2.cause = IASStatisticStoriesV2Impl.APPCLOSE;
        task2.slideIndex = si;
        task2.isFake = true;
        task2.slideTotal = st;
        task2.feedId = feedId;
        task2.durationMs = System.currentTimeMillis() - tm - pauseTime;
        generateBase(task2);
        addFakeTask(task2);
    }

    public void sendDeeplinkStory(final int i,
                                  String link,
                                  final String feedId) {

        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "link";
        task.storyId = Integer.toString(i);
        task.target = link;
        task.feedId = feedId;
        generateBase(task);
        addTask(task);

    }


    public void sendClickLink(int storyId) {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "w-link";

        generateBase(task);
        addTask(task);
    }

    public void sendLikeStory(final int i,
                              final int si,
                              final String feedId) {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "like";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.feedId = feedId;
        generateBase(task);
        addTask(task);

    }

    public void sendDislikeStory(final int i,
                                 final int si,
                                 final String feedId) {

        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "dislike";
        task.feedId = feedId;
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        generateBase(task);
        addTask(task);

    }

    public void sendFavoriteStory(final int i,
                                  final int si,
                                  final String feedId) {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "favorite";
        task.storyId = Integer.toString(i);
        task.feedId = feedId;
        task.slideIndex = si;
        generateBase(task);
        addTask(task);
    }

    public long currentTime = -1;
    public long pauseTime = 0;


    public void sendViewSlide(final int i,
                              final int si,
                              final Long t,
                              final String feedId) {
        if (t <= 0) return;
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "slide";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.durationMs = t;
        task.feedId = feedId;
        generateBase(task);
        addTask(task);

    }

    public void sendShareStory(final int i,
                               final int si,
                               int mode,
                               final String feedId) {
        StoryStatisticV2Task task = new StoryStatisticV2Task();
        task.event = prefix + "share";
        task.storyId = Integer.toString(i);
        task.slideIndex = si;
        task.feedId = feedId;
        task.mode = mode;
        generateBase(task);
        addTask(task, InAppStoryManager.getInstance() != null &&
                InAppStoryManager.getInstance().isSendStatistic());

    }


    public void sendStoryWidgetEvent(final String name,
                                     final String data,
                                     final String feedId,
                                     final boolean forceStatSend) {
        try {
            StoryStatisticV2Task task = JsonParser.fromJson(data, StoryStatisticV2Task.class);
            task.event = name;
            task.feedId = feedId;
            task.fullData = data;
            generateBase(task);
            addTask(
                    task,
                    forceStatSend
            );
        } catch (Exception e) {
        }
    }

    public void sendGameEvent(final String name, final String data,
                              final String feedId) {
        StoryStatisticV2Task task = JsonParser.fromJson(data, StoryStatisticV2Task.class);
        task.event = name;
        task.feedId = feedId;
        generateBase(task);
        addTask(task);
    }

    @Override
    public void changeV2StatePauseTime(long newTime) {
        if (currentState != null) currentState.storyPause = newTime;
    }


    private void sendTask(final StoryStatisticV2Task task) {

        try {
            final Callable<Boolean> _ff = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Response response = core.network().execute(
                            core.network().getApi().sendStat(
                                    task.event,
                                    task.fullData,
                                    task.sessionId,
                                    task.userId,
                                    task.timestamp,
                                    task.feedId,
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
                                    task.target,
                                    task.mode
                            )
                    );
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loopedExecutor.freeExecutor();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loopedExecutor.freeExecutor();
        }
    }


}
