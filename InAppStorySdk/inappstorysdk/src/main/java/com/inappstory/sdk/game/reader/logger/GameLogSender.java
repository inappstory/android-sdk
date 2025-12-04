package com.inappstory.sdk.game.reader.logger;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameLogSender implements IGameLogSender {

    private boolean inProcess = false;

    private final IGameLogSaver saver;

    private final IASCore core;
    private boolean paused = true;
    private boolean gameLaunched = false;
    private final Object lock = new Object();

    public GameLogSender(
            @NonNull IASCore core,
            @NonNull IGameLogSaver saver
    ) {
        this.core = core;
        this.saver = saver;
        submitRunnable();
    }


    private final Runnable statisticUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            sendLogs();
        }
    };

    private void submitRunnable() {
        long statisticUpdateInterval = 1000;
        statisticScheduledThread.scheduleAtFixedRate(
                statisticUpdateRunnable,
                statisticUpdateInterval,
                statisticUpdateInterval,
                TimeUnit.MILLISECONDS
        );
    }

    private final ScheduledTPEManager statisticScheduledThread =
            new ScheduledTPEManager();

    private void sendLogs() {
        synchronized (lock) {
            if (paused) return;
            if (inProcess) return;
            inProcess = true;
        }
        sendLogs(getLogs());
    }

    private void saveLogs(List<GameLog> logs) {
        saver.saveLogs(logs);
    }

    private List<GameLog> getLogs() {
        List<GameLog> logs = saver.getLogs();
        return logs;
    }


    private void sendLogs(
            final List<GameLog> logs
    ) {
        boolean isGameLaunched = false;
        if (logs.isEmpty()) {
            synchronized (lock) {
                inProcess = false;
            }
            return;
        }
        synchronized (lock) {
            if (paused) return;
            isGameLaunched = gameLaunched;
        }
        GameLog log = logs.get(0);
        final List<GameLog> nextLogs = new ArrayList<>();
        if (logs.size() > 1) {
            nextLogs.addAll(logs.subList(1, logs.size()));
        }
        core.network().enqueue(
                core.network().getApi().sendGameLogMessage(
                        log.gameInstanceId(),
                        log.type(),
                        log.launchTryNumber(),
                        log.timestamp(),
                        log.message(),
                        log.stacktrace(),
                        log.logSession(),
                        log.gameLoaded()
                ),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        sendLogs(nextLogs);
                    }

                    @Override
                    public void onError(int code, String message) {
                        sendLogs(nextLogs);
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                }
        );
    }

    @Override
    public void stop() {
        synchronized (lock) {
            paused = true;
            gameLaunched = false;
        }
    }

    @Override
    public void start(boolean gameLaunched) {
        synchronized (lock) {
            this.gameLaunched = gameLaunched;
            paused = false;
        }
    }
}
