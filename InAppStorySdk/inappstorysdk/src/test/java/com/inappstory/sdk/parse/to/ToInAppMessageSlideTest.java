package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageSlide;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToInAppMessageSlideTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedSlide_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageSlide.slideIndex, 2);
        json.put(ModelFields.InAppMessageSlide.duration, 5000);
        json.put(ModelFields.InAppMessageSlide.html, "<p>Test HTML</p>");
        json.put(ModelFields.InAppMessageSlide.slidePayload, new JSONObject().put("eventType", "click"));
        json.put(ModelFields.InAppMessageSlide.resources, new JSONArray());
        json.put(ModelFields.InAppMessageSlide.placeholders, new JSONArray());

        ToInAppMessageSlide parser = new ToInAppMessageSlide(json);
        InAppMessageSlide result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.slideIndex);
        assertEquals(5000, result.duration);
        assertEquals("<p>Test HTML</p>", result.html);
        assertNotNull(result.slidePayload);
        assertNotNull(result.resources);
        assertNotNull(result.placeholders);
    }

    @Test
    public void parse_returnsSlideWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToInAppMessageSlide parser = new ToInAppMessageSlide(json);
        InAppMessageSlide result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.duration);
        assertEquals("", result.html); // optString возвращает пустую строку по умолчанию
        assertNull(result.slidePayload);
        assertNull(result.resources);
        assertNull(result.placeholders);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToInAppMessageSlide(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToInAppMessageSlide parser = new ToInAppMessageSlide(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageSlide.slideIndex, "not_an_int");
        json.put(ModelFields.InAppMessageSlide.duration, new JSONObject());
        
        // optString преобразует число в его строковое представление
        json.put(ModelFields.InAppMessageSlide.html, 123);
        
        json.put(ModelFields.InAppMessageSlide.slidePayload, "not_an_object");
        json.put(ModelFields.InAppMessageSlide.resources, "not_an_array");
        json.put(ModelFields.InAppMessageSlide.placeholders, 999);

        ToInAppMessageSlide parser = new ToInAppMessageSlide(json);
        InAppMessageSlide result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.duration);
        assertEquals("123", result.html);
        assertNull(result.slidePayload);
        assertNull(result.resources);
        assertNull(result.placeholders);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInAppMessageSlide parser = new ToInAppMessageSlide(new JSONObject());
        assertEquals("InAppMessageSlide", parser.getModelClassName());
    }
}