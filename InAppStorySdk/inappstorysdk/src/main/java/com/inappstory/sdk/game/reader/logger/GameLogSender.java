package com.inappstory.sdk.game.reader.logger;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameLogSender implements IGameLogSender {

    private boolean inProcess = false;

    private final IGameLogSaver saver;

    public GameLogSender(
            @NonNull InAppStoryService service,
            @NonNull IGameLogSaver saver
    ) {
        this.service = service;
        this.saver = saver;
        submitRunnable();
    }

    private final InAppStoryService service;

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

    private final ScheduledThreadPoolExecutor statisticScheduledThread =
            new ScheduledThreadPoolExecutor(1);

    private void sendLogs() {
        if (service != InAppStoryService.getInstance()) {
            statisticScheduledThread.shutdownNow();
            return;
        }
        synchronized (this) {
            if (inProcess) return;
            inProcess = true;
        }
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                NetworkClient networkClient = InAppStoryManager.getNetworkClient();
                if (networkClient == null) {
                    synchronized (GameLogSender.this) {
                        inProcess = false;
                    }
                    return;
                }
                sendLogs(getLogs(), service, networkClient);
            }

            @Override
            public void error() throws Exception {
                synchronized (GameLogSender.this) {
                    inProcess = false;
                }
            }
        });
    }

    private void saveLogs(List<GameLog> logs) {
        saver.saveLogs(logs);
    }

    private List<GameLog> getLogs() {
        return saver.getLogs();
    }


    private void sendLogs(
            final List<GameLog> logs,
            final InAppStoryService service,
            final NetworkClient networkClient
    ) {
        if (logs.isEmpty()) {
            synchronized (GameLogSender.this) {
                inProcess = false;
            }
            return;
        }
        GameLog log = logs.get(0);
        final List<GameLog> nextLogs = new ArrayList<>();
        if (logs.size() > 1) {
            nextLogs.addAll(logs.subList(1, logs.size()));
        }
        if (service == null) return;
        networkClient.enqueue(
                networkClient.getApi().sendGameLogMessage(
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
                        sendLogs(nextLogs, service, networkClient);
                    }

                    @Override
                    public void onError(int code, String message) {
                        saveLogs(logs);
                        synchronized (GameLogSender.this) {
                            inProcess = false;
                        }
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                }
        );
    }
}
