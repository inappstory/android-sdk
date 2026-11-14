package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToPreloadGameCenterDataListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsDataList_whenValidJsonArrayStringProvided() throws JSONException {
        JSONObject data1 = new JSONObject();
        data1.put(ModelFields.PreloadGameCenterData.id, "game_1");
        data1.put(ModelFields.PreloadGameCenterData.url, "https://example.com/1.zip");

        JSONObject data2 = new JSONObject();
        data2.put(ModelFields.PreloadGameCenterData.id, "game_2");
        data2.put(ModelFields.PreloadGameCenterData.url, "https://example.com/2.zip");

        JSONArray array = new JSONArray();
        array.put(data1);
        array.put(data2);

        ToPreloadGameCenterDataList parser = new ToPreloadGameCenterDataList(array.toString());
        List<PreloadGameCenterData> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("game_1", result.get(0).id);
        assertEquals("game_2", result.get(1).id);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayStringProvided() {
        ToPreloadGameCenterDataList parser = new ToPreloadGameCenterDataList("[]");
        List<PreloadGameCenterData> result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToPreloadGameCenterDataList((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToPreloadGameCenterDataList("{invalid_json").parse());
    }

    @Test
    public void parse_skipsInvalidElements_andReturnsValidOnes() throws JSONException {
        JSONArray array = new JSONArray();
        
        // 1. Валидный элемент
        JSONObject validData = new JSONObject();
        validData.put(ModelFields.PreloadGameCenterData.id, "game_1");
        validData.put(ModelFields.PreloadGameCenterData.url, "https://example.com/1.zip");
        array.put(validData);
        
        // 2. Невалидный элемент: отсутствует обязательное поле 'url'
        JSONObject invalidDataMissingField = new JSONObject();
        invalidDataMissingField.put(ModelFields.PreloadGameCenterData.id, "game_2");
        array.put(invalidDataMissingField);
        
        // 3. Невалидный элемент: не JSONObject (строка)
        array.put("not_an_object");
        
        // 4. Невалидный элемент: не JSONObject (число)
        array.put(123);

        ToPreloadGameCenterDataList parser = new ToPreloadGameCenterDataList(array.toString());
        List<PreloadGameCenterData> result = parser.parse();

        // Благодаря конструкции `if (data == null) continue;`, невалидные элементы пропускаются
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("game_1", result.get(0).id);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToPreloadGameCenterDataList parser = new ToPreloadGameCenterDataList("[]");
        assertEquals("List of PreloadGameCenterData", parser.getModelClassName());
    }
}