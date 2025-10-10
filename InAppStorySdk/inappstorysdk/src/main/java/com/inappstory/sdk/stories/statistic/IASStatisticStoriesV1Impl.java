package com.inappstory.sdk.stories.statistic;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatistic;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.network.content.models.SessionResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IASStatisticStoriesV1Impl implements IASStatisticStoriesV1 {

    private String userId;
    private String locale;

    public IASStatisticStoriesV1Impl(IASCore core, String userId, String locale, boolean disabled) {
        this.core = core;
        this.disabled = disabled;
        this.userId = userId;
        this.locale = locale;
    }

    private final IASCore core;

    @Override
    public void refreshCurrentState() {
        synchronized (eventLock) {
            if (currentEvent == null) return;
            currentEvent.eventType = 1;
            currentEvent.timer = System.currentTimeMillis();
        }
    }

    @Override
    public void clearCurrentState() {
        synchronized (eventLock) {
            currentEvent = null;
        }
    }

    @Override
    public void restartSchedule() {
        statisticScheduledThread.shutdownNow();
        submitRunnable();
    }

    @Override
    public void refreshTimer() {
        synchronized (eventLock) {
            if (currentEvent != null) {
                currentEvent.timer = System.currentTimeMillis();
            }
        }
    }

    private final Object openProcessLock = new Object();
    private final Object previewLock = new Object();

    private void putStatistic(List<Object> e) {
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

    @Override
    public List<List<Object>> extractCurrentStatistic() {
        List<List<Object>> extractedStatistic = new ArrayList<>(statistic);
        statistic.clear();
        return extractedStatistic;
    }

    private final List<List<Object>> statistic = new ArrayList<>();


    private final Runnable statisticUpdateRunnable = new Runnable() {
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

    private ScheduledTPEManager statisticScheduledThread =
            new ScheduledTPEManager();


    @Override
    public void previewStatisticEvent(List<Integer> vals) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        IASStatistic iasStatistic = core.statistic();
        boolean firstSend = !iasStatistic.hasViewedIds();
        List<Object> sendObject = new ArrayList<Object>();
        sendObject.add(5);
        sendObject.add(eventCount);
        synchronized (previewLock) {
            for (Integer val : vals) {
                if (!iasStatistic.hasViewedId(val)) {
                    sendObject.add(val);
                    iasStatistic.addViewedId(val);
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

    @Override
    public void sendStatistic() {

        final String sessionId = core.sessionManager().getSession().getSessionId();
        if (sessionId.isEmpty()) return;
        synchronized (openProcessLock) {
            if (statistic.isEmpty()) {
                return;
            }
        }
        if (disabled) {
            statistic.clear();
            return;
        }
        new ConnectionCheck().check(core.appContext(), new ConnectionCheckCallback(core) {
            @Override
            public void success() {
                try {
                    synchronized (openProcessLock) {
                        final List<List<Object>> notSendingStatistic = new ArrayList<>(statistic);
                        StatisticSendObject statisticSendObject = new StatisticSendObject(
                                sessionId,
                                new ArrayList<>(statistic)
                        );
                        statistic.clear();

                        final String updateUUID = core.statistic().profiling().addTask(
                                "api_session_update"
                        );
                        core.network().enqueue(
                                core.network().getApi().sessionUpdate(
                                        statisticSendObject,
                                        userId,
                                        locale
                                ),
                                new NetworkCallback<SessionResponse>() {
                                    @Override
                                    public void onSuccess(SessionResponse response) {
                                        core.statistic().profiling().setReady(updateUUID);
                                    }

                                    @Override
                                    public void errorDefault(String message) {
                                        core.statistic().profiling().setReady(updateUUID);
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
                    core.exceptionManager().createExceptionLog(e);
                }
            }
        });

    }

    @Override
    public boolean disabled() {
        return disabled;
    }

    private boolean disabled;

    @Override
    public void disabled(boolean disabled) {
        this.disabled = disabled;
    }


    private StatisticEvent currentEvent;

    private void addStatisticEvent(int eventType, int storyId, int index) {
        synchronized (eventLock) {
            currentEvent = new StatisticEvent(eventType, storyId, index);
        }
    }


    private int eventCount = 0;

    @Override
    public void increaseEventCount() {
        eventCount++;
    }

    private final Object eventLock = new Object();

    @Override
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

    @Override
    public void closeStatisticEvent() {
        closeStatisticEvent(null, false);
        eventCount++;
    }

    @Override
    public void addStatisticBlock(int storyId, int index) {
        boolean closeStat = false;
        synchronized (eventLock) {
            if (currentEvent != null) closeStat = true;
        }
        if (closeStat)
            closeStatisticEvent();
        addStatisticEvent(1, storyId, index);
    }


    @Override
    public void addLinkOpenStatistic(int storyId, int slideIndex) {
        synchronized (eventLock) {
            if (currentEvent != null) {
                if (currentEvent.index == slideIndex && currentEvent.storyId == storyId) {
                    currentEvent.eventType = 2;
                }
            }
        }
    }

    @Override
    public void addDeeplinkClickStatistic(int id) {
        closeStatisticEvent();
        addStatisticEvent(1, id, 0);
        closeStatisticEvent(0, false);
        eventCount++;
        addStatisticEvent(2, id, 0);
        closeStatisticEvent(0, false);
    }

    @Override
    public void addGameClickStatistic(int id) {
        closeStatisticEvent();
        addStatisticEvent(1, id, 0);
        closeStatisticEvent(0, false);
    }
}
