package com.inappstory.sdk.game.reader.logger;

import java.util.List;

public interface IGameLogSaver {
    void saveLog(GameLog log);

    void saveLogs(List<GameLog> logList);

    List<GameLog> getLogs();
}
