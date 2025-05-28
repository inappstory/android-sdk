package com.inappstory.sdk.game.reader.logger;

import com.inappstory.sdk.core.IASCore;

public class GameLoggerLvl1 extends GameLoggerLvl0 {

    public GameLoggerLvl1(IASCore core, String gameInstanceId) {
        super(core, gameInstanceId);
    }

    @Override
    public void sendConsoleError(String message) {
        GameLog gameLog = createBaseLog();
        if (gameLog == null) return;
        sendLog(gameLog.message(message).type(consoleError));
    }

    @Override
    public void sendGameError(String message) {
        GameLog gameLog = createBaseLog();
        if (gameLog == null) return;
        sendLog(gameLog.message(message).type(gameError));
    }

    @Override
    public void sendSdkError(String message, String stacktrace) {
        GameLog gameLog = createBaseLog();
        if (gameLog == null) return;
        sendLog(gameLog.message(message).stacktrace(stacktrace).type(sdkError));

    }
}
