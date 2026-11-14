package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.api.models.StoryLink;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSlideLinkObjectTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedLinkObject_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SlideLinkObject.elementId, "btn_submit");
        json.put(ModelFields.SlideLinkObject.type, "external");
        
        JSONObject linkJson = new JSONObject();
        linkJson.put("target", "https://example.com");
        linkJson.put("type", "web");
        json.put(ModelFields.SlideLinkObject.link, linkJson);

        ToSlideLinkObject parser = new ToSlideLinkObject(json.toString());
        SlideLinkObject result = parser.parse();

        assertNotNull(result);
        assertEquals("btn_submit", result.elementId);
        assertEquals("external", result.type);
        assertNotNull(result.link);
        assertEquals("https://example.com", result.link.target);
        assertEquals("web", result.link.type);
    }

    @Test
    public void parse_returnsLinkObjectWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToSlideLinkObject parser = new ToSlideLinkObject(json.toString());
        SlideLinkObject result = parser.parse();

        assertNotNull(result);
        assertNull(result.elementId);
        assertNull(result.type);
        assertNull(result.link);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToSlideLinkObject parser = new ToSlideLinkObject(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToSlideLinkObject parser = new ToSlideLinkObject("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToSlideLinkObject parser = new ToSlideLinkObject("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных для строковых полей
        json.put(ModelFields.SlideLinkObject.elementId, 12345);
        json.put(ModelFields.SlideLinkObject.type, new JSONObject());
        // Передаем невалидный тип для вложенного объекта (строку вместо объекта)
        json.put(ModelFields.SlideLinkObject.link, "not_an_object");

        ToSlideLinkObject parser = new ToSlideLinkObject(json.toString());
        SlideLinkObject result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertNull(result.elementId);
        assertNull(result.type);
        assertNull(result.link); // ToStoryLink корректно обработает null или невалидный тип
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSlideLinkObject parser = new ToSlideLinkObject("{}");
        assertEquals("SlideLinkObject", parser.getModelClassName());
    }
}