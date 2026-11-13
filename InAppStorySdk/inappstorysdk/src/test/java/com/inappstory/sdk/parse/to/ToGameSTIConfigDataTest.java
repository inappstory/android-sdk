package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameSTIConfigData;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameSTIConfigDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedConfigData_whenValidJsonProvided() throws JSONException {
        JSONObject inputJson = new JSONObject();
        inputJson.put(ModelFields.GameSTIConfigInputData.type, "text");
        inputJson.put(ModelFields.GameSTIConfigInputData.placeholder, "Your name");

        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSTIConfigData.title, "Enter details");
        json.put(ModelFields.GameSTIConfigData.button, "Send");
        json.put(ModelFields.GameSTIConfigData.input, inputJson);

        GameSTIConfigData result = new ToGameSTIConfigData(json).parse();

        assertNotNull(result);
        assertEquals("Enter details", result.title);
        assertEquals("Send", result.button);
        assertNotNull(result.input);
        assertEquals("text", result.input.type);
        assertEquals("Your name", result.input.placeholder);
    }

    @Test
    public void parse_returnsNullFields_whenJsonIsEmpty() {
        GameSTIConfigData result = new ToGameSTIConfigData(new JSONObject()).parse();

        assertNotNull(result);
        assertNull(result.title);
        assertNull(result.button);
        assertNull(result.input);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToGameSTIConfigData(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToGameSTIConfigData parser = new ToGameSTIConfigData(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSTIConfigData.title, 12345);
        json.put(ModelFields.GameSTIConfigData.button, new JSONArray());
        json.put(ModelFields.GameSTIConfigData.input, "not_a_json_object");

        GameSTIConfigData result = new ToGameSTIConfigData(json).parse();

        assertNotNull(result);
        assertNull(result.title);
        assertNull(result.button);
        assertNull(result.input);

        // Проверка строкового "null" и числа вместо вложенного объекта
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.GameSTIConfigData.title, " null ");
        json2.put(ModelFields.GameSTIConfigData.button, JSONObject.NULL);
        json2.put(ModelFields.GameSTIConfigData.input, 999);

        GameSTIConfigData result2 = new ToGameSTIConfigData(json2).parse();

        assertNotNull(result2);
        assertNull(result2.title);
        assertNull(result2.button);
        assertNull(result2.input);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameSTIConfigData parser = new ToGameSTIConfigData(new JSONObject());

        assertEquals("GameSTIConfigData", parser.getModelClassName());
    }
}
