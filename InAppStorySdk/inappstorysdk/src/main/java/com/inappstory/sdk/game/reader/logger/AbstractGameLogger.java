package com.inappstory.sdk.game.reader.logger;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGameLogger {

    private String gameInstanceId;

    protected final String gameError = "gameError";
    protected final String consoleError = "consoleError";
    protected final String consoleWarn = "consoleWarn";
    protected final String consoleInfo = "consoleInfo";
    protected final String sdkError = "sdkError";
    protected final String sdkWarn = "sdkWarn";

    private final IASCore core;

    protected String gameInstanceId() {
        return gameInstanceId;
    }

    public void gameInstanceId(String gameInstanceId) {
        this.gameInstanceId = gameInstanceId;
    }

    protected AbstractGameLogger(IASCore core, String gameInstanceId) {
        this.gameInstanceId = gameInstanceId;
        this.core = core;
    }


    protected AbstractGameLogger(IASCore core) {
        this.core = core;
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
                core.logs().logSaver().saveLog(log);
            }
        });
    }

    protected final GameLog createBaseLog() {
        return new GameLog(
                gameInstanceId,
                core.sessionManager().getSession().getSessionId(),
                System.currentTimeMillis() / 1000,
                launchTryNumber,
                gameLoaded
        );
    }
}
