package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameSTIConfigInputData;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameSTIConfigInputDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedInputData_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSTIConfigInputData.type, "email");
        json.put(ModelFields.GameSTIConfigInputData.placeholder, "Enter your email");

        GameSTIConfigInputData result = new ToGameSTIConfigInputData(json).parse();

        assertNotNull(result);
        assertEquals("email", result.type);
        assertEquals("Enter your email", result.placeholder);
    }

    @Test
    public void parse_returnsNullFields_whenJsonIsEmpty() {
        GameSTIConfigInputData result = new ToGameSTIConfigInputData(new JSONObject()).parse();

        assertNotNull(result);
        assertNull(result.type);
        assertNull(result.placeholder);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToGameSTIConfigInputData(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToGameSTIConfigInputData parser = new ToGameSTIConfigInputData(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSTIConfigInputData.type, 12345);
        json.put(ModelFields.GameSTIConfigInputData.placeholder, new JSONObject());

        GameSTIConfigInputData result = new ToGameSTIConfigInputData(json).parse();

        assertNotNull(result);
        assertNull(result.type);
        assertNull(result.placeholder);

        // Проверка массива, boolean и строкового "null"
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.GameSTIConfigInputData.type, new JSONArray());
        json2.put(ModelFields.GameSTIConfigInputData.placeholder, " null ");

        GameSTIConfigInputData result2 = new ToGameSTIConfigInputData(json2).parse();

        assertNotNull(result2);
        assertNull(result2.type);
        assertNull(result2.placeholder);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameSTIConfigInputData parser = new ToGameSTIConfigInputData(new JSONObject());

        assertEquals("GameSTIConfigInputData", parser.getModelClassName());
    }
}
