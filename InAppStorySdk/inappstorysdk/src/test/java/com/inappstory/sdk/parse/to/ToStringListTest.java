package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToStringListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsStringList_whenConstructedWithValidJsonArray() throws JSONException {
        JSONArray array = new JSONArray();
        array.put("string1");
        array.put("string2");
        array.put("string3");

        List<String> result = new ToStringList(array).parse();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("string1", result.get(0));
        assertEquals("string2", result.get(1));
        assertEquals("string3", result.get(2));
    }

    @Test
    public void parse_returnsStringList_whenConstructedWithValidJsonString() throws JSONException {
        JSONArray array = new JSONArray();
        array.put("item1");
        array.put("item2");

        List<String> result = new ToStringList(array.toString()).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("item1", result.get(0));
        assertEquals("item2", result.get(1));
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<String> result = new ToStringList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonStringProvided() {
        List<String> result = new ToStringList("[]").parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonArray() {
        assertNull(new ToStringList((JSONArray) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToStringList((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToStringList("{invalid_json").parse());
        assertNull(new ToStringList("not_a_json_array").parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonStringElements() throws JSONException {
        JSONArray arrayWithObject = new JSONArray();
        arrayWithObject.put("valid_string");
        arrayWithObject.put(new JSONObject().put("key", "value"));

        JSONArray arrayWithNumber = new JSONArray();
        arrayWithNumber.put(123);

        // JSONArray.getString(i) выбросит JSONException, если элемент не является строкой,
        // что приведет к возврату null из-за блока try-catch в методе parseList.
        assertNull(new ToStringList(arrayWithObject).parse());
        assertNull(new ToStringList(arrayWithNumber).parse());
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStringList parser = new ToStringList(new JSONArray());
        assertEquals("List of String", parser.getModelClassName());
    }
}