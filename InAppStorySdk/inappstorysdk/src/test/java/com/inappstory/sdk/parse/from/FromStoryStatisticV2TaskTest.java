package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONObject;
import org.junit.Test;

public class FromStoryStatisticV2TaskTest {

    @Test
    public void parseToJSONObject_returnsValidJsonObject_whenModelIsPopulated() throws Exception {
        StoryStatisticV2Task model = new StoryStatisticV2Task();
        model.event = "slide_view";
        model.timestamp = 1700000000L;
        model.userId = "user_123";
        model.sessionId = "sess_456";
        model.isFake = false;
        model.storyId = "story_789";
        model.feedId = "feed_000";
        model.whence = "banner";
        model.target = "click";
        model.slideIndex = 2;
        model.slideTotal = 5;
        model.durationMs = 1500L;
        model.value = "test_value";
        model.cause = "user_action";
        model.widgetId = "widget_1";
        model.widgetLabel = "Submit";
        model.widgetValue = "yes";
        model.widgetAnswer = 1;
        model.widgetAnswerLabel = "Correct";
        model.widgetAnswerScore = 10;
        model.layoutIndex = 0;
        model.mode = 1;
        model.fullData = "{\"extra\":\"data\"}";

        FromStoryStatisticV2Task parser = new FromStoryStatisticV2Task(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals("slide_view", result.getString(ModelFields.StoryStatisticV2Task.event));
        assertEquals(1700000000L, result.getLong(ModelFields.StoryStatisticV2Task.timestamp));
        assertEquals("user_123", result.getString(ModelFields.StoryStatisticV2Task.userId));
        assertEquals("sess_456", result.getString(ModelFields.StoryStatisticV2Task.sessionId));
        assertFalse(result.getBoolean(ModelFields.StoryStatisticV2Task.isFake));
        assertEquals("story_789", result.getString(ModelFields.StoryStatisticV2Task.storyId));
        assertEquals("feed_000", result.getString(ModelFields.StoryStatisticV2Task.feedId));
        assertEquals("banner", result.getString(ModelFields.StoryStatisticV2Task.whence));
        assertEquals("click", result.getString(ModelFields.StoryStatisticV2Task.target));
        assertEquals(2, result.getInt(ModelFields.StoryStatisticV2Task.slideIndex));
        assertEquals(5, result.getInt(ModelFields.StoryStatisticV2Task.slideTotal));
        assertEquals(1500L, result.getLong(ModelFields.StoryStatisticV2Task.durationMs));
        assertEquals("test_value", result.getString(ModelFields.StoryStatisticV2Task.value));
        assertEquals("user_action", result.getString(ModelFields.StoryStatisticV2Task.cause));
        assertEquals("widget_1", result.getString(ModelFields.StoryStatisticV2Task.widgetId));
        assertEquals("Submit", result.getString(ModelFields.StoryStatisticV2Task.widgetLabel));
        assertEquals("yes", result.getString(ModelFields.StoryStatisticV2Task.widgetValue));
        assertEquals(1, result.getInt(ModelFields.StoryStatisticV2Task.widgetAnswer));
        assertEquals("Correct", result.getString(ModelFields.StoryStatisticV2Task.widgetAnswerLabel));
        assertEquals(10, result.getInt(ModelFields.StoryStatisticV2Task.widgetAnswerScore));
        assertEquals(0, result.getInt(ModelFields.StoryStatisticV2Task.layoutIndex));
        assertEquals(1, result.getInt(ModelFields.StoryStatisticV2Task.mode));
        assertEquals("{\"extra\":\"data\"}", result.getString(ModelFields.StoryStatisticV2Task.fullData));
    }

    @Test(expected = NullPointerException.class)
    public void parseToJSONObject_throwsNPE_whenModelIsNull() throws Exception {
        FromStoryStatisticV2Task parser = new FromStoryStatisticV2Task(null);
        parser.parseToJSONObject();
    }

    @Test
    public void parseToJSONObject_handlesNullFields() throws Exception {
        StoryStatisticV2Task model = new StoryStatisticV2Task();
        // Все строковые и объектные поля null
        model.event = null;
        model.userId = null;
        model.sessionId = null;
        model.storyId = null;
        model.feedId = null;
        model.whence = null;
        model.target = null;
        model.value = null;
        model.cause = null;
        model.widgetId = null;
        model.widgetLabel = null;
        model.widgetValue = null;
        model.widgetAnswerLabel = null;
        model.fullData = null;
        
        // Примитивные и их обертки имеют значения по умолчанию (null для Integer/Long, 0/false для примитивов)
        model.timestamp = null;
        model.slideIndex = null;
        model.slideTotal = null;
        model.durationMs = null;
        model.widgetAnswer = null;
        model.widgetAnswerScore = null;
        model.layoutIndex = null;
        model.mode = null;

        FromStoryStatisticV2Task parser = new FromStoryStatisticV2Task(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        // Строковые и объектные поля с null не добавляются в JSON
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.event));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.userId));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.sessionId));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.storyId));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.feedId));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.whence));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.target));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.value));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.cause));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.widgetId));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.widgetLabel));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.widgetValue));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.widgetAnswerLabel));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.fullData));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.timestamp));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.slideIndex));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.slideTotal));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.durationMs));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.widgetAnswer));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.widgetAnswerScore));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.layoutIndex));
        assertFalse(result.has(ModelFields.StoryStatisticV2Task.mode));
    }
}