package com.inappstory.sdk.core.api;

import com.inappstory.sdk.game.reader.logger.IGameLogSaver;
import com.inappstory.sdk.game.reader.logger.IGameLogSender;

public interface IASLogs {
    IGameLogSaver logSaver();
    IGameLogSender logSender();
}
