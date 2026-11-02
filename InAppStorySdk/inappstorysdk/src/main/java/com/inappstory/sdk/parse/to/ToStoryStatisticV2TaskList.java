package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public final class ToStoryStatisticV2TaskList extends ToModelParser<List<StoryStatisticV2Task>> {
    public ToStoryStatisticV2TaskList(String value) {
        super(value);
    }

    @Override
    protected List<StoryStatisticV2Task> parseString(@NonNull String value) {
        try {
            JSONArray tasksJson = new JSONArray(value);
            List<StoryStatisticV2Task> tasks = new ArrayList<>();
            for (int i = 0; i < tasksJson.length(); i++) {
                tasks.add(new ToStoryStatisticV2Task(tasksJson.getJSONObject(i)).parse());
            }
            return tasks;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "List of GameLog";
    }
}
