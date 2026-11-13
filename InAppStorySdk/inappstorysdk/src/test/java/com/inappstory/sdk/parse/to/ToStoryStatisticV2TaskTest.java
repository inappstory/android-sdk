package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToStoryStatisticV2TaskTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedTask_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryStatisticV2Task.event, "story_open");
        json.put(ModelFields.StoryStatisticV2Task.timestamp, 1700000000000L);
        json.put(ModelFields.StoryStatisticV2Task.userId, "user_1");
        json.put(ModelFields.StoryStatisticV2Task.sessionId, "sess_2");
        json.put(ModelFields.StoryStatisticV2Task.isFake, true);
        json.put(ModelFields.StoryStatisticV2Task.storyId, "story_10");
        json.put(ModelFields.StoryStatisticV2Task.feedId, "feed_20");
        json.put(ModelFields.StoryStatisticV2Task.whence, "onboarding");
        json.put(ModelFields.StoryStatisticV2Task.target, "slide_2");
        json.put(ModelFields.StoryStatisticV2Task.cause, "tap");
        json.put(ModelFields.StoryStatisticV2Task.value, "val_1");
        json.put(ModelFields.StoryStatisticV2Task.widgetId, "w_1");
        json.put(ModelFields.StoryStatisticV2Task.widgetLabel, "Vote");
        json.put(ModelFields.StoryStatisticV2Task.widgetValue, "option_a");
        json.put(ModelFields.StoryStatisticV2Task.widgetAnswerLabel, "Option A");
        json.put(ModelFields.StoryStatisticV2Task.fullData, "{\"raw\":1}");
        json.put(ModelFields.StoryStatisticV2Task.durationMs, 4200L);
        json.put(ModelFields.StoryStatisticV2Task.slideIndex, 1);
        json.put(ModelFields.StoryStatisticV2Task.slideTotal, 5);
        json.put(ModelFields.StoryStatisticV2Task.widgetAnswer, 2);
        json.put(ModelFields.StoryStatisticV2Task.widgetAnswerScore, 50);
        json.put(ModelFields.StoryStatisticV2Task.layoutIndex, 0);
        json.put(ModelFields.StoryStatisticV2Task.mode, 1);

        StoryStatisticV2Task result = new ToStoryStatisticV2Task(json).parse();

        assertNotNull(result);
        assertEquals("story_open", result.event);
        assertEquals(Long.valueOf(1700000000000L), result.timestamp);
        assertEquals("user_1", result.userId);
        assertEquals("sess_2", result.sessionId);
        assertTrue(result.isFake);
        assertEquals("story_10", result.storyId);
        assertEquals("feed_20", result.feedId);
        assertEquals("onboarding", result.whence);
        assertEquals("slide_2", result.target);
        assertEquals("tap", result.cause);
        assertEquals("val_1", result.value);
        assertEquals("w_1", result.widgetId);
        assertEquals("Vote", result.widgetLabel);
        assertEquals("option_a", result.widgetValue);
        assertEquals("Option A", result.widgetAnswerLabel);
        assertEquals("{\"raw\":1}", result.fullData);
        assertEquals(Long.valueOf(4200L), result.durationMs);
        assertEquals(Integer.valueOf(1), result.slideIndex);
        assertEquals(Integer.valueOf(5), result.slideTotal);
        assertEquals(Integer.valueOf(2), result.widgetAnswer);
        assertEquals(Integer.valueOf(50), result.widgetAnswerScore);
        assertEquals(Integer.valueOf(0), result.layoutIndex);
        assertEquals(Integer.valueOf(1), result.mode);
    }

    @Test
    public void parse_returnsPopulatedTask_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryStatisticV2Task.event, "slide_show");
        json.put(ModelFields.StoryStatisticV2Task.slideIndex, 3);
        json.put(ModelFields.StoryStatisticV2Task.isFake, false);

        StoryStatisticV2Task result = new ToStoryStatisticV2Task(json.toString()).parse();

        assertNotNull(result);
        assertEquals("slide_show", result.event);
        assertEquals(Integer.valueOf(3), result.slideIndex);
        assertFalse(result.isFake);
    }

    @Test
    public void parse_returnsDefaultValues_whenJsonIsEmpty() {
        StoryStatisticV2Task result = new ToStoryStatisticV2Task("{}").parse();

        assertNotNull(result);
        assertNull(result.event);
        assertNull(result.timestamp);
        assertNull(result.userId);
        assertNull(result.sessionId);
        assertFalse(result.isFake);
        assertNull(result.storyId);
        assertNull(result.feedId);
        assertNull(result.whence);
        assertNull(result.target);
        assertNull(result.cause);
        assertNull(result.value);
        assertNull(result.widgetId);
        assertNull(result.widgetLabel);
        assertNull(result.widgetValue);
        assertNull(result.widgetAnswerLabel);
        assertNull(result.fullData);
        assertNull(result.durationMs);
        assertNull(result.slideIndex);
        assertNull(result.slideTotal);
        assertNull(result.widgetAnswer);
        assertNull(result.widgetAnswerScore);
        assertNull(result.layoutIndex);
        assertNull(result.mode);
    }

    // --- Обработка null и невалидного JSON ---

    @Test
    public void parse_returnsNull_whenInputsAreNullOrMalformed() {
        assertNull(new ToStoryStatisticV2Task((String) null).parse());
        assertNull(new ToStoryStatisticV2Task((JSONObject) null).parse());
        assertNull(new ToStoryStatisticV2Task("").parse());
        assertNull(new ToStoryStatisticV2Task("{broken_json").parse());
        assertNull(new ToStoryStatisticV2Task("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_handlesInvalidDataTypes_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Нестроковые типы и строковый "null" для String-полей
        json.put(ModelFields.StoryStatisticV2Task.event, 123);
        json.put(ModelFields.StoryStatisticV2Task.userId, new JSONObject());
        json.put(ModelFields.StoryStatisticV2Task.sessionId, new JSONArray());
        json.put(ModelFields.StoryStatisticV2Task.storyId, true);
        json.put(ModelFields.StoryStatisticV2Task.feedId, " null ");
        json.put(ModelFields.StoryStatisticV2Task.whence, JSONObject.NULL);
        json.put(ModelFields.StoryStatisticV2Task.target, 45.6);
        json.put(ModelFields.StoryStatisticV2Task.cause, new JSONObject());
        json.put(ModelFields.StoryStatisticV2Task.value, new JSONArray());
        json.put(ModelFields.StoryStatisticV2Task.widgetId, 999);
        json.put(ModelFields.StoryStatisticV2Task.widgetLabel, false);
        json.put(ModelFields.StoryStatisticV2Task.widgetValue, " null ");
        json.put(ModelFields.StoryStatisticV2Task.widgetAnswerLabel, 0);
        json.put(ModelFields.StoryStatisticV2Task.fullData, new JSONArray());

        // Невалидные типы для boolean, Long и Integer полей
        json.put(ModelFields.StoryStatisticV2Task.isFake, "not_a_boolean");
        json.put(ModelFields.StoryStatisticV2Task.timestamp, "not_a_long");
        json.put(ModelFields.StoryStatisticV2Task.durationMs, new JSONObject());
        json.put(ModelFields.StoryStatisticV2Task.slideIndex, "not_an_int");
        json.put(ModelFields.StoryStatisticV2Task.slideTotal, new JSONArray());
        json.put(ModelFields.StoryStatisticV2Task.widgetAnswer, false);
        json.put(ModelFields.StoryStatisticV2Task.widgetAnswerScore, "null");
        json.put(ModelFields.StoryStatisticV2Task.layoutIndex, new JSONObject());
        json.put(ModelFields.StoryStatisticV2Task.mode, "invalid");

        StoryStatisticV2Task result = new ToStoryStatisticV2Task(json).parse();

        assertNotNull(result);
        assertNull(result.event);
        assertNull(result.userId);
        assertNull(result.sessionId);
        assertNull(result.storyId);
        assertNull(result.feedId);
        assertNull(result.whence);
        assertNull(result.target);
        assertNull(result.cause);
        assertNull(result.value);
        assertNull(result.widgetId);
        assertNull(result.widgetLabel);
        assertNull(result.widgetValue);
        assertNull(result.widgetAnswerLabel);
        assertNull(result.fullData);

        assertFalse(result.isFake);
        assertNull(result.timestamp);
        assertNull(result.durationMs);
        assertNull(result.slideIndex);
        assertNull(result.slideTotal);
        assertNull(result.widgetAnswer);
        assertNull(result.widgetAnswerScore);
        assertNull(result.layoutIndex);
        assertNull(result.mode);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStoryStatisticV2Task parser = new ToStoryStatisticV2Task("{}");

        assertEquals("StoryStatisticV2Task", parser.getModelClassName());
    }
}
