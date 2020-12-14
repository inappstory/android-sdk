package com.inappstory.sdk.stories.statistic;

import android.os.Handler;
import android.os.HandlerThread;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.Response;
import com.inappstory.sdk.stories.api.models.StatisticSession;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.inappstory.sdk.InAppStoryService.EXPAND_STRING;

public class StatisticSendManager {
    private static StatisticSendManager INSTANCE;

    private static final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    private Object statisticTasksLock = new Object();

    private ArrayList<StatisticTask> tasks = new ArrayList<>();

    public void addTask(StatisticTask task) {
        synchronized (statisticTasksLock) {
            tasks.add(task);
            saveTasksSP();
        }
    }

    private static void saveTasksSP() {
        try {
            SharedPreferencesAPI.saveString(TASKS_KEY, JsonParser.getJson(getInstance().tasks));
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
            if (getInstance().tasks.size() == 0 || InAppStoryService.getInstance() == null
                    || !InAppStoryService.getInstance().isConnected()) {
                handler.postDelayed(queueTasksRunnable, 100);
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

    private static void sendTask(StatisticTask task) {
        try {
            final Callable<Boolean> _ff = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Response response = NetworkClient.getApi().getStoryById("",
                            StatisticSession.getInstance().id, 1,
                            InAppStoryManager.getInstance().getApiKey(),
                            EXPAND_STRING).execute();

                    //change request here

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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
