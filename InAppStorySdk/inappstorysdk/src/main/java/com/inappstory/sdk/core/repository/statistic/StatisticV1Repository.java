package com.inappstory.sdk.core.repository.statistic;

import android.os.Handler;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.models.js.StoryIdSlideIndex;
import com.inappstory.sdk.core.repository.session.interfaces.IUpdateSessionCallback;
import com.inappstory.sdk.core.utils.network.NetworkClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatisticV1Repository implements IStatisticV1Repository {
    private final Set<Integer> viewedStoryIds = new HashSet<>();
    private final List<List<Object>> statistic = new ArrayList<>();

    @Override
    public void clear() {
        synchronized (statisticLock) {
            viewedStoryIds.clear();
            statistic.clear();
            eventCount = 0;
            currentEvent = null;
        }
    }

    private final Handler handler = new Handler();


    private final Runnable statisticUpdateThread = new Runnable() {
        @Override
        public void run() {
            sendStatistic();
            restartTimer();
        }
    };

    private void sendStatistic() {
        final NetworkClient networkClient = IASCore.getInstance().getNetworkClient();
        synchronized (statisticLock) {
            if (networkClient == null || IASCore.getInstance().notConnected() || statistic.isEmpty()) {
                return;
            }
        }
        if (!IASCore.getInstance().getSendStatistic()) {
            synchronized (statisticLock) {
                statistic.clear();
            }
            return;
        }
        try {
            final List<List<Object>> sendingStatistic;
            synchronized (statisticLock) {
                sendingStatistic = new ArrayList<>(statistic);
                statistic.clear();
            }
            IASCore.getInstance().sessionRepository.updateSession(
                    sendingStatistic,
                    new IUpdateSessionCallback() {
                        @Override
                        public void onSuccess(Void response) {
                        }

                        @Override
                        public void onError() {
                            synchronized (statisticLock) {
                                statistic.addAll(sendingStatistic);
                            }
                        }
                    });
        } catch (Exception ignored) {
        }
    }

    private void restartTimer() {
        long statisticUpdateInterval = 15000;
        handler.postDelayed(statisticUpdateThread, statisticUpdateInterval);
    }

    @Override
    public List<List<Object>> getCurrentStatistic() {
        return statistic;
    }

    @Override
    public void completeCurrentStatisticRecord() {
        completeCurrentRecord(null);
    }

    @Override
    public void onDeeplinkClick(int id) {
        addStatisticEvent(1, new StoryIdSlideIndex(id), 0);
        addStatisticEvent(2, new StoryIdSlideIndex(id), 0);
        completeCurrentRecord(0);
    }

    @Override
    public void setTypeToTransition(StoryIdSlideIndex storyIdSlideIndex) {
        synchronized (statisticLock) {
            if (currentEvent == null) {
                currentEvent = new StatisticEvent(
                        2,
                        storyIdSlideIndex.id,
                        storyIdSlideIndex.index
                ); //TODO - maximum check, if it break something
            } else {
                currentEvent.eventType = 2;
            }
        }
    }

    @Override
    public void refreshTimer() {
        synchronized (statisticLock) {
            if (currentEvent != null) {
                currentEvent.timer = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void forceSend() {
        completeCurrentStatisticRecord();
        sendStatistic();
    }

    private StatisticEvent currentEvent;

    void completeCurrentRecord(final Integer time) {
        StatisticEvent event = new StatisticEvent();
        List<Object> statObject = new ArrayList<>();
        synchronized (statisticLock) {
            if (currentEvent == null) return;
            statObject.add(event.eventType);
            statObject.add(event.storyId);
            statObject.add(event.index);
            statObject.add(
                    Math.max(
                            time != null ? time : System.currentTimeMillis() - event.timer,
                            0
                    )
            );
            setEventCountAndAddStatObject(statObject);
            currentEvent = null;
        }
    }

    private final Object statisticLock = new Object();

    @Override
    public List<Integer> getNonViewedStoryIds(List<Integer> ids) {
        List<Integer> sendObject = new ArrayList<>();
        synchronized (statisticLock) {
            for (Integer val : ids) {
                if (!viewedStoryIds.contains(val)) {
                    sendObject.add(val);
                }
            }
        }
        return sendObject;
    }

    private int eventCount = 0;

    @Override
    public void setViewedStoryIds(List<Integer> ids) {
        boolean firstSend;
        synchronized (statisticLock) {
            firstSend = (viewedStoryIds.size() == 0);
            List<Object> sendObject = new ArrayList<Object>();
            sendObject.add(5);
            for (Integer id : ids) {
                if (!viewedStoryIds.contains(id)) {
                    sendObject.add(id);
                    viewedStoryIds.add(id);
                }
            }
            if (sendObject.size() > 1) {
                setEventCountAndAddStatObject(sendObject);
            }
        }
        if (firstSend) {
            sendStatistic();
        }
    }

    private void setEventCountAndAddStatObject(List<Object> statObject) {
        statObject.add(1, eventCount);
        statistic.add(statObject);
        eventCount++;
    }

    @Override
    public void refreshStatisticProcess() {
        try {
            handler.removeCallbacks(statisticUpdateThread);
            clear();
        } catch (Exception ignored) {
        } finally {
            restartTimer();
        }
    }

    @Override
    public void addStatisticEvent(StoryIdSlideIndex storyIdSlideIndex) {
       addStatisticEvent(
               1,
               storyIdSlideIndex,
               System.currentTimeMillis()
       );
    }

    private void addStatisticEvent(int type, StoryIdSlideIndex storyIdSlideIndex, long timer) {
        boolean complete;
        synchronized (statisticLock) {
            complete = currentEvent != null;
        }
        if (complete) {
            completeCurrentRecord(null);
        }
        synchronized (statisticLock) {
            currentEvent = new StatisticEvent(
                    type,
                    storyIdSlideIndex.id,
                    storyIdSlideIndex.index,
                    timer
            );
        }
    }
}
