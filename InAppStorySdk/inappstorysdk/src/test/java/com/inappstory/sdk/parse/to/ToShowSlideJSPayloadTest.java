package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.inappmessage.domain.reader.ShowSlideJSPayload;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToShowSlideJSPayloadTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedPayload_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ShowSlideJSPayload.index, 7);
        json.put(ModelFields.ShowSlideJSPayload.cardId, 2048);

        ToShowSlideJSPayload parser = new ToShowSlideJSPayload(json.toString());
        ShowSlideJSPayload result = parser.parse();

        assertNotNull(result);
        assertEquals(7, result.index);
        assertEquals(2048, result.cardId);
    }

    @Test
    public void parse_returnsPayloadWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToShowSlideJSPayload parser = new ToShowSlideJSPayload(json.toString());
        ShowSlideJSPayload result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.index); // optInt по умолчанию возвращает 0
        assertEquals(0, result.cardId);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToShowSlideJSPayload parser = new ToShowSlideJSPayload(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToShowSlideJSPayload parser = new ToShowSlideJSPayload("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToShowSlideJSPayload parser = new ToShowSlideJSPayload("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put(ModelFields.ShowSlideJSPayload.index, "not_an_int");
        json.put(ModelFields.ShowSlideJSPayload.cardId, new JSONObject());

        ToShowSlideJSPayload parser = new ToShowSlideJSPayload(json.toString());
        ShowSlideJSPayload result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertEquals(0, result.index);
        assertEquals(0, result.cardId);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToShowSlideJSPayload parser = new ToShowSlideJSPayload("{}");
        assertEquals("ShowSlideJSPayload", parser.getModelClassName());
    }
}