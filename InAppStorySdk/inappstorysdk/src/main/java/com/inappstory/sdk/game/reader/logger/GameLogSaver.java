package com.inappstory.sdk.game.reader.logger;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;

import java.util.ArrayList;
import java.util.List;

public class GameLogSaver implements IGameLogSaver {
    private final IASCore core;

    public GameLogSaver(IASCore core) {
        this.core = core;
    }

    private final String GAME_LOG_KEY = "gameLogKey";

    public void saveLog(GameLog log) {
        synchronized (this) {
            String prefsLog = core.sharedPreferencesAPI().getString(GAME_LOG_KEY);
            List<GameLog> currentLogs = new ArrayList<>();
            if (prefsLog != null)
                currentLogs = JsonParser.listFromJson(prefsLog, GameLog.class);
            currentLogs.add(log);
            try {
                prefsLog = JsonParser.getJson(currentLogs);
                core.sharedPreferencesAPI().saveString(GAME_LOG_KEY, prefsLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public List<GameLog> getLogs() {
        synchronized (this) {
            String prefsLog = core.sharedPreferencesAPI().getString(GAME_LOG_KEY);
            core.sharedPreferencesAPI().removeString(GAME_LOG_KEY);
            return JsonParser.listFromJson(prefsLog, GameLog.class);
        }
    }

    public void saveLogs(List<GameLog> logs) {
        synchronized (this) {
            try {
                String prefsLog = JsonParser.getJson(logs);
                core.sharedPreferencesAPI().saveString(GAME_LOG_KEY, prefsLog);
            } catch (Exception ignored) {

            }
        }
    }
}
