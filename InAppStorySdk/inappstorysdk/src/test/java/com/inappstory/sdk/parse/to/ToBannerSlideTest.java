package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.BannerSlide;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToBannerSlideTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedBannerSlide_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.BannerSlide.slideIndex, 2);
        json.put(ModelFields.BannerSlide.duration, 5000);
        json.put(ModelFields.BannerSlide.html, "<html><body>Banner</body></html>");
        json.put(ModelFields.BannerSlide.slidePayload, new JSONObject());
        json.put(ModelFields.BannerSlide.resources, new JSONArray());
        json.put(ModelFields.BannerSlide.placeholders, new JSONArray());

        BannerSlide result = new ToBannerSlide(json).parse();

        assertNotNull(result);
        assertEquals(2, result.slideIndex);
        assertEquals(5000, result.duration);
        assertEquals("<html><body>Banner</body></html>", result.html);
        assertNotNull(result.slidePayload);
        assertNotNull(result.resources);
        assertTrue(result.resources.isEmpty());
        assertNotNull(result.placeholders);
        assertTrue(result.placeholders.isEmpty());
    }

    @Test
    public void parse_returnsSlideWithDefaultValues_whenJsonIsEmpty() {
        BannerSlide result = new ToBannerSlide(new JSONObject()).parse();

        assertNotNull(result);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.duration);
        assertEquals("", result.html);
        assertNull(result.slidePayload);
        assertNull(result.resources);
        assertNull(result.placeholders);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToBannerSlide(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToBannerSlide parser = new ToBannerSlide(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Строка вместо int для числовых полей
        json.put(ModelFields.BannerSlide.slideIndex, "not_an_int");
        json.put(ModelFields.BannerSlide.duration, "invalid_duration");
        // Число и массив вместо JSONObject / JSONArray для вложенных структур
        json.put(ModelFields.BannerSlide.slidePayload, "not_a_json_object");
        json.put(ModelFields.BannerSlide.resources, 12345);
        json.put(ModelFields.BannerSlide.placeholders, new JSONObject().put("k", "v"));

        BannerSlide result = new ToBannerSlide(json).parse();

        assertNotNull(result);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.duration);
        assertNull(result.slidePayload);
        assertNull(result.resources);
        assertNull(result.placeholders);
    }

    @Test
    public void parse_handlesNonStringTypeForHtmlField() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.BannerSlide.html, 404);

        BannerSlide result = new ToBannerSlide(json).parse();

        // В org.json метод optString(key) приводит примитивы к строке ("404")
        assertNotNull(result);
        assertEquals("404", result.html);
    }

    @Test
    public void parse_setsResourcesAndPlaceholdersToNull_whenArraysContainInvalidElements() throws JSONException {
        JSONArray invalidResources = new JSONArray().put("not_a_resource_object");
        JSONArray invalidPlaceholders = new JSONArray().put(999);

        JSONObject json = new JSONObject();
        json.put(ModelFields.BannerSlide.slideIndex, 1);
        json.put(ModelFields.BannerSlide.resources, invalidResources);
        json.put(ModelFields.BannerSlide.placeholders, invalidPlaceholders);

        BannerSlide result = new ToBannerSlide(json).parse();

        assertNotNull(result);
        assertEquals(1, result.slideIndex);
        assertNull(result.resources);
        assertNull(result.placeholders);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToBannerSlide parser = new ToBannerSlide(new JSONObject());

        assertEquals("BannerSlide", parser.getModelClassName());
    }
}
