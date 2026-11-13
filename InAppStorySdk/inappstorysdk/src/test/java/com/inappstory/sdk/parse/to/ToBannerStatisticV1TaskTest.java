package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.statistic.BannerStatisticV1Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToBannerStatisticV1TaskTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedTask_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.BannerStatisticV1Task.bannerId, 10);
        json.put(ModelFields.BannerStatisticV1Task.event, "banner_click");
        json.put(ModelFields.BannerStatisticV1Task.eventId, "evt_001");
        json.put(ModelFields.BannerStatisticV1Task.iterationId, "iter_5");
        json.put(ModelFields.BannerStatisticV1Task.slideIndex, 2);
        json.put(ModelFields.BannerStatisticV1Task.slideTotal, 4);
        json.put(ModelFields.BannerStatisticV1Task.durationMs, 3500L);
        json.put(ModelFields.BannerStatisticV1Task.widgetId, "w_12");
        json.put(ModelFields.BannerStatisticV1Task.widgetLabel, "Submit");
        json.put(ModelFields.BannerStatisticV1Task.widgetValue, "yes");
        json.put(ModelFields.BannerStatisticV1Task.widgetAnswerLabel, "Answer A");
        json.put(ModelFields.BannerStatisticV1Task.widgetAnswer, 1);
        json.put(ModelFields.BannerStatisticV1Task.widgetAnswerScore, 100);

        BannerStatisticV1Task result = new ToBannerStatisticV1Task(json.toString()).parse();

        assertNotNull(result);
        assertEquals(Integer.valueOf(10), result.bannerId);
        assertEquals("banner_click", result.event);
        assertEquals("evt_001", result.eventId);
        assertEquals("iter_5", result.iterationId);
        assertEquals(Integer.valueOf(2), result.slideIndex);
        assertEquals(Integer.valueOf(4), result.slideTotal);
        assertEquals(Long.valueOf(3500L), result.durationMs);
        assertEquals("w_12", result.widgetId);
        assertEquals("Submit", result.widgetLabel);
        assertEquals("yes", result.widgetValue);
        assertEquals("Answer A", result.widgetAnswerLabel);
        assertEquals(Integer.valueOf(1), result.widgetAnswer);
        assertEquals(Integer.valueOf(100), result.widgetAnswerScore);
    }

    @Test
    public void parse_returnsTaskWithAllNullFields_whenJsonIsEmpty() {
        BannerStatisticV1Task result = new ToBannerStatisticV1Task("{}").parse();

        assertNotNull(result);
        assertNull(result.bannerId);
        assertNull(result.event);
        assertNull(result.eventId);
        assertNull(result.iterationId);
        assertNull(result.slideIndex);
        assertNull(result.slideTotal);
        assertNull(result.durationMs);
        assertNull(result.widgetId);
        assertNull(result.widgetLabel);
        assertNull(result.widgetValue);
        assertNull(result.widgetAnswerLabel);
        assertNull(result.widgetAnswer);
        assertNull(result.widgetAnswerScore);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToBannerStatisticV1Task(null).parse());
        assertNull(new ToBannerStatisticV1Task("").parse());
        assertNull(new ToBannerStatisticV1Task("{broken_json").parse());
        assertNull(new ToBannerStatisticV1Task("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenDataTypesAreInvalid() throws JSONException {
        JSONObject json = new JSONObject();
        // Строки и объекты вместо Integer / Long
        json.put(ModelFields.BannerStatisticV1Task.bannerId, "not_an_int");
        json.put(ModelFields.BannerStatisticV1Task.slideIndex, new JSONObject());
        json.put(ModelFields.BannerStatisticV1Task.slideTotal, new JSONArray());
        json.put(ModelFields.BannerStatisticV1Task.durationMs, "not_a_long");
        json.put(ModelFields.BannerStatisticV1Task.widgetAnswer, false);
        json.put(ModelFields.BannerStatisticV1Task.widgetAnswerScore, "null");

        // Числа, массивы, объекты и строковый "null" вместо String
        json.put(ModelFields.BannerStatisticV1Task.event, 123);
        json.put(ModelFields.BannerStatisticV1Task.eventId, new JSONObject());
        json.put(ModelFields.BannerStatisticV1Task.iterationId, new JSONArray());
        json.put(ModelFields.BannerStatisticV1Task.widgetId, true);
        json.put(ModelFields.BannerStatisticV1Task.widgetLabel, " null ");
        json.put(ModelFields.BannerStatisticV1Task.widgetValue, JSONObject.NULL);
        json.put(ModelFields.BannerStatisticV1Task.widgetAnswerLabel, 99.9);

        BannerStatisticV1Task result = new ToBannerStatisticV1Task(json.toString()).parse();

        assertNotNull(result);
        assertNull(result.bannerId);
        assertNull(result.slideIndex);
        assertNull(result.slideTotal);
        assertNull(result.durationMs);
        assertNull(result.widgetAnswer);
        assertNull(result.widgetAnswerScore);

        assertNull(result.event);
        assertNull(result.eventId);
        assertNull(result.iterationId);
        assertNull(result.widgetId);
        assertNull(result.widgetLabel);
        assertNull(result.widgetValue);
        assertNull(result.widgetAnswerLabel);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToBannerStatisticV1Task parser = new ToBannerStatisticV1Task("{}");

        assertEquals("BannerStatisticV1Task", parser.getModelClassName());
    }
}