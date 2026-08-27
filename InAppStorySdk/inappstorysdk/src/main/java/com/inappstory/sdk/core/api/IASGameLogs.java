package com.inappstory.sdk.core.api;

import com.inappstory.sdk.game.reader.logger.IGameLogSaver;
import com.inappstory.sdk.game.reader.logger.IGameLogSender;

public interface IASGameLogs {
    IGameLogSaver logSaver();
    IGameLogSender logSender();
}
