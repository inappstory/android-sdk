package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
        object.put(ModelFields.StoryStatisticV2Task.event, model.event);
        object.put(ModelFields.StoryStatisticV2Task.timestamp, model.timestamp);
        object.put(ModelFields.StoryStatisticV2Task.userId, model.userId);
        object.put(ModelFields.StoryStatisticV2Task.sessionId, model.sessionId);
        object.put(ModelFields.StoryStatisticV2Task.isFake, model.isFake);
        object.put(ModelFields.StoryStatisticV2Task.storyId, model.storyId);
        object.put(ModelFields.StoryStatisticV2Task.feedId, model.feedId);
        object.put(ModelFields.StoryStatisticV2Task.whence, model.whence);
        object.put(ModelFields.StoryStatisticV2Task.target, model.target);
        object.put(ModelFields.StoryStatisticV2Task.slideIndex, model.slideIndex);
        object.put(ModelFields.StoryStatisticV2Task.slideTotal, model.slideTotal);
        object.put(ModelFields.StoryStatisticV2Task.durationMs, model.durationMs);
        object.put(ModelFields.StoryStatisticV2Task.value, model.value);
        object.put(ModelFields.StoryStatisticV2Task.cause, model.cause);
        object.put(ModelFields.StoryStatisticV2Task.widgetId, model.widgetId);
        object.put(ModelFields.StoryStatisticV2Task.widgetLabel, model.widgetLabel);
        object.put(ModelFields.StoryStatisticV2Task.widgetValue, model.widgetValue);
        object.put(ModelFields.StoryStatisticV2Task.widgetAnswer, model.widgetAnswer);
        object.put(ModelFields.StoryStatisticV2Task.widgetAnswerLabel, model.widgetAnswerLabel);
        object.put(ModelFields.StoryStatisticV2Task.widgetAnswerScore, model.widgetAnswerScore);
        object.put(ModelFields.StoryStatisticV2Task.layoutIndex, model.layoutIndex);
        object.put(ModelFields.StoryStatisticV2Task.mode, model.mode);
        object.put(ModelFields.StoryStatisticV2Task.fullData, model.fullData);
        return object;
    }
}
