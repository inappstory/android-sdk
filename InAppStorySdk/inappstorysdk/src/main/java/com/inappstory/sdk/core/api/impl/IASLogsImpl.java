package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASLogs;
import com.inappstory.sdk.game.reader.logger.GameLogSaver;
import com.inappstory.sdk.game.reader.logger.GameLogSender;
import com.inappstory.sdk.game.reader.logger.IGameLogSaver;

public class IASLogsImpl implements IASLogs {
    private final IASCore core;

    public IGameLogSaver logSaver() {
        return logSaver;
    }

    IGameLogSaver logSaver;

    public IASLogsImpl(IASCore core) {
        this.core = core;
        logSaver = new GameLogSaver();
        new GameLogSender(core, logSaver);
    }
}
