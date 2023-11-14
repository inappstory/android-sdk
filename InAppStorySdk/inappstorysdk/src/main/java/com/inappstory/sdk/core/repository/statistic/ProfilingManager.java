package com.inappstory.sdk.core.repository.statistic;

import static android.content.Context.TELEPHONY_SERVICE;
import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.utils.network.models.Request;
import com.inappstory.sdk.core.utils.network.utils.GetUrl;
import com.inappstory.sdk.core.utils.network.utils.UserAgent;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfilingManager {
    ArrayList<ProfilingTask> tasks = new ArrayList<>();
    ArrayList<ProfilingTask> readyTasks = new ArrayList<>();
    private static ProfilingManager INSTANCE;

    Context context;

    public static ProfilingManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfilingManager();
            INSTANCE.init();
        }
        return INSTANCE;
    }

    private final Object tasksLock = new Object();

    public String addTask(String name, String hash) {
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
        String hash = randomUUID().toString();
        SessionDTO sessionDTO = IASCore.getInstance().sessionRepository.getSessionData();
        ProfilingTask task = new ProfilingTask();
        if (sessionDTO != null) {
            task.sessionId = sessionDTO.getId();
            task.userId = sessionDTO.getUserId();
        }
        task.isAllowToForceSend = isAllowToSend();
        task.uniqueHash = hash;
        task.name = name;
        task.startTime = System.currentTimeMillis();
        synchronized (tasksLock) {
            tasks.add(task);
        }
        return hash;
    }

    public void setReady(String hash, boolean force) {
        if (handler == null) return;
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
                this.handler.post(new Runnable() {
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

    private Handler handler;
    private HandlerThread thread;

    public void cleanTasks() {
        synchronized (tasksLock) {
            tasks.clear();
        }
    }

    public void init() {
        thread = new HandlerThread("ProfilingThread" + System.currentTimeMillis());
        thread.start();
        context = InAppStoryManager.getInstance().getContext();
        handler = new Handler(thread.getLooper());
        handler.postDelayed(queueTasksRunnable, 100);
    }

    private Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            if (handler == null) return;
            boolean readyIsEmpty = false;
            synchronized (getInstance().tasksLock) {
                readyIsEmpty = getInstance().readyTasks == null || getInstance().readyTasks.size() == 0;
            }
            if (readyIsEmpty || IASCore.getInstance().notConnected() || !isAllowToSend()) {
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

    private boolean isAllowToSend() {
        return IASCore.getInstance().sessionRepository.isAllowProfiling();
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

        SessionDTO sessionDTO = IASCore.getInstance().sessionRepository.getSessionData();
        qParams.put("s",
                (task.sessionId != null && !task.sessionId.isEmpty()) ? task.sessionId :
                (sessionDTO != null ? sessionDTO.getId() : null)
        );
        qParams.put("u", task.userId != null ? task.userId :
                (sessionDTO != null ? sessionDTO.getId() : null));
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
