package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.game.reader.logger.GameLog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public final class ToGameLogList extends ToModelParser<List<GameLog>> {
    public ToGameLogList(String value) {
        super(value);
    }

    @Override
    protected List<GameLog> parseString(@NonNull String value) {
        try {
            JSONArray logs = new JSONArray(value);
            List<GameLog> gameLogs = new ArrayList<>();
            for (int i = 0; i < logs.length(); i++) {
                gameLogs.add(new ToGameLog(logs.getJSONObject(i)).parse());
            }
            return gameLogs;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "List of GameLog";
    }
}
