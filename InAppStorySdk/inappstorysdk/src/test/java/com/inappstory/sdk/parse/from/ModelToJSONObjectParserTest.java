package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Test;

public class ModelToJSONObjectParserTest {

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
     * Тестовая реализация парсера для проверки сохранения модели и работы абстрактного метода.
     */
    private static class TestParser extends ModelToJSONObjectParser<DummyModel> {
        TestParser(DummyModel model) {
            super(model);
        }

        @Override
        protected JSONObject parseToJSONObject() throws Exception {
            if (this.model == null) {
                throw new Exception("Model is null");
            }
            JSONObject json = new JSONObject();
            json.put("data", this.model.data);
            return json;
        }
    }

    // --- Успешный парсинг ---

    @Test
    public void parseToJSONObject_returnsValidJsonObject_whenModelIsValid() throws Exception {
        DummyModel model = new DummyModel("test_data");
        TestParser parser = new TestParser(model);

        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals("test_data", result.getString("data"));
    }

    // --- Обработка исключений ---

    @Test
    public void parseToJSONObject_throwsException_whenModelIsNull() {
        TestParser parser = new TestParser(null);

        try {
            parser.parseToJSONObject();
            throw new AssertionError("Expected Exception was not thrown");
        } catch (Exception e) {
            assertEquals("Model is null", e.getMessage());
        }
    }
}