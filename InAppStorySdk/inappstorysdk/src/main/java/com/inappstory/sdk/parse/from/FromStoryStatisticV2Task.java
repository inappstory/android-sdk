package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONObject;

public class FromStoryStatisticV2Task extends
        ModelToJSONObjectParser<StoryStatisticV2Task> {
    public FromStoryStatisticV2Task(
            StoryStatisticV2Task model
    ) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("event", model.event);
        object.put("ts", model.timestamp);
        object.put("u", model.userId);
        object.put("s", model.sessionId);
        object.put("isFake", model.isFake);
        object.put("i", model.storyId);
        object.put("f", model.feedId);
        object.put("w", model.whence);
        object.put("t", model.target);
        object.put("si", model.slideIndex);
        object.put("st", model.slideTotal);
        object.put("d", model.durationMs);
        object.put("v", model.value);
        object.put("wi", model.widgetId);
        object.put("wl", model.widgetLabel);
        object.put("wv", model.widgetValue);
        object.put("wa", model.widgetAnswer);
        object.put("wal", model.widgetAnswerLabel);
        object.put("was", model.widgetAnswerScore);
        object.put("li", model.layoutIndex);
        object.put("m", model.mode);
        object.put("fullData", model.fullData);
        return object;
    }
}
