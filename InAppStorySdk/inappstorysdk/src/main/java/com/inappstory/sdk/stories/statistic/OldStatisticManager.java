package com.inappstory.sdk.stories.statistic;

import android.os.Handler;
import android.util.Log;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.StatisticResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OldStatisticManager {
    private static OldStatisticManager INSTANCE;

    public static OldStatisticManager getInstance() {
        if (INSTANCE == null) {
            synchronized (OldStatisticManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new OldStatisticManager();
            }
        }
        return INSTANCE;
    }

    public void refreshCallbacks() {
        if (handler == null) handler = new Handler();
        try {
            handler.removeCallbacks(OldStatisticManager.getInstance().statisticUpdateThread);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        } finally {
            handler.postDelayed(OldStatisticManager.getInstance().statisticUpdateThread,
                    statisticUpdateInterval);
        }
    }

    public void refreshTimer() {
        synchronized (eventLock) {
            if (currentEvent != null) {
                currentEvent.timer = System.currentTimeMillis();
            }
        }
    }

    public static Object openProcessLock = new Object();
    public static ArrayList<OpenSessionCallback> callbacks = new ArrayList<>();

    public OldStatisticManager() {
    }

    private static final long statisticUpdateInterval = 15000;

    private Handler handler = new Handler();

    public void putStatistic(List<Object> e) {
        synchronized (openProcessLock) {
            if (statistic != null) {
                statistic.add(e);
            }
        }
    }

    public List<List<Object>> statistic = new ArrayList<>();

    public Runnable statisticUpdateThread = new Runnable() {
        @Override
        public void run() {
            if (InAppStoryService.isNull()
                    || InAppStoryService.getInstance().getContext() == null) {
                handler.removeCallbacks(statisticUpdateThread);
                return;
            }
            if (sendStatistic()) {
                handler.postDelayed(statisticUpdateThread, statisticUpdateInterval);
            }
        }
    };

    public void previewStatisticEvent(ArrayList<Integer> vals) {
        boolean firstSend = (StatisticSession.getInstance().viewed.size() == 0);
        ArrayList<Object> sendObject = new ArrayList<Object>();
        sendObject.add(5);
        sendObject.add(eventCount);

        ArrayList<Integer> addedVals = new ArrayList<>();
        int count = 0;
        for (Integer val : vals) {
            if (!StatisticSession.getInstance().viewed.contains(val)) {
                sendObject.add(val);
                count++;
                StatisticSession.getInstance().viewed.add(val);
            }
        }
        if (sendObject.size() > 2) {
            putStatistic(sendObject);
            eventCount++;
        }

        if (firstSend) {
            sendStatistic();
        }
    }

    public boolean sendStatistic() {
        if (!InAppStoryService.isConnected()) return true;
        if (StatisticSession.getInstance().id == null || StatisticSession.needToUpdate())
            return false;
        synchronized (openProcessLock) {
            if (statistic == null || (statistic.isEmpty() && !StatisticSession.needToUpdate())) {
                return true;
            }
        }
        if (!InAppStoryService.getInstance().getSendStatistic()) {
            StatisticSession.getInstance();
            StatisticSession.updateStatistic();
            if (statistic != null)
                statistic.clear();
            return true;
        }
        try {

            synchronized (openProcessLock) {

                //   CsEventBus.getDefault().post(new DebugEvent(statistic.toString()));
                final String updateUUID = ProfilingManager.getInstance().addTask("api_session_update");
                NetworkClient.getApi().statisticsUpdate(
                        new StatisticSendObject(StatisticSession.getInstance().id,
                                statistic)).enqueue(new NetworkCallback<StatisticResponse>() {
                    @Override
                    public void onSuccess(StatisticResponse response) {
                        ProfilingManager.getInstance().setReady(updateUUID);
                        cleanStatistic();
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        ProfilingManager.getInstance().setReady(updateUUID);
                        cleanStatistic();
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        ProfilingManager.getInstance().setReady(updateUUID);
                        cleanStatistic();
                    }

                    @Override
                    public Type getType() {
                        return StatisticResponse.class;
                    }
                });

            }
            synchronized (openProcessLock) {
                if (statistic != null)
                    statistic.clear();
            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
        return true;
    }


    public void cleanStatistic() {
        StatisticSession.getInstance();
        StatisticSession.updateStatistic();
        synchronized (openProcessLock) {
            if (statistic == null) return;
            statistic.clear();
        }
    }

    public static boolean openProcess = false;


    public class StatisticEvent {
        public int eventType;
        public int storyId;
        public int index;
        public long timer;

        public StatisticEvent() {
            this.timer = System.currentTimeMillis();
        }

        public StatisticEvent(int eventType, int storyId, int index) {
            this.eventType = eventType;
            this.storyId = storyId;
            this.index = index;
            this.timer = System.currentTimeMillis();
        }

        public StatisticEvent(int eventType, int storyId, int index, long timer) {
            this.eventType = eventType;
            this.storyId = storyId;
            this.index = index;
            this.timer = timer;
        }
    }

    public StatisticEvent currentEvent;

    public void addStatisticEvent(int eventType, int storyId, int index) {
        synchronized (eventLock) {
            currentEvent = new StatisticEvent(eventType, storyId, index);
        }
    }

    public void addArticleStatisticEvent(int eventType, int articleId) {
        synchronized (eventLock) {
            currentEvent = new StatisticEvent(eventType, articleEventCount, articleId, articleTimer);
        }
    }


    public int eventCount = 0;


    private final Object eventLock = new Object();

    public void closeStatisticEvent(final Integer time, boolean clear) {

        StatisticEvent event = new StatisticEvent();
        int count = 0;
        synchronized (eventLock) {
            if (currentEvent == null) return;
            event.eventType = currentEvent.eventType;
            event.storyId = currentEvent.storyId;
            event.index = currentEvent.index;
            event.timer = currentEvent.timer;
            count = eventCount;
        }

        ArrayList statObject = new ArrayList<Object>();
        statObject.add(event.eventType);
        statObject.add(count);
        statObject.add(event.storyId);
        statObject.add(event.index);
        statObject.add(Math.max(time != null ? time : System.currentTimeMillis() - event.timer, 0));
        putStatistic(statObject);
        if (!clear)
            synchronized (eventLock) {
                currentEvent = null;
            }
    }

    public void closeStatisticEvent() {
        closeStatisticEvent(null, false);
        eventCount++;
    }

    public void addStatisticBlock(int storyId, int index) {
        boolean closeStat = false;
        synchronized (eventLock) {
            if (currentEvent != null) closeStat = true;
        }
        if (closeStat)
            closeStatisticEvent();
        addStatisticEvent(1, storyId, index);
    }

    public int articleEventCount = 0;
    public long articleTimer = 0;

    public void addArticleOpenStatistic(int eventType, int articleId) {
        articleEventCount = eventCount;

        synchronized (eventLock) {
            if (currentEvent != null)
                currentEvent.eventType = 2;
        }
        closeStatisticEvent();
        articleTimer = System.currentTimeMillis();
        addArticleStatisticEvent(eventType, articleId);
    }

    public void addLinkOpenStatistic() {
        synchronized (eventLock) {
            if (currentEvent != null)
                currentEvent.eventType = 2;
        }
    }

    public void addDeeplinkClickStatistic(int id) {
        closeStatisticEvent();
        addStatisticEvent(1, id, 0);
        closeStatisticEvent(0, false);
        eventCount++;
        addStatisticEvent(2, id, 0);
        closeStatisticEvent(0, false);
    }

}
