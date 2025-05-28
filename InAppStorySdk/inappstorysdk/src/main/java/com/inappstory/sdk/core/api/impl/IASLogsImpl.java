package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASLogs;
import com.inappstory.sdk.game.reader.logger.GameLogSaver;
import com.inappstory.sdk.game.reader.logger.GameLogSender;
import com.inappstory.sdk.game.reader.logger.IGameLogSaver;
import com.inappstory.sdk.game.reader.logger.IGameLogSender;

public class IASLogsImpl implements IASLogs {
    private final IASCore core;

    @Override
    public IGameLogSaver logSaver() {
        return logSaver;
    }

    @Override
    public IGameLogSender logSender() {
        return logSender;
    }

    IGameLogSender logSender;

    IGameLogSaver logSaver;

    public IASLogsImpl(IASCore core) {
        this.core = core;
        logSaver = new GameLogSaver(core);
        logSender = new GameLogSender(core, logSaver);
    }
}
