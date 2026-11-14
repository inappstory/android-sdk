package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.StoryLink;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToStoryLinkTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedLink_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryLink.target, "https://example.com");
        json.put(ModelFields.StoryLink.type, "external");

        ToStoryLink parser = new ToStoryLink(json);
        StoryLink result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.target);
        assertEquals("external", result.type);
    }

    @Test
    public void parse_returnsLinkWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToStoryLink parser = new ToStoryLink(json);
        StoryLink result = parser.parse();

        assertNotNull(result);
        assertNull(result.target);
        assertNull(result.type);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToStoryLink(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToStoryLink parser = new ToStoryLink(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put(ModelFields.StoryLink.target, 12345);
        json.put(ModelFields.StoryLink.type, new JSONObject());

        ToStoryLink parser = new ToStoryLink(json);
        StoryLink result = parser.parse();

        // optStringNullable корректно обработает невалидные типы и вернет null
        assertNotNull(result);
        assertNull(result.target);
        assertNull(result.type);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStoryLink parser = new ToStoryLink(new JSONObject());
        assertEquals("StoryLink", parser.getModelClassName());
    }
}