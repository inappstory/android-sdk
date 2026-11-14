package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.SlidePayload;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSlidePayloadTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedPayload_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SlidePayload.eventType, "click");
        json.put(ModelFields.SlidePayload.slideIndex, 3);
        json.put(ModelFields.SlidePayload.payload, "{\"action\": \"next\"}");

        ToSlidePayload parser = new ToSlidePayload(json);
        SlidePayload result = parser.parse();

        assertNotNull(result);
        assertEquals("click", result.eventType);
        assertEquals(3, result.slideIndex);
        assertEquals("{\"action\": \"next\"}", result.payload);
    }

    @Test
    public void parse_returnsPayloadWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToSlidePayload parser = new ToSlidePayload(json);
        SlidePayload result = parser.parse();

        assertNotNull(result);
        assertNull(result.eventType);
        assertEquals(0, result.slideIndex); // optInt по умолчанию возвращает 0
        assertNull(result.payload);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToSlidePayload(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToSlidePayload parser = new ToSlidePayload(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put(ModelFields.SlidePayload.eventType, 123);
        json.put(ModelFields.SlidePayload.slideIndex, "not_an_int");
        json.put(ModelFields.SlidePayload.payload, new JSONObject());

        ToSlidePayload parser = new ToSlidePayload(json);
        SlidePayload result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertNull(result.eventType);
        assertEquals(0, result.slideIndex);
        assertNull(result.payload);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSlidePayload parser = new ToSlidePayload(new JSONObject());
        assertEquals("SlidePayload", parser.getModelClassName());
    }
}