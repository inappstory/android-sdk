package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameSTIData;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameSTIDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedGameSTIData_whenValidJsonProvided() throws JSONException {
        JSONObject inputJson = new JSONObject()
                .put(ModelFields.GameSTIConfigInputData.type, "phone")
                .put(ModelFields.GameSTIConfigInputData.placeholder, "+7...");

        JSONObject configJson = new JSONObject()
                .put(ModelFields.GameSTIConfigData.title, "Phone")
                .put(ModelFields.GameSTIConfigData.button, "OK")
                .put(ModelFields.GameSTIConfigData.input, inputJson);

        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSTIData.cb, "callback_fn");
        json.put(ModelFields.GameSTIData.id, "sti_101");
        json.put(ModelFields.GameSTIData.config, configJson);

        GameSTIData result = new ToGameSTIData(json.toString()).parse();

        assertNotNull(result);
        assertEquals("callback_fn", result.cb);
        assertEquals("sti_101", result.id);
        assertNotNull(result.config);
        assertEquals("Phone", result.config.title);
        assertEquals("OK", result.config.button);
        assertNotNull(result.config.input);
        assertEquals("phone", result.config.input.type);
    }

    @Test
    public void parse_returnsNullFields_whenJsonIsEmpty() {
        GameSTIData result = new ToGameSTIData("{}").parse();

        assertNotNull(result);
        assertNull(result.cb);
        assertNull(result.id);
        assertNull(result.config);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToGameSTIData(null).parse());
        assertNull(new ToGameSTIData("").parse());
        assertNull(new ToGameSTIData("{broken_json").parse());
        assertNull(new ToGameSTIData("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSTIData.cb, 123);
        json.put(ModelFields.GameSTIData.id, new JSONArray());
        json.put(ModelFields.GameSTIData.config, "not_an_object");

        GameSTIData result = new ToGameSTIData(json.toString()).parse();

        assertNotNull(result);
        assertNull(result.cb);
        assertNull(result.id);
        assertNull(result.config);

        // Проверка строкового "null" и числа вместо config
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.GameSTIData.cb, " null ");
        json2.put(ModelFields.GameSTIData.id, JSONObject.NULL);
        json2.put(ModelFields.GameSTIData.config, 555);

        GameSTIData result2 = new ToGameSTIData(json2.toString()).parse();

        assertNotNull(result2);
        assertNull(result2.cb);
        assertNull(result2.id);
        assertNull(result2.config);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameSTIData parser = new ToGameSTIData("{}");

        assertEquals("GameSTIData", parser.getModelClassName());
    }
}
