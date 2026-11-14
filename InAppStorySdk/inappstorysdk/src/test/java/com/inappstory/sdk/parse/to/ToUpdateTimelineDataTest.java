package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.UpdateTimelineData;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToUpdateTimelineDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedData_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.UpdateTimelineData.action, "next");
        json.put(ModelFields.UpdateTimelineData.currentTime, 1500L);
        json.put(ModelFields.UpdateTimelineData.duration, 5000L);
        json.put(ModelFields.UpdateTimelineData.showLoader, true);
        json.put(ModelFields.UpdateTimelineData.showError, false);
        json.put(ModelFields.UpdateTimelineData.slideIndex, 3);

        ToUpdateTimelineData parser = new ToUpdateTimelineData(json.toString());
        UpdateTimelineData result = parser.parse();

        assertNotNull(result);
        assertEquals("next", result.action);
        assertEquals(1500L, result.currentTime);
        assertEquals(5000L, result.duration);
        assertTrue(result.showLoader);
        assertFalse(result.showError);
        assertEquals(3, result.slideIndex);
    }

    @Test
    public void parse_returnsDataWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToUpdateTimelineData parser = new ToUpdateTimelineData(json.toString());
        UpdateTimelineData result = parser.parse();

        assertNotNull(result);
        assertNull(result.action);
        assertEquals(0L, result.currentTime);
        assertEquals(0L, result.duration);
        assertFalse(result.showLoader);
        assertFalse(result.showError);
        assertEquals(0, result.slideIndex);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToUpdateTimelineData parser = new ToUpdateTimelineData(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToUpdateTimelineData parser = new ToUpdateTimelineData("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToUpdateTimelineData parser = new ToUpdateTimelineData("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных для всех полей
        json.put(ModelFields.UpdateTimelineData.action, 123);
        json.put(ModelFields.UpdateTimelineData.currentTime, "not_a_long");
        json.put(ModelFields.UpdateTimelineData.duration, new JSONObject());
        json.put(ModelFields.UpdateTimelineData.showLoader, "not_a_boolean");
        json.put(ModelFields.UpdateTimelineData.showError, 999);
        json.put(ModelFields.UpdateTimelineData.slideIndex, "not_an_int");

        ToUpdateTimelineData parser = new ToUpdateTimelineData(json.toString());
        UpdateTimelineData result = parser.parse();

        // Благодаря блоку try-catch или внутренней логике opt-методов, 
        // парсинг не должен приводить к неперехваченному исключению.
        assertNotNull(result);
        assertNull(result.action);
        assertEquals(0L, result.currentTime);
        assertEquals(0L, result.duration);
        assertFalse(result.showLoader);
        assertFalse(result.showError);
        assertEquals(0, result.slideIndex);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToUpdateTimelineData parser = new ToUpdateTimelineData("{}");
        assertEquals("UpdateTimelineData", parser.getModelClassName());
    }
}