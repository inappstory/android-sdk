package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.statistic.IAMStatisticV1Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToIAMStatisticV1TaskTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedTask_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.IAMStatisticV1Task.iamId, 101);
        json.put(ModelFields.IAMStatisticV1Task.event, "impression");
        json.put(ModelFields.IAMStatisticV1Task.eventId, "evt_123");
        json.put(ModelFields.IAMStatisticV1Task.iterationId, "iter_456");
        json.put(ModelFields.IAMStatisticV1Task.slideIndex, 2);
        json.put(ModelFields.IAMStatisticV1Task.layerIndex, 1);
        json.put(ModelFields.IAMStatisticV1Task.slideTotal, 5);
        json.put(ModelFields.IAMStatisticV1Task.durationMs, 1500L);
        json.put(ModelFields.IAMStatisticV1Task.widgetId, "widget_789");
        json.put(ModelFields.IAMStatisticV1Task.widgetLabel, "Submit Button");
        json.put(ModelFields.IAMStatisticV1Task.widgetValue, "click");
        json.put(ModelFields.IAMStatisticV1Task.value, "some_value");
        json.put(ModelFields.IAMStatisticV1Task.slideAnalytics, "analytics_data");
        json.put(ModelFields.IAMStatisticV1Task.widgetAnswerLabel, "Correct");
        json.put(ModelFields.IAMStatisticV1Task.widgetAnswer, 1);
        json.put(ModelFields.IAMStatisticV1Task.widgetAnswerScore, 10);

        ToIAMStatisticV1Task parser = new ToIAMStatisticV1Task(json.toString());
        IAMStatisticV1Task result = parser.parse();

        assertNotNull(result);
        assertEquals(Integer.valueOf(101), result.iamId);
        assertEquals("impression", result.event);
        assertEquals("evt_123", result.eventId);
        assertEquals("iter_456", result.iterationId);
        assertEquals(Integer.valueOf(2), result.slideIndex);
        assertEquals(Integer.valueOf(1), result.layerIndex);
        assertEquals(Integer.valueOf(5), result.slideTotal);
        assertEquals(Long.valueOf(1500L), result.durationMs);
        assertEquals("widget_789", result.widgetId);
        assertEquals("Submit Button", result.widgetLabel);
        assertEquals("click", result.widgetValue);
        assertEquals("some_value", result.value);
        assertEquals("analytics_data", result.slideAnalytics);
        assertEquals("Correct", result.widgetAnswerLabel);
        assertEquals(Integer.valueOf(1), result.widgetAnswer);
        assertEquals(Integer.valueOf(10), result.widgetAnswerScore);
    }

    @Test
    public void parse_returnsTaskWithNullAndZeroValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToIAMStatisticV1Task parser = new ToIAMStatisticV1Task(json.toString());
        IAMStatisticV1Task result = parser.parse();

        assertNotNull(result);
        assertNull(result.iamId);
        assertNull(result.event);
        assertNull(result.eventId);
        assertNull(result.iterationId);
        assertNull(result.slideIndex);
        assertNull(result.layerIndex);
        assertNull(result.slideTotal);
        assertNull(result.durationMs);
        assertNull(result.widgetId);
        assertNull(result.widgetLabel);
        assertNull(result.widgetValue);
        assertNull(result.value);
        assertNull(result.slideAnalytics);
        assertNull(result.widgetAnswerLabel);
        assertNull(result.widgetAnswer);
        assertNull(result.widgetAnswerScore);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToIAMStatisticV1Task parser = new ToIAMStatisticV1Task(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToIAMStatisticV1Task parser = new ToIAMStatisticV1Task("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToIAMStatisticV1Task parser = new ToIAMStatisticV1Task("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        
        // Передаем невалидные типы данных для числовых полей
        json.put(ModelFields.IAMStatisticV1Task.iamId, "not_an_int");
        json.put(ModelFields.IAMStatisticV1Task.slideIndex, new JSONObject());
        json.put(ModelFields.IAMStatisticV1Task.durationMs, "not_a_long");
        json.put(ModelFields.IAMStatisticV1Task.widgetAnswer, true);
        json.put(ModelFields.IAMStatisticV1Task.widgetAnswerScore, new JSONArray());
        
        // Передаем невалидные типы данных для строковых полей
        json.put(ModelFields.IAMStatisticV1Task.event, 12345);
        json.put(ModelFields.IAMStatisticV1Task.widgetId, new JSONObject());

        ToIAMStatisticV1Task parser = new ToIAMStatisticV1Task(json.toString());
        IAMStatisticV1Task result = parser.parse();

        // Благодаря opt*Nullable методам и блоку try-catch, парсинг не должен приводить к крашу.
        // Невалидные поля просто получат значения по умолчанию (null или 0).
        assertNotNull(result);
        assertNull(result.iamId);
        assertNull(result.slideIndex);
        assertNull(result.durationMs);
        assertNull(result.widgetAnswer);
        assertNull(result.widgetAnswerScore);
        assertNull(result.event);
        assertNull(result.widgetId);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToIAMStatisticV1Task parser = new ToIAMStatisticV1Task("{}");
        assertEquals("IAMStatisticV1Task", parser.getModelClassName());
    }
}