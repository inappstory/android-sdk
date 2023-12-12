package com.inappstory.sdk.stories.statistic;

import static android.content.Context.TELEPHONY_SERVICE;
import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.GetUrl;
import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProfilingManager {
    ArrayList<ProfilingTask> tasks = new ArrayList<>();
    ArrayList<ProfilingTask> readyTasks = new ArrayList<>();
    private static ProfilingManager INSTANCE;

    Context context;
    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    public static ProfilingManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfilingManager();
            INSTANCE.init();
        }
        return INSTANCE;
    }

    private final Object tasksLock = new Object();

    public String addTask(String name, String hash) {
        if (InAppStoryService.isNull()) return "";
        ProfilingTask task = new ProfilingTask();
        task.uniqueHash = hash;
        task.name = name;
        task.startTime = System.currentTimeMillis();
        task.isAllowToForceSend = isAllowToSend();
        synchronized (tasksLock) {
            for (ProfilingTask hasTask : tasks) {
                if (hasTask.uniqueHash.equals(hash)) {
                    hasTask.startTime = System.currentTimeMillis();
                    return hash;
                }
            }
            tasks.add(task);
        }
        return hash;
    }

    public String addTask(String name) {
        if (InAppStoryService.isNull()) return "";
        String hash = randomUUID().toString();
        ProfilingTask task = new ProfilingTask();
        task.sessionId = Session.getInstance().id;
        task.isAllowToForceSend = isAllowToSend();
        task.userId = InAppStoryService.getInstance().getUserId();
        task.uniqueHash = hash;
        task.name = name;
        task.startTime = System.currentTimeMillis();
        synchronized (tasksLock) {
            tasks.add(task);
        }
        return hash;
    }

    public void setReady(String hash, boolean force) {
        synchronized (tasksLock) {
            ProfilingTask readyTask = null;
            for (ProfilingTask task : tasks) {
                if (task.uniqueHash != null && task.uniqueHash.equals(hash)) {
                    readyTask = task;
                    break;
                }
            }
            if (readyTask == null) return;
            tasks.remove(readyTask);
            if (hash == null || hash.isEmpty()) return;
            readyTask.endTime = System.currentTimeMillis();
            readyTask.isReady = true;

            if (force && readyTask.isAllowToForceSend) {
                final ProfilingTask finalReadyTask = readyTask;
                runnableExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendTiming(finalReadyTask);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                readyTasks.add(readyTask);
            }
        }
    }

    public void setReady(String hash) {
        setReady(hash, false);
    }

    public void cleanTasks() {
        synchronized (tasksLock) {
            tasks.clear();
        }
    }

    LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);


    public void init() {
        context = InAppStoryManager.getInstance().getContext();
        loopedExecutor.init(queueTasksRunnable);
    }

    private Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            boolean readyIsEmpty = false;
            synchronized (getInstance().tasksLock) {
                readyIsEmpty = getInstance().readyTasks == null || getInstance().readyTasks.size() == 0;
            }
            if (readyIsEmpty || !InAppStoryService.isConnected() || !isAllowToSend()) {
                loopedExecutor.freeExecutor();
                return;
            }
            ProfilingTask task;
            synchronized (getInstance().tasksLock) {
                task = getInstance().readyTasks.get(0);
                getInstance().readyTasks.remove(0);
            }

            if (task != null) {
                try {
                    sendTiming(task);
                } catch (Exception e) {
                }
            }
            loopedExecutor.freeExecutor();
        }
    };

    private boolean isAllowToSend() {
        return !Session.needToUpdate()
                && Session.getInstance().isAllowProfiling();
    }

    private String getCC() {
        if (context == null) return null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        if (countryCodeValue == null || countryCodeValue.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                countryCodeValue = context.
                        getResources().getConfiguration().getLocales().get(0).getCountry();
            } else {
                countryCodeValue = context.
                        getResources().getConfiguration().locale.getCountry();
            }
        }
        return countryCodeValue.toUpperCase();
    }


    private int sendTiming(ProfilingTask task) throws Exception {

        Map<String, String> qParams = new HashMap<>();
        qParams.put("s", (task.sessionId != null && !task.sessionId.isEmpty()) ? task.sessionId :
                Session.getInstance().id);
        qParams.put("u", task.userId != null ? task.userId : "");
        String cc = getCC();
        qParams.put("ts", "" + System.currentTimeMillis() / 1000);
        if (cc != null)
            qParams.put("c", cc);
        qParams.put("n", task.name);
        qParams.put("v", "" + (task.endTime - task.startTime));
        HttpURLConnection connection = (HttpURLConnection) new GetUrl()
                .fromRequest(
                        new Request.Builder()
                                .url("profiling/timing")
                                .vars(qParams)
                                .build()
                )
                .openConnection();
        connection.setRequestProperty("User-Agent", new UserAgent().generate(context));
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod("POST");
        int statusCode = connection.getResponseCode();
        InAppStoryManager.showDLog("InAppStory_Network", connection.getURL().toString() + " \nStatus Code: " + statusCode);
        connection.disconnect();
        return statusCode;

    }
}
