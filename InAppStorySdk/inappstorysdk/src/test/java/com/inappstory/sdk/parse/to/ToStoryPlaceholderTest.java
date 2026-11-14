package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToStoryPlaceholderTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedPlaceholder_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryPlaceholder.name, "user_name");
        json.put(ModelFields.StoryPlaceholder.defaultVal, "John Doe");

        ToStoryPlaceholder parser = new ToStoryPlaceholder(json);
        StoryPlaceholder result = parser.parse();

        assertNotNull(result);
        assertEquals("user_name", result.name);
        assertEquals("John Doe", result.defaultVal);
    }

    @Test
    public void parse_returnsPlaceholderWithNullDefaultVal_whenOptionalKeyIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryPlaceholder.name, "user_name");

        ToStoryPlaceholder parser = new ToStoryPlaceholder(json);
        StoryPlaceholder result = parser.parse();

        assertNotNull(result);
        assertEquals("user_name", result.name);
        assertNull(result.defaultVal);
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredNameIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryPlaceholder.defaultVal, "John Doe");

        assertNull(new ToStoryPlaceholder(json).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToStoryPlaceholder(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToStoryPlaceholder parser = new ToStoryPlaceholder(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredNameHasInvalidDataType() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryPlaceholder.name, 12345); // Ожидается строка
        json.put(ModelFields.StoryPlaceholder.defaultVal, "valid");

        // getString для не-строки вызовет исключение, которое перехватывается в try-catch
        assertNull(new ToStoryPlaceholder(json).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypeInOptionalField_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StoryPlaceholder.name, "user_name");
        json.put(ModelFields.StoryPlaceholder.defaultVal, new JSONObject()); // Ожидается строка или null

        ToStoryPlaceholder parser = new ToStoryPlaceholder(json);
        StoryPlaceholder result = parser.parse();

        assertNotNull(result);
        assertEquals("user_name", result.name);
        assertNull(result.defaultVal); // optStringNullable корректно обработает невалидный тип
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStoryPlaceholder parser = new ToStoryPlaceholder(new JSONObject());
        assertEquals("StoryPlaceholder", parser.getModelClassName());
    }
}