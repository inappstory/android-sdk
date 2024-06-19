package com.inappstory.sdk.game.reader.logger;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;

public abstract class AbstractGameLogger {

    private String gameInstanceId;

    protected final String gameError = "gameError";
    protected final String consoleError = "consoleError";
    protected final String consoleWarn = "consoleWarn";
    protected final String consoleInfo = "consoleInfo";
    protected final String sdkError = "sdkError";
    protected final String sdkWarn = "sdkWarn";

    protected String gameInstanceId() {
        return gameInstanceId;
    }

    public void gameInstanceId(String gameInstanceId) {
        this.gameInstanceId = gameInstanceId;
    }

    protected AbstractGameLogger(String gameInstanceId) {
        this.gameInstanceId = gameInstanceId;
    }


    protected AbstractGameLogger() {
    }

    public abstract void sendGameError(String message);

    public abstract void sendConsoleError(String message);

    public abstract void sendSdkError(
            String message,
            String stacktrace
    );

    public abstract void sendConsoleWarn(String message);

    public abstract void sendSdkWarn(String message);

    public abstract void sendConsoleInfo(String message);

    private boolean gameLoaded = false;

    public final void launchTryNumber(int launchTryNumber) {
        this.launchTryNumber = launchTryNumber;
    }

    private int launchTryNumber = 1;

    public final void gameLoaded(boolean gameLoaded) {
        this.gameLoaded = gameLoaded;
    }

    public final void sendLog(final GameLog log) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                    @Override
                    public void use(@NonNull InAppStoryService service) throws Exception {
                        service.getLogSaver().saveLog(log);
                    }
                });
            }
        });
    }

    protected final GameLog createBaseLog() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        return new GameLog(
                gameInstanceId,
                service.getSession().getSessionId(),
                System.currentTimeMillis() / 1000,
                launchTryNumber,
                gameLoaded
        );
    }
}
