package com.inappstory.sdk.stories.statistic;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.StatisticSession;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.inappstory.sdk.network.NetworkClient.getUAString;
import static java.util.UUID.randomUUID;

public class ProfilingManager {
    ArrayList<ProfilingTask> tasks = new ArrayList<>();
    ArrayList<ProfilingTask> readyTasks = new ArrayList<>();
    private static ProfilingManager INSTANCE;


    private static final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    public static ProfilingManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfilingManager();
            INSTANCE.init();
        }
        return INSTANCE;
    }

    private Object tasksLock = new Object();

    public String addTask(String name, String hash) {
        ProfilingTask task = new ProfilingTask();
        task.uniqueHash = hash;
        task.name = name;
        task.startTime = System.currentTimeMillis();
        synchronized (tasksLock) {
            for (ProfilingTask hasTask : tasks) {
                if (hasTask.uniqueHash == hash) {
                    hasTask.startTime = System.currentTimeMillis();
                    return hash;
                }
            }
            tasks.add(task);
        }
        return hash;
    }

    public String addTask(String name) {
        String hash = randomUUID().toString();
        ProfilingTask task = new ProfilingTask();
        task.sessionId = StatisticSession.getInstance().id;
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
                if (task.uniqueHash.equals(hash)) {
                    readyTask = task;
                    break;
                }
            }
            if (readyTask == null) return;
            tasks.remove(readyTask);
            if (hash == null || hash.isEmpty()) return;
            readyTask.endTime = System.currentTimeMillis();
            readyTask.isReady = true;

            if (force) {
                try {
                    sendTiming(readyTask);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                readyTasks.add(readyTask);
            }
        }
    }

    public void setReady(String hash) {
        setReady(hash, false);
    }

    private Handler handler = new Handler();
    private HandlerThread thread;

    public void cleanTasks() {
        synchronized (tasksLock) {
            tasks.clear();
        }
    }

    public void init() {
        thread = new HandlerThread("ProfilingThread" + System.currentTimeMillis());
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.postDelayed(queueTasksRunnable, 100);
    }

    private Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            if (getInstance().readyTasks == null || getInstance().readyTasks.size() == 0
                    || StatisticSession.needToUpdate()
                    || (!StatisticSession.getInstance().allowProfiling)
                    || !InAppStoryService.isConnected()) {
                handler.postDelayed(queueTasksRunnable, 100);
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
            handler.postDelayed(queueTasksRunnable, 100);
        }
    };

    static URL getURL(String path, Map<String, String> queryParams) throws Exception {
        String url = NetworkClient.getInstance().getBaseUrl() + "profiling/" + path;
        String varStr = "";
        if (queryParams != null && queryParams.keySet().size() > 0) {
            for (Object key : queryParams.keySet()) {
                varStr += "&" + key + "=" + queryParams.get(key);
            }
            varStr = "?" + varStr.substring(1);
        }
        return new URL(url + varStr);
    }

    private int sendTiming(ProfilingTask task) throws Exception {
        TelephonyManager tm = (TelephonyManager)InAppStoryManager.getInstance().getContext().getSystemService(TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        if (countryCodeValue == null || countryCodeValue.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                countryCodeValue = InAppStoryManager.getInstance().getContext().
                        getResources().getConfiguration().getLocales().get(0).getCountry();
            } else {
                countryCodeValue = InAppStoryManager.getInstance().getContext().
                        getResources().getConfiguration().locale.getCountry();
            }
        }
        Map<String, String> qParams = new HashMap<>();
        qParams.put("s", (task.sessionId != null && !task.sessionId.isEmpty()) ? task.sessionId :
                StatisticSession.getInstance().id);
        qParams.put("u", InAppStoryService.getInstance().getUserId());
        qParams.put("ts", "" + System.currentTimeMillis() / 1000);
        qParams.put("c", countryCodeValue);
        qParams.put("n", task.name);
        qParams.put("v", "" + (task.endTime - task.startTime));
        HttpURLConnection connection = (HttpURLConnection) getURL("timing", qParams).openConnection();
        connection.setRequestProperty("User-Agent", getUAString(InAppStoryManager.getInstance().getContext()));
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod("POST");
        int statusCode = connection.getResponseCode();
        Log.d("InAppStory_Network", connection.getURL().toString() + " \nStatus Code: " + statusCode);
        connection.disconnect();
        return statusCode;

    }
}
