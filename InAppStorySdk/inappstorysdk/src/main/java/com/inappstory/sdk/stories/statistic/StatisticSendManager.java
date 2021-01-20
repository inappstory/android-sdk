package com.inappstory.sdk.stories.statistic;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.StatisticTask;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StatisticSendManager {
    private static StatisticSendManager INSTANCE;

    private static final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    public static final String NEXT = "next";
    public static final String PREV = "prev";
    public static final String ONBOARDING = "onboarding";
    public static final String LIST = "list";
    public static final String DIRECT = "direct";
    public static final String FAVORITE = "favorite";


    public static final String AUTO = "auto-close";
    public static final String CLICK = "button-close";
    public static final String SWIPE = "swipe-close";
    public static final String CUSTOM = "custom-close";

    private Object statisticTasksLock = new Object();

    private ArrayList<StatisticTask> tasks = new ArrayList<>();

    public void addTask(StatisticTask task) {
        if (1 == 1) return;
        synchronized (statisticTasksLock) {
            tasks.add(task);
            saveTasksSP();
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

    public static StatisticSendManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatisticSendManager();
        }
        return INSTANCE;
    }

    public static final String TASKS_KEY = "statisticTasks";

    public void cleanTasks() {
        synchronized (statisticTasksLock) {
            tasks.clear();
            SharedPreferencesAPI.remove(TASKS_KEY);
        }
    }


    private static Handler handler = new Handler();
    private static HandlerThread thread;

    @CsSubscribe
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        try {
            if (event.isWithBackground()) {
                isBackgroundPause = true;
                pauseTimer = System.currentTimeMillis();
            }
        } catch (Exception e) {

        }
    }

    long pauseTimer = -1;
    boolean isBackgroundPause = false;

    boolean backPaused = false;

    @CsSubscribe
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (event.isWithBackground()) {
            if (isBackgroundPause) {
                pauseTime += (System.currentTimeMillis() - pauseTimer);
            }
            isBackgroundPause = false;
        }
    }


    public StatisticSendManager() {
        thread = new HandlerThread("StatisticSendManagerThread" + System.currentTimeMillis());
        thread.start();
        handler = new Handler(thread.getLooper());
        String tasksJson = SharedPreferencesAPI.getString(TASKS_KEY);
        if (tasksJson != null) {
            tasks = JsonParser.listFromJson(tasksJson, StatisticTask.class);
        } else {
            tasks = new ArrayList<>();
        }
        handler.postDelayed(queueTasksRunnable, 100);
    }

    private static Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            if (getInstance().tasks == null || getInstance().tasks.size() == 0 || InAppStoryService.getInstance() == null
                    || !InAppStoryService.getInstance().isConnected()) {
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

    public void sendOpenStory(final int i, final String w) {
        currentTime = System.currentTimeMillis();
        pauseTime = 0;
        StatisticTask task = new StatisticTask();
        task.event = prefix + "open";
        task.storyId = Integer.toString(i);
        task.whence = w;
        generateBase(task);
        addTask(task);
    }

    public void generateBase(StatisticTask task) {
       //Context context = InAppStoryManager.getInstance().getContext();
     /*   task.app = new PhoneAppData();
        task.app.platform = "android";
        // String deviceId = Settings.Secure.getString(context.getContentResolver(),
        //         Settings.Secure.ANDROID_ID);// Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        task.app.model = Build.MODEL;
        task.app.manufacturer = Build.MANUFACTURER;
        task.app.brand = Build.BRAND;
        task.app.screenWidth = Sizes.getScreenSize().x;
        task.app.screenHeight = Sizes.getScreenSize().y;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        task.app.screenDpi = (int) metrics.density * 160;
        task.app.osVersion = Build.VERSION.CODENAME;
        task.app.osSdkVersion = Build.VERSION.SDK_INT;
        task.app.appPackageId = context.getPackageName();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        task.app.appVersion = (pInfo != null ? pInfo.versionName : "");
        task.app.appBuild = (pInfo != null ? pInfo.versionCode : 1);*/
        task.sessionId = StatisticSession.getInstance().id;
        task.userId = InAppStoryManager.getInstance().getUserId();
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

        StatisticTask task = new StatisticTask();
        task.event = prefix + "close";
        task.storyId = Integer.toString(i);
        task.cause = c;
        task.slideIndex = si;
        task.slideTotal = st;
        task.durationMs = currentTime - pauseTime;

        generateBase(task);
        addTask(task);
        pauseTime = 0;
    }

    public void sendCloseStory(final int i, final String c, final Integer si, final Integer st, final Long t) {

        StatisticTask task = new StatisticTask();

        task.event = prefix + "close";
        task.storyId = Integer.toString(i);
        task.cause = c;
        task.slideIndex = si;
        task.slideTotal = st;
        task.durationMs = t;

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


    private static void sendTask(final StatisticTask task) {
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
                            task.layoutIndex).execute();
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
