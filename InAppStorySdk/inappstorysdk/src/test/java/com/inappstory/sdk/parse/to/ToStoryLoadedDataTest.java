package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToStoryLoadedDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedData_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryLoadedData.id, 101);
        json.put(ModelFields.StoryLoadedData.index, 5);

        ToStoryLoadedData parser = new ToStoryLoadedData(json.toString());
        StoryLoadedData result = parser.parse();

        assertNotNull(result);
        assertEquals(101, result.id);
        assertEquals(5, result.index);
    }

    @Test
    public void parse_returnsDataWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToStoryLoadedData parser = new ToStoryLoadedData(json.toString());
        StoryLoadedData result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.id); // optInt по умолчанию возвращает 0
        assertEquals(0, result.index);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToStoryLoadedData parser = new ToStoryLoadedData(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToStoryLoadedData parser = new ToStoryLoadedData("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToStoryLoadedData parser = new ToStoryLoadedData("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put(ModelFields.StoryLoadedData.id, "not_an_int");
        json.put(ModelFields.StoryLoadedData.index, new JSONObject());

        ToStoryLoadedData parser = new ToStoryLoadedData(json.toString());
        StoryLoadedData result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertEquals(0, result.id);
        assertEquals(0, result.index);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStoryLoadedData parser = new ToStoryLoadedData("{}");
        assertEquals("StoryLoadedData", parser.getModelClassName());
    }
}