package com.inappstory.sdk.game.reader.logger;

import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;

import java.util.ArrayList;
import java.util.List;

public class GameLogSaver implements IGameLogSaver {
    private final String GAME_LOG_KEY = "gameLogKey";

    public void saveLog(GameLog log) {
        String prefsLog = SharedPreferencesAPI.getString(GAME_LOG_KEY);
        List<GameLog> currentLogs = new ArrayList<>();
        if (prefsLog != null)
            currentLogs = JsonParser.listFromJson(prefsLog, GameLog.class);
        currentLogs.add(log);
        try {
            prefsLog = JsonParser.getJson(currentLogs);
            SharedPreferencesAPI.saveString(GAME_LOG_KEY, prefsLog);
        } catch (Exception ignored) {

        }
    }



    public List<GameLog> getLogs() {
        String prefsLog = SharedPreferencesAPI.getString(GAME_LOG_KEY);
        SharedPreferencesAPI.removeString(GAME_LOG_KEY);
        return JsonParser.listFromJson(prefsLog, GameLog.class);
    }

    public void saveLogs(List<GameLog> logs) {
        try {
            String prefsLog = JsonParser.getJson(logs);
            SharedPreferencesAPI.saveString(GAME_LOG_KEY, prefsLog);
        } catch (Exception ignored) {

        }
    }
}
