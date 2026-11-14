package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;

public class ModelToStringParserTest {

    private static class DummyModel {
        final String data;

        DummyModel(String data) {
            this.data = data;
        }
    }

    /**
     * Успешная реализация, возвращающая валидный JSONObject.
     */
    private static class SuccessParser extends ModelToStringParser<DummyModel> {
        SuccessParser(DummyModel model) {
            super(model);
        }

        @Override
        protected JSONObject parseToJSONObject() throws Exception {
            JSONObject json = new JSONObject();
            json.put("data", this.model.data);
            return json;
        }
    }

    /**
     * Реализация, которая всегда выбрасывает исключение.
     */
    private static class FailingParser extends ModelToStringParser<DummyModel> {
        FailingParser(DummyModel model) {
            super(model);
        }

        @Override
        protected JSONObject parseToJSONObject() throws Exception {
            throw new Exception("Simulated parsing error");
        }
    }

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsValidJsonString_whenParseToJSONObjectSucceeds() {
        DummyModel model = new DummyModel("success_data");
        SuccessParser parser = new SuccessParser(model);

        String result = parser.parse();

        assertNotNull(result);
        assertEquals("{\"data\":\"success_data\"}", result);
    }

    // --- Обработка исключений базовым классом ---

    @Test
    public void parse_returnsNull_whenParseToJSONObjectThrowsException() {
        DummyModel model = new DummyModel("fail_data");
        FailingParser parser = new FailingParser(model);

        // Метод parse() в базовом классе должен перехватить Exception и вернуть null
        String result = parser.parse();

        assertNull(result);
    }
}