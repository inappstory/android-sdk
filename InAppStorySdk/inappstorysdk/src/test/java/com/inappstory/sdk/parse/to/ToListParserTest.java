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

public class ToListParserTest {

    /**
     * Тестовая модель с одним строковым полем.
     */
    private static class DummyItem {
        final String name;

        DummyItem(String name) {
            this.name = name;
        }
    }

    /**
     * Тестовый парсер элемента списка.
     * Возвращает null, если поле "name" отсутствует или не является строкой.
     */
    private static class DummyItemParser extends ToModelParser<DummyItem> {
        DummyItemParser(JSONObject jsonValue) {
            super(jsonValue);
        }

        @Override
        protected DummyItem parseObject(JSONObject jsonObject) {
            try {
                String name = jsonObject.getString("name");
                return new DummyItem(name);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected String getModelClassName() {
            return DummyItem.class.getSimpleName();
        }
    }

    /**
     * Тестовый наследник ToListParser.
     */
    private static class TestDummyListParser extends ToListParser<DummyItem, DummyItemParser> {
        TestDummyListParser(String strValue) {
            super(strValue);
        }

        TestDummyListParser(JSONArray jsonArrayValue) {
            super(jsonArrayValue);
        }

        @Override
        protected DummyItemParser createParserInstance(JSONObject object) {
            return new DummyItemParser(object);
        }
    }

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedList_whenConstructedWithValidJsonArray() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject().put("name", "first"));
        array.put(new JSONObject().put("name", "second"));

        List<DummyItem> result = new TestDummyListParser(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("first", result.get(0).name);
        assertEquals("second", result.get(1).name);
    }

    @Test
    public void parse_returnsPopulatedList_whenConstructedWithValidJsonString() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject().put("name", "alpha"));
        array.put(new JSONObject().put("name", "beta"));

        List<DummyItem> result = new TestDummyListParser(array.toString()).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("alpha", result.get(0).name);
        assertEquals("beta", result.get(1).name);
    }

    @Test
    public void parse_returnsEmptyList_whenInputArrayIsEmpty() {
        List<DummyItem> fromArray = new TestDummyListParser(new JSONArray()).parse();
        List<DummyItem> fromString = new TestDummyListParser("[]").parse();

        assertNotNull(fromArray);
        assertTrue(fromArray.isEmpty());

        assertNotNull(fromString);
        assertTrue(fromString.isEmpty());
    }

    // --- Обработка null и невалидного JSON ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new TestDummyListParser((String) null).parse());
        assertNull(new TestDummyListParser((JSONArray) null).parse());
    }

    @Test
    public void parse_returnsNull_whenStringIsMalformedOrNotJsonArray() {
        assertNull(new TestDummyListParser("{invalid_json").parse());
        assertNull(new TestDummyListParser("").parse());
        // Валидный JSON, но объект вместо массива
        assertNull(new TestDummyListParser("{\"name\": \"not_an_array\"}").parse());
    }

    // --- Проверка невалидных типов элементов внутри массива ---

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithPrimitives = new JSONArray();
        arrayWithPrimitives.put(new JSONObject().put("name", "valid"));
        arrayWithPrimitives.put("invalid_string_element");

        JSONArray arrayWithNumber = new JSONArray();
        arrayWithNumber.put(12345);

        JSONArray arrayWithNullElement = new JSONArray();
        arrayWithNullElement.put(JSONObject.NULL);

        JSONArray arrayWithNestedArray = new JSONArray();
        arrayWithNestedArray.put(new JSONArray());

        assertNull(new TestDummyListParser(arrayWithPrimitives).parse());
        assertNull(new TestDummyListParser(arrayWithNumber).parse());
        assertNull(new TestDummyListParser(arrayWithNullElement).parse());
        assertNull(new TestDummyListParser(arrayWithNestedArray).parse());

        // То же самое при передаче через конструктор со String
        assertNull(new TestDummyListParser(arrayWithPrimitives.toString()).parse());
        assertNull(new TestDummyListParser(arrayWithNumber.toString()).parse());
    }

    // --- Проверка поведения, когда парсер элемента возвращает null ---

    @Test
    public void parseFromJsonArray_returnsNull_whenAnyItemFailsToParse() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject().put("name", "valid"));
        // Невалидный тип поля "name" (число вместо строки -> DummyItemParser вернёт null)
        array.put(new JSONObject().put("name", 999));

        List<DummyItem> result = new TestDummyListParser(array).parse();

        // Ветка parseJSONArray прерывает цикл и возвращает null при item == null
        assertNull(result);
    }

    @Test
    public void parseFromString_includesNullItem_whenAnyItemFailsToParse() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject().put("name", "valid"));
        // Невалидный тип поля "name" -> DummyItemParser вернёт null
        array.put(new JSONObject().put("name", 999));

        List<DummyItem> result = new TestDummyListParser(array.toString()).parse();

        // Ветка parseString не проверяет item == null и добавляет null в список
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("valid", result.get(0).name);
        assertNull(result.get(1));
    }

    // --- Проверка исключения внутри createParserInstance ---

    @Test
    public void parse_returnsNull_whenCreateParserInstanceThrowsException() throws JSONException {
        ToListParser<DummyItem, DummyItemParser> crashingParser =
                new ToListParser<DummyItem, DummyItemParser>(new JSONArray().put(new JSONObject())) {
                    @Override
                    protected DummyItemParser createParserInstance(JSONObject object) {
                        throw new RuntimeException("Unexpected error");
                    }
                };

        assertNull(crashingParser.parse());
    }

    // --- Тест форматирования ошибки ---

    @Test
    public void parseError_returnsFormattedMessage() {
        TestDummyListParser parser = new TestDummyListParser("[]");

        assertEquals("Field items has incorrect format.", parser.parseError("items"));
    }
}