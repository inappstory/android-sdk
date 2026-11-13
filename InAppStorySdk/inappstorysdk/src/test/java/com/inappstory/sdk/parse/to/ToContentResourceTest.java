package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.ContentResource;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToContentResourceTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedContentResource_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ContentResource.url, "https://example.com");
        json.put(ModelFields.ContentResource.key, "res_key_1");
        json.put(ModelFields.ContentResource.type, "video");
        json.put(ModelFields.ContentResource.purpose, "slide_bg");
        json.put(ModelFields.ContentResource.filename, "video.mp4");
        json.put(ModelFields.ContentResource.index, 0);
        json.put(ModelFields.ContentResource.rangeStart, 1024L);
        json.put(ModelFields.ContentResource.rangeEnd, 4096L);

        ContentResource result = new ToContentResource(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals("res_key_1", result.key);
        assertEquals("video", result.type);
        assertEquals("slide_bg", result.purpose);
        assertEquals("video.mp4", result.filename);
        assertEquals(Integer.valueOf(0), result.index);
        assertEquals(Long.valueOf(1024L), result.rangeStart);
        assertEquals(Long.valueOf(4096L), result.rangeEnd);
    }

    @Test
    public void parse_returnsDefaultAndNullOptionalFields_whenOnlyRequiredUrlProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ContentResource.url, "https://example.com");

        ContentResource result = new ToContentResource(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals("", result.key);
        assertEquals("", result.type);
        assertEquals("", result.purpose);
        assertEquals("", result.filename);
        assertNull(result.index);
        assertNull(result.rangeStart);
        assertNull(result.rangeEnd);
    }

    // --- Обработка null и отсутствия обязательного url ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToContentResource(null).parse());
        assertNull(new ToContentResource(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() {
        assertNull(new ToContentResource(new JSONObject()).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenRequiredUrlHasInvalidDataType() throws JSONException {
        JSONObject intUrl = new JSONObject().put(ModelFields.ContentResource.url, 12345);
        JSONObject objectUrl = new JSONObject().put(ModelFields.ContentResource.url, new JSONObject());
        JSONObject arrayUrl = new JSONObject().put(ModelFields.ContentResource.url, new JSONArray());
        JSONObject nullUrl = new JSONObject().put(ModelFields.ContentResource.url, JSONObject.NULL);

        assertNull(new ToContentResource(intUrl).parse());
        assertNull(new ToContentResource(objectUrl).parse());
        assertNull(new ToContentResource(arrayUrl).parse());
        assertNull(new ToContentResource(nullUrl).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ContentResource.url, "https://example.com");

        // Примитивы для optString (org.json преобразует их в строки)
        json.put(ModelFields.ContentResource.key, 100);
        json.put(ModelFields.ContentResource.type, true);

        // Невалидные типы для optIntNullable и optLongNullable -> должны стать null
        json.put(ModelFields.ContentResource.index, "not_an_int");
        json.put(ModelFields.ContentResource.rangeStart, "not_a_long");
        json.put(ModelFields.ContentResource.rangeEnd, new JSONObject());

        ContentResource result = new ToContentResource(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals("100", result.key);
        assertEquals("true", result.type);
        assertNull(result.index);
        assertNull(result.rangeStart);
        assertNull(result.rangeEnd);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToContentResource parser = new ToContentResource(new JSONObject());

        assertEquals("ContentResource", parser.getModelClassName());
    }
}