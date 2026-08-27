package com.inappstory.sdk.game.reader.logger;

import android.content.pm.PackageInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.utils.WebViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractGameLogger {

    private String gameInstanceId;

    protected final String gameError = "gameError";
    protected final String consoleError = "consoleError";
    protected final String consoleWarn = "consoleWarn";
    protected final String consoleInfo = "consoleInfo";
    protected final String sdkError = "sdkError";
    protected final String sdkWarn = "sdkWarn";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
                core.gameLogs().logSaver().saveLog(log);
            }
        });
    }

    List<String> multipleLogs = new ArrayList<>();
    int lastType = -1;

    public final void sendDebugLog(int type, final String message) {
        if (type == 3 && multipleLogs.size() < 20) {
            multipleLogs.add(message);
        } else {
            final String msg;
            if (type == 3) {
                msg = TextUtils.join(" *** ", multipleLogs);
                multipleLogs.clear();
            } else {
                msg = message;
            }
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    core.gameLogs().logSaver().saveLog(createBaseLog().type("debug").message(msg));
                }
            });
        }
    }


    public final void stopQueue() {
        core.gameLogs().logSender().stop();
    }

    public final void startQueue(boolean gameLaunched) {
        core.gameLogs().logSender().start(gameLaunched);
    }

    protected final GameLog createBaseLog() {
        PackageInfo pi = WebViewUtils.getCheckableWebViewPI(core);
        if (pi == null) pi = WebViewUtils.getUncheckableWebViewPI(core);
        int ver = 0;
        String packageName = "Unknown";
        if (pi != null) {
            packageName = pi.packageName;
            ver = WebViewUtils.getWebViewVersion(pi);
        }
        return new GameLog(
                gameInstanceId,
                ((IASDataSettingsHolder) core.settingsAPI()).sessionIdOrEmpty(),
                packageName,
                ver,
                System.currentTimeMillis() / 1000,
                launchTryNumber,
                gameLoaded
        );
    }
}
