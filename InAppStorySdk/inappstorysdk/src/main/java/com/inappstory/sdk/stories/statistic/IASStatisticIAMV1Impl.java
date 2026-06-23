package com.inappstory.sdk.stories.statistic;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatisticIAMV1;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.SlideLayerKey;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IASStatisticIAMV1Impl implements IASStatisticIAMV1 {
    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);

    private final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    private final ArrayList<IAMStatisticV1Task> tasks = new ArrayList<>();


    public IASStatisticIAMV1Impl(IASCore core) {
        this.core = core;
        loopedExecutor.task(queueTasksRunnable);
    }

    private final IASCore core;

    @Override
    public boolean disabled() {
        return disabled;
    }

    @Override
    public boolean softDisabled() {
        return disabled || softDisabled;
    }

    private final Object statisticTasksLock = new Object();

    private final Runnable queueTasksRunnable = new Runnable() {
        @Override
        public void run() {
            if (tasks.size() == 0) {
                loopedExecutor.freeExecutor();
                return;
            }
            IAMStatisticV1Task task;
            synchronized (statisticTasksLock) {
                task = tasks.get(0);
                tasks.remove(0);
            }
            if (task != null) {
                sendTask(task);
            }
        }
    };


    private void addTask(IAMStatisticV1Task task) {
        synchronized (statisticTasksLock) {
            tasks.add(task);
        }
    }

    public void cleanTasks() {
        synchronized (statisticTasksLock) {
            tasks.clear();
        }
    }

    private void sendTask(final IAMStatisticV1Task task) {
        try {
            final Callable<Boolean> _ff = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Response response = core.network().execute(
                            core.network().getApi().sendInAppMessageStat(
                                    Integer.toString(task.iamId),
                                    task.event,
                                    task.eventId,
                                    task.iterationId,
                                    task.slideIndex,
                                    task.slideTotal,
                                    task.durationMs,
                                    task.value,
                                    task.widgetId,
                                    task.widgetLabel,
                                    task.widgetValue,
                                    task.widgetAnswer,
                                    task.widgetAnswerLabel,
                                    task.widgetAnswerScore,
                                    task.slideAnalytics
                            )
                    );
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loopedExecutor.freeExecutor();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            loopedExecutor.freeExecutor();
        }
    }

    private boolean disabled;
    private boolean softDisabled;

    @Override
    public void sendWidgetEvent(
            String widgetName,
            String widgetData,
            int iamId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String iterationId
    ) {
        if (disabled) return;
        String eventId = UUID.randomUUID().toString();
        IAMStatisticV1Task task = JsonParser.fromJson(widgetData, IAMStatisticV1Task.class);
        task.iamId = iamId;
        task.eventId = eventId;
        task.iterationId = iterationId;
        task.durationMs = duration;
        task.slideIndex = slideIndex;
        task.slideTotal = slidesTotal;
        task.event = widgetName;
        addTask(task);
    }

    private final Object scrollEventsLock = new Object();


    @Override
    public void setScrollEvents(Map<SlideLayerKey, Float> scrollEvents) {
        scrollValues.putAll(scrollEvents);
    }

    @Override
    public Map<SlideLayerKey, Float> getScrollEvents() {
        return new HashMap<>(scrollValues);
    }

    @Override
    public void addScrollEvent(SlideLayerKey key, float value) {
        synchronized (scrollEventsLock) {
            Float cacheValue = scrollValues.get(key);
            if (cacheValue == null) cacheValue = value;
            else cacheValue = Math.max(cacheValue, value);
            scrollValues.put(key, cacheValue);
        }
    }

    @Override
    public void sendSlideScrollEvents(String iterationId) {
        Map<SlideLayerKey, Float> localScrollValues;
        synchronized (scrollEventsLock) {
            localScrollValues = new HashMap<>(scrollValues);
            scrollValues.clear();
        }
        for (SlideLayerKey key : localScrollValues.keySet()) {
            Float value = localScrollValues.get(key);
            if (value == null) value = 0f;
            sendScrollEvent(
                    key.cardId,
                    key.slideIndex,
                    key.layerIndex,
                    value,
                    iterationId
            );
        }
    }

    private final Map<SlideLayerKey, Float> scrollValues = new HashMap<>();

    private void sendScrollEvent(
            int iamId,
            int slideIndex,
            int layerIndex,
            float value,
            String iterationId
    ) {
        if (disabled) return;
        String eventId = UUID.randomUUID().toString();
        IAMStatisticV1Task task = new IAMStatisticV1Task();
        task.iamId = iamId;
        task.eventId = eventId;
        task.iterationId = iterationId;
        task.slideIndex = slideIndex;
        task.layerIndex = layerIndex;
        task.value = Float.toString(value);
        task.event = SCROLL_EVENT_NAME;
        addTask(task);
    }

    @Override
    public void sendOpenEvent(
            int iamId,
            int slideIndex,
            int slidesTotal,
            String iterationId,
            boolean useIterationId
    ) {
        if (disabled) return;
        String eventId = useIterationId ? iterationId : UUID.randomUUID().toString();
        IAMStatisticV1Task task = new IAMStatisticV1Task();
        task.iamId = iamId;
        task.eventId = eventId;
        task.iterationId = iterationId;
        task.slideIndex = slideIndex;
        task.slideTotal = slidesTotal;
        task.event = OPEN_EVENT_NAME;
        addTask(task);
    }

    private final String OPEN_EVENT_NAME = "open";
    private final String CLOSE_EVENT_NAME = "close";
    private final String SCROLL_EVENT_NAME = "w-scrollview-impression";

    @Override
    public void sendCloseEvent(
            int iamId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String slideAnalytics,
            String iterationId
    ) {
        if (disabled) return;
        String eventId = UUID.randomUUID().toString();
        IAMStatisticV1Task task = new IAMStatisticV1Task();
        task.iamId = iamId;
        task.eventId = eventId;
        task.iterationId = iterationId;
        task.slideIndex = slideIndex;
        task.durationMs = duration;
        task.slideTotal = slidesTotal;
        task.slideAnalytics = slideAnalytics;
        task.event = CLOSE_EVENT_NAME;
        addTask(task);
    }

    @Override
    public void disabled(boolean softDisabled, boolean disabled) {
        this.softDisabled = softDisabled;
        this.disabled = disabled;
    }
}
