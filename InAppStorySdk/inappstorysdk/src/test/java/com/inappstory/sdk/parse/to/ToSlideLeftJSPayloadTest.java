package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.inappmessage.domain.reader.SlideLeftJSPayload;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSlideLeftJSPayloadTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedPayload_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SlideLeftJSPayload.index, 5);
        json.put(ModelFields.SlideLeftJSPayload.cardId, 1024);

        ToSlideLeftJSPayload parser = new ToSlideLeftJSPayload(json.toString());
        SlideLeftJSPayload result = parser.parse();

        assertNotNull(result);
        assertEquals(5, result.index);
        assertEquals(1024, result.cardId);
    }

    @Test
    public void parse_returnsPayloadWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToSlideLeftJSPayload parser = new ToSlideLeftJSPayload(json.toString());
        SlideLeftJSPayload result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.index); // optInt по умолчанию возвращает 0
        assertEquals(0, result.cardId);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToSlideLeftJSPayload parser = new ToSlideLeftJSPayload(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToSlideLeftJSPayload parser = new ToSlideLeftJSPayload("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToSlideLeftJSPayload parser = new ToSlideLeftJSPayload("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put(ModelFields.SlideLeftJSPayload.index, "not_an_int");
        json.put(ModelFields.SlideLeftJSPayload.cardId, new JSONObject());

        ToSlideLeftJSPayload parser = new ToSlideLeftJSPayload(json.toString());
        SlideLeftJSPayload result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertEquals(0, result.index);
        assertEquals(0, result.cardId);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSlideLeftJSPayload parser = new ToSlideLeftJSPayload("{}");
        assertEquals("SlideLeftJSPayload", parser.getModelClassName());
    }
}