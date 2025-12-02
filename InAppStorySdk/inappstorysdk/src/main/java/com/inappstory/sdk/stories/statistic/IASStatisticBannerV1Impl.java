package com.inappstory.sdk.stories.statistic;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatisticBannerV1;
import com.inappstory.sdk.core.api.IASStatisticIAMV1;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IASStatisticBannerV1Impl implements IASStatisticBannerV1 {
    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);

    private final ExecutorService netExecutor = Executors.newFixedThreadPool(1);
    private final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);

    private final ArrayList<BannerStatisticV1Task> tasks = new ArrayList<>();


    public IASStatisticBannerV1Impl(IASCore core) {
        this.core = core;
        loopedExecutor.init(queueTasksRunnable);
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
            BannerStatisticV1Task task;
            synchronized (statisticTasksLock) {
                task = tasks.get(0);
                tasks.remove(0);
            }
            if (task != null) {
                sendTask(task);
            }
        }
    };


    private void addTask(BannerStatisticV1Task task) {
        synchronized (statisticTasksLock) {
            tasks.add(task);
        }
    }

    public void cleanTasks() {
        synchronized (statisticTasksLock) {
            tasks.clear();
        }
    }

    private void sendTask(final BannerStatisticV1Task task) {
        try {
            final Callable<Boolean> _ff = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Response response = core.network().execute(
                            core.network().getApi().sendBannerStat(
                                    Integer.toString(task.bannerId),
                                    task.event,
                                    task.eventId,
                                    task.iterationId,
                                    task.slideIndex,
                                    task.slideTotal,
                                    task.durationMs,
                                    task.widgetId,
                                    task.widgetLabel,
                                    task.widgetValue,
                                    task.widgetAnswer,
                                    task.widgetAnswerLabel,
                                    task.widgetAnswerScore
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
            int bannerId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String iterationId
    ) {
        Log.e("BannerStatistic", "Widget event " + bannerId + " " + widgetName + " " + widgetData);
        if (disabled) return;
        String eventId = UUID.randomUUID().toString();
        BannerStatisticV1Task task = JsonParser.fromJson(
                widgetData,
                BannerStatisticV1Task.class
        );
        task.bannerId = bannerId;
        task.eventId = eventId;
        task.iterationId = iterationId;
        task.durationMs = duration;
        task.slideIndex = slideIndex;
        task.slideTotal = slidesTotal;
        task.event = widgetName;
        addTask(task);
    }

    @Override
    public void sendOpenEvent(
            int bannerId,
            int slideIndex,
            int slidesTotal,
            String iterationId
    ) {
        Log.e("BannerStatistic", "Open event " + bannerId);
        if (disabled) return;
        String eventId = UUID.randomUUID().toString();
        BannerStatisticV1Task task = new BannerStatisticV1Task();
        task.bannerId = bannerId;
        task.eventId = eventId;
        task.iterationId = iterationId;
        task.slideIndex = slideIndex;
        task.slideTotal = slidesTotal;
        task.event = OPEN_EVENT_NAME;
        addTask(task);
    }


    private final String OPEN_EVENT_NAME = "view";
    private final String CLOSE_EVENT_NAME = "close";

    @Override
    public void sendCloseEvent(
            int bannerId,
            int slideIndex,
            int slidesTotal,
            long duration,
            String iterationId
    ) {
        if (disabled) return;
        String eventId = UUID.randomUUID().toString();
        BannerStatisticV1Task task = new BannerStatisticV1Task();
        task.bannerId = bannerId;
        task.eventId = eventId;
        task.iterationId = iterationId;
        task.slideIndex = slideIndex;
        task.durationMs = duration;
        task.slideTotal = slidesTotal;
        task.event = CLOSE_EVENT_NAME;
        addTask(task);
    }

    @Override
    public void disabled(boolean softDisabled, boolean disabled) {
        this.softDisabled = softDisabled;
        this.disabled = disabled;
    }
}
