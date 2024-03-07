package com.inappstory.sdk.game.reader.logger;

public final class GameLoggerLvl3 extends GameLoggerLvl2 {
    public GameLoggerLvl3(String gameInstanceId) {
        super(gameInstanceId);
    }

    @Override
    public void sendConsoleInfo(String message) {
        GameLog gameLog = createBaseLog();
        if (gameLog == null) return;
        sendLog(gameLog.message(message).type(consoleInfo));
    }
}
