package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.game.reader.GameLoadedConfig;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameLoadedConfigTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedConfig_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameLoadedConfig.backGesture, true);
        json.put(ModelFields.GameLoadedConfig.showClose, false);

        GameLoadedConfig result = new ToGameLoadedConfig(json.toString()).parse();

        assertNotNull(result);
        assertTrue(result.backGesture);
        assertFalse(result.showClose);
    }

    @Test
    public void parse_returnsDefaultValues_whenJsonIsEmpty() {
        GameLoadedConfig result = new ToGameLoadedConfig("{}").parse();

        assertNotNull(result);
        assertFalse(result.backGesture);
        assertTrue(result.showClose);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToGameLoadedConfig(null).parse());
        assertNull(new ToGameLoadedConfig("").parse());
        assertNull(new ToGameLoadedConfig("{broken_json").parse());
        assertNull(new ToGameLoadedConfig("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_usesDefaultBooleanValues_whenFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameLoadedConfig.backGesture, "not_a_boolean");
        json.put(ModelFields.GameLoadedConfig.showClose, 12345);

        GameLoadedConfig result = new ToGameLoadedConfig(json.toString()).parse();

        assertNotNull(result);
        assertFalse(result.backGesture);
        assertTrue(result.showClose);

        // Проверка JSONObject / JSONArray / JSONObject.NULL
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.GameLoadedConfig.backGesture, new JSONObject());
        json2.put(ModelFields.GameLoadedConfig.showClose, new JSONArray());

        GameLoadedConfig result2 = new ToGameLoadedConfig(json2.toString()).parse();

        assertNotNull(result2);
        assertFalse(result2.backGesture);
        assertTrue(result2.showClose);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameLoadedConfig parser = new ToGameLoadedConfig("{}");

        assertEquals("GameLoadedConfig", parser.getModelClassName());
    }
}
