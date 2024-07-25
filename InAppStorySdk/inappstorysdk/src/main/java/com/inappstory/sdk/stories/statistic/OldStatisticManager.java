package com.inappstory.sdk.stories.statistic;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.utils.ISessionHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OldStatisticManager {

    public static void useInstance(GetOldStatisticManagerCallback callback) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        OldStatisticManager manager = service.getSession().currentStatisticManager();
        if (manager != null) callback.get(manager);
    }

    public static void useInstance(String sessionId, GetOldStatisticManagerCallback callback) {
        if (sessionId == null) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        OldStatisticManager manager = service.getSession().getStatisticManager(sessionId);
        if (manager != null) callback.get(manager);
    }

    public void refreshCallbacks() {
        statisticScheduledThread.shutdownNow();
        statisticScheduledThread = new ScheduledThreadPoolExecutor(1);
        submitRunnable();
    }

    public void refreshTimer() {
        synchronized (eventLock) {
            if (currentEvent != null) {
                currentEvent.timer = System.currentTimeMillis();
            }
        }
    }

    private final Object openProcessLock = new Object();
    private final Object previewLock = new Object();

    public OldStatisticManager() {
    }

    public void putStatistic(List<Object> e) {
        synchronized (openProcessLock) {
            if (e != null) {
                boolean alreadyAdded = false;
                for (List<Object> statisticObject : statistic) {
                    if (statisticObject.size() > 1 && e.size() > 1
                            && statisticObject.get(1).equals(e.get(1))) {
                        alreadyAdded = true;
                    }
                }
                if (!alreadyAdded)
                    statistic.add(e);
            }
        }
    }

    public final List<List<Object>> statistic = new ArrayList<>();


    public Runnable statisticUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            sendStatistic();

        }
    };

    private void submitRunnable() {
        long statisticUpdateInterval = 15000;
        statisticScheduledThread.scheduleAtFixedRate(
                statisticUpdateRunnable,
                statisticUpdateInterval,
                statisticUpdateInterval,
                TimeUnit.MILLISECONDS
        );
    }

    private ScheduledThreadPoolExecutor statisticScheduledThread =
            new ScheduledThreadPoolExecutor(1);


    public void clear() {
        statistic.clear();
    }

    public ArrayList<Integer> newStatisticPreviews(ArrayList<Integer> vals) {

        ArrayList<Integer> sendObject = new ArrayList<>();
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return sendObject;
        synchronized (previewLock) {
            for (Integer val : vals) {
                if (!service.getSession().hasViewedId(val)) {
                    sendObject.add(val);
                }
            }
        }
        return sendObject;
    }

    public void previewStatisticEvent(ArrayList<Integer> vals) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        ISessionHolder sessionHolder = service.getSession();
        boolean firstSend = !sessionHolder.hasViewedIds();
        ArrayList<Object> sendObject = new ArrayList<Object>();
        sendObject.add(5);
        sendObject.add(eventCount);
        synchronized (previewLock) {
            for (Integer val : vals) {
                if (!sessionHolder.hasViewedId(val)) {
                    sendObject.add(val);
                    sessionHolder.addViewedId(val);
                }
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
        NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            return true;
        }
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || !service.isConnected()) return true;
        String sessionId = service.getSession().getSessionId();
        if (sessionId.isEmpty())
            return false;
        synchronized (openProcessLock) {
            if (statistic.isEmpty()) {
                return true;
            }
        }
        if (service.statV1Disallowed()) {
            statistic.clear();
            return true;
        }
        try {
            synchronized (openProcessLock) {
                final List<List<Object>> notSendingStatistic = new ArrayList<>(statistic);
                StatisticSendObject statisticSendObject = new StatisticSendObject(
                        sessionId,
                        new ArrayList<>(statistic)
                );
                statistic.clear();

                final String updateUUID = ProfilingManager.getInstance().addTask(
                        "api_session_update"
                );
                networkClient.enqueue(
                        networkClient.getApi().sessionUpdate(statisticSendObject),
                        new NetworkCallback<SessionResponse>() {
                            @Override
                            public void onSuccess(SessionResponse response) {
                                ProfilingManager.getInstance().setReady(updateUUID);
                            }

                            @Override
                            public void errorDefault(String message) {
                                ProfilingManager.getInstance().setReady(updateUUID);
                                for (List<Object> statisticObject : notSendingStatistic) {
                                    putStatistic(statisticObject);
                                }
                            }

                            @Override
                            public Type getType() {
                                return SessionResponse.class;
                            }
                        });

            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
        return true;
    }


    public static class StatisticEvent {
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

    public void increaseEventCount() {
        eventCount++;
    }

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

        ArrayList<Object> statObject = new ArrayList<>();
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

    public void addLinkOpenStatistic(int storyId, int slideIndex) {
        synchronized (eventLock) {
            if (currentEvent != null)
                if (currentEvent.storyId == storyId && currentEvent.index == slideIndex)
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

    public void addGameClickStatistic(int id) {
        closeStatisticEvent();
        addStatisticEvent(1, id, 0);
        closeStatisticEvent(0, false);
    }
}
