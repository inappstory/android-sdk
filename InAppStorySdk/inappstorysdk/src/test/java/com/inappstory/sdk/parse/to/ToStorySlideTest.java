package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToStorySlideTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedSlide_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StorySlide.slideIndex, 1);
        json.put(ModelFields.StorySlide.duration, 5000);
        json.put(ModelFields.StorySlide.html, "<p>Test HTML</p>");
        json.put(ModelFields.StorySlide.isScreenshotShare, true);

        JSONObject payload = new JSONObject().put("eventType", "click");
        json.put(ModelFields.StorySlide.slidePayload, payload);

        JSONObject timeline = new JSONObject().put("isTimelineHidden", false);
        json.put(ModelFields.StorySlide.slideTimeline, timeline);

        JSONArray resources = new JSONArray().put(new JSONObject().put("url", "res1.png"));
        json.put(ModelFields.StorySlide.resources, resources);

        JSONArray placeholders = new JSONArray().put(new JSONObject().put("url", "ph1.png"));
        json.put(ModelFields.StorySlide.placeholders, placeholders);

        ToStorySlide parser = new ToStorySlide(json);
        StorySlide result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.slideIndex);
        assertEquals(5000, result.duration);
        assertEquals("<p>Test HTML</p>", result.html);
        assertTrue(result.isScreenshotShare);
        assertNotNull(result.slidePayload);
        assertNotNull(result.slideTimeline);
        assertNotNull(result.resources);
        assertNotNull(result.placeholders);
    }

    @Test
    public void parse_returnsSlideWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToStorySlide parser = new ToStorySlide(json);
        StorySlide result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.duration);
        assertEquals("", result.html); // optString возвращает пустую строку по умолчанию
        assertFalse(result.isScreenshotShare);
        assertNull(result.slidePayload);
        assertNull(result.slideTimeline);
        assertNull(result.resources);
        assertNull(result.placeholders);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToStorySlide(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToStorySlide parser = new ToStorySlide(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StorySlide.slideIndex, "not_an_int");
        json.put(ModelFields.StorySlide.duration, new JSONObject());

        // optString преобразует число в его строковое представление ("123")
        json.put(ModelFields.StorySlide.html, 123);

        json.put(ModelFields.StorySlide.isScreenshotShare, "not_a_boolean");
        json.put(ModelFields.StorySlide.slidePayload, "not_an_object");
        json.put(ModelFields.StorySlide.slideTimeline, 123);
        json.put(ModelFields.StorySlide.resources, "not_an_array");
        json.put(ModelFields.StorySlide.placeholders, new JSONObject());

        ToStorySlide parser = new ToStorySlide(json);
        StorySlide result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.duration);
        // optString возвращает строковое представление числа, а не пустую строку
        assertEquals("123", result.html);
        assertFalse(result.isScreenshotShare);
        assertNull(result.slidePayload);
        assertNull(result.slideTimeline);
        assertNull(result.resources);
        assertNull(result.placeholders);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStorySlide parser = new ToStorySlide(new JSONObject());
        assertEquals("StorySlide", parser.getModelClassName());
    }
}