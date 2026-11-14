package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToStoryListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsStoryList_whenConstructedWithValidJsonArray() throws JSONException {
        JSONObject story1 = new JSONObject();
        story1.put(ModelFields.Story.id, 101);
        story1.put(ModelFields.Story.title, "First Story");

        JSONObject story2 = new JSONObject();
        story2.put(ModelFields.Story.id, 102);
        story2.put(ModelFields.Story.title, "Second Story");

        JSONArray array = new JSONArray();
        array.put(story1);
        array.put(story2);

        List<Story> result = new ToStoryList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101, result.get(0).id);
        assertEquals("First Story", result.get(0).title);
        assertEquals(102, result.get(1).id);
        assertEquals("Second Story", result.get(1).title);
    }

    @Test
    public void parse_returnsStoryList_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject story1 = new JSONObject();
        story1.put(ModelFields.Story.id, 201);

        JSONArray array = new JSONArray();
        array.put(story1);

        List<Story> result = new ToStoryList(array.toString()).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(201, result.get(0).id);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<Story> result = new ToStoryList(new JSONArray()).parse();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonStringProvided() {
        List<Story> result = new ToStoryList("[]").parse();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToStoryList((JSONArray) null).parse());
        assertNull(new ToStoryList((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToStoryList("{invalid_json").parse());
        assertNull(new ToStoryList("not_an_array").parse());
    }

    @Test
    public void parse_returnsNull_whenStringIsNotAJsonArray() {
        // Передаем объект вместо массива
        assertNull(new ToStoryList("{}").parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.Story.id, 101));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        // getJSONObject(i) выбросит JSONException, если элемент не является объектом,
        // что приведет к возврату null из-за блока try-catch.
        assertNull(new ToStoryList(arrayWithString).parse());
        assertNull(new ToStoryList(arrayWithNumber).parse());
        assertNull(new ToStoryList(arrayWithNestedArray).parse());
        assertNull(new ToStoryList(arrayWithJsonNull).parse());
    }

    @Test
    public void parse_addsNullToList_whenStoryIsInvalidButIsJsonObject() throws JSONException {
        JSONObject validStory = new JSONObject().put(ModelFields.Story.id, 101);
        JSONObject invalidStoryMissingId = new JSONObject().put(ModelFields.Story.title, "No ID");

        JSONArray array = new JSONArray();
        array.put(validStory);
        array.put(invalidStoryMissingId);

        List<Story> result = new ToStoryList(array).parse();

        // ToStory вернет null для второго элемента, и этот null будет добавлен в список
        // (поскольку это ручная итерация, а не строгий ToListParser)
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertEquals(101, result.get(0).id);
        assertNull(result.get(1));
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStoryList parser = new ToStoryList(new JSONArray());
        assertEquals("List of Story", parser.getModelClassName());
    }
}