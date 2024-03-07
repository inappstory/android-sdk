package com.inappstory.sdk.game.reader.logger;

public class GameLoggerLvl2 extends GameLoggerLvl1 {
    public GameLoggerLvl2(String gameInstanceId) {
        super(gameInstanceId);
    }

    @Override
    public void sendConsoleWarn(String message) {
        GameLog gameLog = createBaseLog();
        if (gameLog == null) return;
        sendLog(gameLog.message(message).type(consoleWarn));
    }

    @Override
    public void sendSdkWarn(String message) {
        GameLog gameLog = createBaseLog();
        if (gameLog == null) return;
        sendLog(gameLog.message(message).type(sdkWarn));

    }
}
