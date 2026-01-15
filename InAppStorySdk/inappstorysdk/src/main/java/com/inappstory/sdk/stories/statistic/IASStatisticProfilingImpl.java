package com.inappstory.sdk.stories.statistic;

import static android.content.Context.TELEPHONY_SERVICE;
import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStatisticProfiling;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.GetUrl;
import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IASStatisticProfilingImpl implements IASStatisticProfiling {
    private final List<ProfilingTask> tasks = new ArrayList<>();
    private final List<ProfilingTask> readyTasks = new ArrayList<>();
    private final IASCore core;

    public IASStatisticProfilingImpl(IASCore core) {
        this.core = core;
        loopedExecutor.init(queueTasksRunnable);
    }


    private final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    private final Object tasksLock = new Object();

    public String addTask(String name, String hash) {
        ProfilingTask task = new ProfilingTask();
        task.uniqueHash = hash;
        task.name = name;
        task.startTime = System.currentTimeMillis();
        task.isAllowToForceSend = !disabled;
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
        IASDataSettingsHolder settingsHolder =
                (IASDataSettingsHolder) core.settingsAPI();
        String hash = randomUUID().toString();
        ProfilingTask task = new ProfilingTask();
        CachedSessionData sessionData = settingsHolder.sessionData();
        task.sessionId = ((IASDataSettingsHolder) core.settingsAPI()).sessionIdOrEmpty();
        task.isAllowToForceSend = !(disabled || softDisabled);
        task.userId = sessionData != null ? sessionData.userId : "";
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

    private final Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            boolean readyIsEmpty = false;
            synchronized (tasksLock) {
                readyIsEmpty = readyTasks.size() == 0;
            }
            if (readyIsEmpty || disabled) {
                loopedExecutor.freeExecutor();
                return;
            }
            ProfilingTask task;
            synchronized (tasksLock) {
                task = readyTasks.get(0);
                readyTasks.remove(0);
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

    private String getCC() {
        Context context = core.appContext();
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


    private void sendTiming(ProfilingTask task) throws Exception {
        Map<String, String> qParams = new HashMap<>();
        qParams.put("s", (task.sessionId != null && !task.sessionId.isEmpty()) ? task.sessionId :
                ((IASDataSettingsHolder)core.settingsAPI()).sessionIdOrEmpty());
        qParams.put("u", task.userId != null ? task.userId : "");
        String cc = getCC();
        qParams.put("ts", "" + System.currentTimeMillis() / 1000);
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
        connection.setRequestProperty("User-Agent", new UserAgent().generate(core));
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod("POST");
        int statusCode = connection.getResponseCode();
        InAppStoryManager.showDLog(LoggerTags.IAS_NETWORK, connection.getURL().toString() + " \nStatus Code: " + statusCode);
        connection.disconnect();
        return;

    }

    private boolean disabled;
    private boolean softDisabled;

    @Override
    public void disabled(boolean softDisabled, boolean disabled) {
        this.softDisabled = softDisabled;
        this.disabled = disabled;
    }

    @Override
    public boolean disabled() {
        return disabled;
    }

    @Override
    public boolean softDisabled() {
        return softDisabled || disabled;
    }
}
