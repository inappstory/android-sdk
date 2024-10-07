package com.inappstory.sdk.game.reader.logger;

import com.inappstory.sdk.core.IASCore;

public final class GameLoggerLvl3 extends GameLoggerLvl2 {
    public GameLoggerLvl3(IASCore core, String gameInstanceId) {
        super(core, gameInstanceId);
    }

    @Override
    public void sendConsoleInfo(String message) {
        GameLog gameLog = createBaseLog();
        if (gameLog == null) return;
        sendLog(gameLog.message(message).type(consoleInfo));
    }
}
