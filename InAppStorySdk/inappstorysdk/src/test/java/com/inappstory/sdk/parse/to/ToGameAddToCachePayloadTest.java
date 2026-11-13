package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameAddToCachePayload;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameAddToCachePayloadTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedPayload_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameAddToCachePayload.id, "cache_item_1");
        json.put(ModelFields.GameAddToCachePayload.cb, "onCacheReady");
        json.put(ModelFields.GameAddToCachePayload.webResource, new JSONObject());

        GameAddToCachePayload result = new ToGameAddToCachePayload(json.toString()).parse();

        assertNotNull(result);
        assertEquals("cache_item_1", result.id);
        assertEquals("onCacheReady", result.cb);
    }

    @Test
    public void parse_returnsPayloadWithNullFields_whenJsonIsEmpty() {
        GameAddToCachePayload result = new ToGameAddToCachePayload("{}").parse();

        assertNotNull(result);
        assertNull(result.id);
        assertNull(result.cb);
        assertNull(result.webResource);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToGameAddToCachePayload(null).parse());
        assertNull(new ToGameAddToCachePayload("").parse());
        assertNull(new ToGameAddToCachePayload("{broken_json").parse());
        assertNull(new ToGameAddToCachePayload("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenDataTypesAreInvalid() throws JSONException {
        JSONObject json = new JSONObject();
        // Нестроковые типы и строковый "null" для optStringNullable
        json.put(ModelFields.GameAddToCachePayload.id, 12345);
        json.put(ModelFields.GameAddToCachePayload.cb, new JSONArray());
        // Строка вместо JSONObject для webResource
        json.put(ModelFields.GameAddToCachePayload.webResource, "not_a_json_object");

        GameAddToCachePayload result = new ToGameAddToCachePayload(json.toString()).parse();

        assertNotNull(result);
        assertNull(result.id);
        assertNull(result.cb);
        assertNull(result.webResource);

        // Дополнительная проверка JSONObject / boolean вместо id и числа вместо webResource
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.GameAddToCachePayload.id, new JSONObject());
        json2.put(ModelFields.GameAddToCachePayload.cb, " null ");
        json2.put(ModelFields.GameAddToCachePayload.webResource, 999);

        GameAddToCachePayload result2 = new ToGameAddToCachePayload(json2.toString()).parse();

        assertNotNull(result2);
        assertNull(result2.id);
        assertNull(result2.cb);
        assertNull(result2.webResource);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameAddToCachePayload parser = new ToGameAddToCachePayload("{}");

        assertEquals("GameAddToCachePayload", parser.getModelClassName());
    }
}
