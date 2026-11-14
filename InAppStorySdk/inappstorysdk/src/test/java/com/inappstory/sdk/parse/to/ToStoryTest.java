package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToStoryTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedStory_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Story.id, 1001);
        json.put(ModelFields.Story.title, "Test Story");
        json.put(ModelFields.Story.titleColor, "#FF0000");
        json.put(ModelFields.Story.isFullscreen, true);
        json.put(ModelFields.Story.slidesCount, 5);
        json.put(ModelFields.Story.favorite, true);
        json.put(ModelFields.Story.timelineIsHidden, false);
        
        json.put(ModelFields.Story.image, new JSONArray());
        json.put(ModelFields.Story.slides, new JSONArray());
        json.put(ModelFields.Story.customAttributes, new JSONObject().put("key", "value"));

        ToStory parser = new ToStory(json);
        Story result = parser.parse();

        assertNotNull(result);
        assertEquals(1001, result.id);
        assertEquals("Test Story", result.title);
        assertEquals("#FF0000", result.titleColor);
        assertTrue(result.isFullscreen);
        assertEquals(5, result.slidesCount);
        assertTrue(result.favorite);
        assertFalse(result.timelineIsHidden);
        assertNotNull(result.image);
        assertNotNull(result.slides);
        assertNotNull(result.customAttributes);
        assertEquals("value", result.customAttributes.get("key"));
    }

    @Test
    public void parse_returnsPopulatedStory_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Story.id, 2002);
        json.put(ModelFields.Story.title, "String Story");

        ToStory parser = new ToStory(json.toString());
        Story result = parser.parse();

        assertNotNull(result);
        assertEquals(2002, result.id);
        assertEquals("String Story", result.title);
    }

    @Test
    public void parse_returnsStoryWithDefaultValues_whenOnlyRequiredIdProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Story.id, 3003);

        ToStory parser = new ToStory(json);
        Story result = parser.parse();

        assertNotNull(result);
        assertEquals(3003, result.id);
        assertNull(result.title);
        assertEquals(0, result.slidesCount);
        assertFalse(result.isFullscreen);
        assertFalse(result.favorite);
        assertFalse(result.timelineIsHidden);
        assertNull(result.image);
        assertNull(result.slides);
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() throws JSONException {
        JSONObject jsonWithoutId = new JSONObject();
        jsonWithoutId.put(ModelFields.Story.title, "No ID Story");

        assertNull(new ToStory(jsonWithoutId).parse());
        assertNull(new ToStory("{}").parse());
    }

    @Test
    public void parse_returnsNull_whenIdHasInvalidDataType() throws JSONException {
        JSONObject stringId = new JSONObject();
        stringId.put(ModelFields.Story.id, "not_a_number");

        assertNull(new ToStory(stringId).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToStory((JSONObject) null).parse());
        assertNull(new ToStory((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToStory("{invalid_json").parse());
        assertNull(new ToStory("").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Story.id, 4004);
        
        // Передаем невалидные типы для опциональных полей
        json.put(ModelFields.Story.title, 123);
        json.put(ModelFields.Story.isFullscreen, "not_a_boolean");
        json.put(ModelFields.Story.slidesCount, "not_an_int");
        json.put(ModelFields.Story.image, "not_an_array");
        json.put(ModelFields.Story.customAttributes, "not_an_object");

        ToStory parser = new ToStory(json);
        Story result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertEquals(4004, result.id);
        assertNull(result.title);
        assertFalse(result.isFullscreen);
        assertEquals(0, result.slidesCount);
        assertNull(result.image);
        assertNull(result.customAttributes);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStory parser = new ToStory(new JSONObject());
        assertEquals("Story", parser.getModelClassName());
    }
}