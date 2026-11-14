package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.UrlObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToUrlObjectTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedUrlObject_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.UrlObject.url, "https://example.com/some-path");

        ToUrlObject parser = new ToUrlObject(json.toString());
        UrlObject result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/some-path", result.url);
    }

    @Test
    public void parse_returnsUrlObjectWithNullUrl_whenKeyIsMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToUrlObject parser = new ToUrlObject(json.toString());
        UrlObject result = parser.parse();

        assertNotNull(result);
        assertNull(result.url);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToUrlObject parser = new ToUrlObject(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToUrlObject parser = new ToUrlObject("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToUrlObject parser = new ToUrlObject("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypeForUrl_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидный тип данных (число вместо строки)
        json.put(ModelFields.UrlObject.url, 12345);

        ToUrlObject parser = new ToUrlObject(json.toString());
        UrlObject result = parser.parse();

        // optStringNullable должен корректно обработать невалидный тип и вернуть null
        assertNotNull(result);
        assertNull(result.url);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToUrlObject parser = new ToUrlObject("{}");
        assertEquals("UrlObject", parser.getModelClassName());
    }
}