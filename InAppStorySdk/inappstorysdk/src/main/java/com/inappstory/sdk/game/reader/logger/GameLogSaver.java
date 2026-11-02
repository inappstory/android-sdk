package com.inappstory.sdk.game.reader.logger;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.from.FromListOfGameLog;
import com.inappstory.sdk.parse.to.ToGameLogList;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;

import java.util.ArrayList;
import java.util.List;

public class GameLogSaver implements IGameLogSaver {
    private final IASCore core;
    private final Object lock = new Object();

    public GameLogSaver(IASCore core) {
        this.core = core;
    }

    private final String GAME_LOG_KEY = "gameLogKey";

    public void saveLog(GameLog log) {
        synchronized (lock) {
            String prefsLog = core.sharedPreferencesAPI().getString(GAME_LOG_KEY);
            List<GameLog> currentLogs = null;
            if (prefsLog != null)
                currentLogs = new ToGameLogList(prefsLog).parse();
            if (currentLogs == null) currentLogs = new ArrayList<>();
            currentLogs.add(log);
            prefsLog = new FromListOfGameLog(currentLogs).parse();
            try {
                if (prefsLog != null)
                    core.sharedPreferencesAPI().saveString(GAME_LOG_KEY, prefsLog);
            } catch (Exception e) {

            }
        }
    }


    public List<GameLog> getLogs() {
        synchronized (lock) {
            String prefsLog = core.sharedPreferencesAPI().getString(GAME_LOG_KEY);
            core.sharedPreferencesAPI().removeString(GAME_LOG_KEY);
            return new ToGameLogList(prefsLog).parse();
        }
    }

    public void saveLogs(List<GameLog> logs) {
        synchronized (lock) {
            try {
                String prefsLog = new FromListOfGameLog(logs).parse();
                if (prefsLog != null)
                    core.sharedPreferencesAPI().saveString(GAME_LOG_KEY, prefsLog);
            } catch (Exception ignored) {

            }
        }
    }
}
