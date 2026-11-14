package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.StoryPlaceholder;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToStoryPlaceholderListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPlaceholderList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject placeholder1 = new JSONObject();
        placeholder1.put(ModelFields.StoryPlaceholder.name, "name_1");
        placeholder1.put(ModelFields.StoryPlaceholder.defaultVal, "default_1");

        JSONObject placeholder2 = new JSONObject();
        placeholder2.put(ModelFields.StoryPlaceholder.name, "name_2");
        // defaultVal отсутствует, должен быть null

        JSONArray array = new JSONArray();
        array.put(placeholder1);
        array.put(placeholder2);

        List<StoryPlaceholder> result = new ToStoryPlaceholderList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("name_1", result.get(0).name);
        assertEquals("default_1", result.get(0).defaultVal);

        assertEquals("name_2", result.get(1).name);
        assertNull(result.get(1).defaultVal);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<StoryPlaceholder> result = new ToStoryPlaceholderList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToStoryPlaceholderList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.StoryPlaceholder.name, "valid"));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToStoryPlaceholderList(arrayWithString).parse());
        assertNull(new ToStoryPlaceholderList(arrayWithNumber).parse());
        assertNull(new ToStoryPlaceholderList(arrayWithNestedArray).parse());
        assertNull(new ToStoryPlaceholderList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных данных внутри элементов StoryPlaceholder ---

    @Test
    public void parse_returnsNull_whenAnyPlaceholderMissingRequiredName() throws JSONException {
        JSONObject validPlaceholder = new JSONObject().put(ModelFields.StoryPlaceholder.name, "valid_name");
        JSONObject invalidPlaceholder = new JSONObject().put(ModelFields.StoryPlaceholder.defaultVal, "some_value");

        JSONArray array = new JSONArray();
        array.put(validPlaceholder);
        array.put(invalidPlaceholder);

        // ToStoryPlaceholder вернёт null для второго элемента -> ToListParser вернёт null для всего списка
        assertNull(new ToStoryPlaceholderList(array).parse());
    }

    @Test
    public void parse_ignoresInvalidTypesInOptionalPlaceholderFields() throws JSONException {
        JSONObject placeholderWithBadOptionalField = new JSONObject();
        placeholderWithBadOptionalField.put(ModelFields.StoryPlaceholder.name, "valid_name");
        placeholderWithBadOptionalField.put(ModelFields.StoryPlaceholder.defaultVal, 12345); // Не строка

        JSONArray array = new JSONArray().put(placeholderWithBadOptionalField);

        List<StoryPlaceholder> result = new ToStoryPlaceholderList(array).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("valid_name", result.get(0).name);
        assertNull(result.get(0).defaultVal); // Невалидный тип опционального поля игнорируется
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToStoryPlaceholderParser() throws JSONException {
        ToStoryPlaceholderList listParser = new ToStoryPlaceholderList(new JSONArray());
        JSONObject placeholderJson = new JSONObject()
                .put(ModelFields.StoryPlaceholder.name, "test_name")
                .put(ModelFields.StoryPlaceholder.defaultVal, "test_default");

        ToStoryPlaceholder itemParser = listParser.createParserInstance(placeholderJson);

        assertNotNull(itemParser);
        StoryPlaceholder parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals("test_name", parsed.name);
        assertEquals("test_default", parsed.defaultVal);
    }
}