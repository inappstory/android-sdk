package com.inappstory.sdk.parse.to;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToModelParserTest {

    /**
     * Тестовая модель с одним строковым полем.
     */
    private static class DummyModel {
        final String data;

        DummyModel(String data) {
            this.data = data;
        }
    }

    /**
     * Тестовая реализация парсера для проверки маршрутизации вызовов из final-метода parse().
     */
    private static class TestModelParser extends ToModelParser<DummyModel> {
        boolean parseStringCalled = false;
        boolean parseObjectCalled = false;
        boolean parseListCalled = false;

        TestModelParser(String strValue) {
            super(strValue);
        }

        TestModelParser(JSONObject jsonValue) {
            super(jsonValue);
        }

        TestModelParser(JSONArray jsonArrayValue) {
            super(jsonArrayValue);
        }

        @Override
        protected DummyModel parseString(@NonNull String value) {
            parseStringCalled = true;
            return new DummyModel("from_string:" + value);
        }

        @Override
        protected DummyModel parseObject(JSONObject jsonObject) {
            parseObjectCalled = true;
            return new DummyModel("from_object:" + jsonObject.optString("key"));
        }

        @Override
        protected DummyModel parseList(JSONArray jsonArray) {
            parseListCalled = true;
            return new DummyModel("from_list:" + jsonArray.length());
        }

        @Override
        protected String getModelClassName() {
            return DummyModel.class.getSimpleName();
        }
    }

    /**
     * Наследник без переопределения parseString / parseObject / parseList
     * для проверки базового поведения (возврат null по умолчанию).
     */
    private static class DefaultStubParser extends ToModelParser<DummyModel> {
        DefaultStubParser(String strValue) {
            super(strValue);
        }

        DefaultStubParser(JSONObject jsonValue) {
            super(jsonValue);
        }

        DefaultStubParser(JSONArray jsonArrayValue) {
            super(jsonArrayValue);
        }

        @Override
        protected String getModelClassName() {
            return "StubDummyModel";
        }
    }

    // --- Тесты маршрутизации метода parse() ---

    @Test
    public void parse_callsParseString_whenConstructedWithString() {
        TestModelParser parser = new TestModelParser("sample");

        DummyModel result = parser.parse();

        assertNotNull(result);
        assertEquals("from_string:sample", result.data);
        assertTrue(parser.parseStringCalled);
        assertFalse(parser.parseObjectCalled);
        assertFalse(parser.parseListCalled);
    }

    @Test
    public void parse_callsParseObject_whenConstructedWithJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject("{\"key\": \"val\"}");
        TestModelParser parser = new TestModelParser(jsonObject);

        DummyModel result = parser.parse();

        assertNotNull(result);
        assertEquals("from_object:val", result.data);
        assertTrue(parser.parseObjectCalled);
        assertFalse(parser.parseStringCalled);
        assertFalse(parser.parseListCalled);
    }

    @Test
    public void parse_callsParseList_whenConstructedWithJsonArray() {
        JSONArray jsonArray = new JSONArray().put("a").put("b").put("c");
        TestModelParser parser = new TestModelParser(jsonArray);

        DummyModel result = parser.parse();

        assertNotNull(result);
        assertEquals("from_list:3", result.data);
        assertTrue(parser.parseListCalled);
        assertFalse(parser.parseStringCalled);
        assertFalse(parser.parseObjectCalled);
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullValues() {
        TestModelParser nullStringParser = new TestModelParser((String) null);
        TestModelParser nullObjectParser = new TestModelParser((JSONObject) null);
        TestModelParser nullArrayParser = new TestModelParser((JSONArray) null);

        assertNull(nullStringParser.parse());
        assertNull(nullObjectParser.parse());
        assertNull(nullArrayParser.parse());

        assertFalse(nullStringParser.parseStringCalled);
        assertFalse(nullObjectParser.parseObjectCalled);
        assertFalse(nullArrayParser.parseListCalled);
    }

    // --- Тесты базовых реализаций protected-методов ---

    @Test
    public void defaultProtectedMethods_returnNull_whenNotOverridden() throws JSONException {
        assertNull(new DefaultStubParser("text").parse());
        assertNull(new DefaultStubParser(new JSONObject("{\"a\": 1}")).parse());
        assertNull(new DefaultStubParser(new JSONArray().put(1)).parse());
    }

    // --- Тесты формирования сообщений об ошибках ---

    @Test
    public void parseErrorWithField_returnsFormattedMessage() {
        TestModelParser parser = new TestModelParser("test");

        assertEquals("Field title has incorrect format.", parser.parseError("title"));
    }

    @Test
    public void parseErrorNoArgs_returnsFormattedMessageWithModelClassName() {
        TestModelParser parser = new TestModelParser("test");

        assertEquals("Can't parse model: DummyModel", parser.parseError());
    }
}