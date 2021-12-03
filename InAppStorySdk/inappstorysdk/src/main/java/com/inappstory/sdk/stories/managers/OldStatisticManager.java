package com.inappstory.sdk.stories.managers;

import android.os.Handler;
import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.StatisticResponse;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;
import com.inappstory.sdk.stories.api.models.StatisticSession;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.utils.SessionManager;

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

    public static Object openProcessLock = new Object();
    public static ArrayList<OpenSessionCallback> callbacks = new ArrayList<>();

    public OldStatisticManager() {
        //CsEventBus.getDefault().register(this);
    }

    private static final long statisticUpdateInterval = 30000;

    private Handler handler = new Handler();

    public void putStatistic(List<Object> e) {
        synchronized (openProcessLock) {
            if (statistic != null) {
                for (List<Object> obj : statistic)
                    if (e.toString().equals(obj.toString())) return;
                statistic.add(e);
            }
        }
    }

    public List<List<Object>> statistic = new ArrayList<>();

    public Runnable statisticUpdateThread = new Runnable() {
        @Override
        public void run() {
            if (handler == null) handler = new Handler();
            if (InAppStoryService.isNull()
                    || InAppStoryService.getInstance().getContext() == null) {
                handler.removeCallbacks(statisticUpdateThread);
                return;
            }
            if (sendStatistic("statisticUpdateThread")) {
                handler.postDelayed(statisticUpdateThread, statisticUpdateInterval);
            }
        }
    };

    public void previewStatisticEvent(ArrayList<Integer> vals) {
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
        if (count > 2) {
            sendStatistic("previewStatisticEvent");
        }
    }

    public boolean sendStatistic(String place) {
        if (!InAppStoryService.isConnected()) return true;
        if (StatisticSession.getInstance().id == null || StatisticSession.needToUpdate())
            return false;
        if (!InAppStoryService.getInstance().getSendStatistic()) {
            cleanStatistic();
            return true;
        }
        synchronized (openProcessLock) {

            Log.e("sendStatPlace", place);
            if (statistic == null || (statistic.isEmpty() && !StatisticSession.needToUpdate())) {
                return true;
            }
            NetworkClient.getApi().statisticsUpdate(
                    new StatisticSendObject(StatisticSession.getInstance().id,
                            statistic)).enqueue(new NetworkCallback<StatisticResponse>() {
                @Override
                public void onSuccess(StatisticResponse response) {
                    //cleanStatistic();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                   // cleanStatistic();
                }

                @Override
                public void onTimeout() {
                    super.onTimeout();
                   // cleanStatistic();
                }

                @Override
                public Type getType() {
                    return StatisticResponse.class;
                }
            });
            if (statistic != null)
                statistic.clear();
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
        currentEvent = new StatisticEvent(eventType, storyId, index);
    }

    public void addArticleStatisticEvent(int eventType, int articleId) {
        currentEvent = new StatisticEvent(eventType, articleEventCount, articleId, articleTimer);
    }




    public int eventCount = 0;

    public void closeStatisticEvent(final Integer time, boolean clear) {
        if (currentEvent != null) {
            ArrayList statObject = new ArrayList<Object>();
            statObject.add(currentEvent.eventType);
            statObject.add(eventCount);
            statObject.add(currentEvent.storyId);
            statObject.add(currentEvent.index);
            statObject.add(Math.max(time != null ? time : System.currentTimeMillis() - currentEvent.timer, 0));
            putStatistic(statObject);
            if (!clear)
                currentEvent = null;
        }
    }

    public void closeStatisticEvent() {
        closeStatisticEvent(null, false);
    }

    public void addStatisticBlock(int storyId, int index) {
        //if (currentEvent != null)
        closeStatisticEvent();
        addStatisticEvent(1, storyId, index);
        eventCount++;
    }

    public int articleEventCount = 0;
    public long articleTimer = 0;

    public void addArticleOpenStatistic(int eventType, int articleId) {
        articleEventCount = eventCount;
        if (currentEvent != null)
            currentEvent.eventType = 2;
        closeStatisticEvent();
        eventCount++;
        articleTimer = System.currentTimeMillis();
        addArticleStatisticEvent(eventType, articleId);
    }

    public void addLinkOpenStatistic() {
        if (currentEvent != null)
            currentEvent.eventType = 2;
    }

    public void addDeeplinkClickStatistic(int id) {
        closeStatisticEvent();
        eventCount++;
        addStatisticEvent(1, id, 0);
        closeStatisticEvent(0, false);
        eventCount++;
        addStatisticEvent(2, id, 0);
        closeStatisticEvent(0, false);
    }

    public void addArticleCloseStatistic() {
        closeStatisticEvent();
        eventCount++;
        addStatisticEvent(1, InAppStoryService.getInstance().getCurrentId(),
                InAppStoryService.getInstance().getCurrentIndex());
    }

}
