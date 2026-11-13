package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameScreenOptionsTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedOptions_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameScreenOptions.fullScreen, true);
        json.put(ModelFields.GameScreenOptions.screenOrientation, "portrait");

        GameScreenOptions result = new ToGameScreenOptions(json).parse();

        assertNotNull(result);
        assertTrue(result.fullScreen);
        assertEquals("portrait", result.screenOrientation);
    }

    @Test
    public void parse_returnsDefaultValues_whenJsonIsEmpty() {
        GameScreenOptions result = new ToGameScreenOptions(new JSONObject()).parse();

        assertNotNull(result);
        assertFalse(result.fullScreen);
        assertNull(result.screenOrientation);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToGameScreenOptions(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToGameScreenOptions parser = new ToGameScreenOptions(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_handlesInvalidDataTypes_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameScreenOptions.fullScreen, "not_a_boolean");
        json.put(ModelFields.GameScreenOptions.screenOrientation, 12345);

        GameScreenOptions result = new ToGameScreenOptions(json).parse();

        assertNotNull(result);
        assertFalse(result.fullScreen);
        assertNull(result.screenOrientation);

        // Проверка JSONObject / JSONArray и строкового "null"
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.GameScreenOptions.fullScreen, new JSONObject());
        json2.put(ModelFields.GameScreenOptions.screenOrientation, new JSONArray());

        GameScreenOptions result2 = new ToGameScreenOptions(json2).parse();
        assertNotNull(result2);
        assertFalse(result2.fullScreen);
        assertNull(result2.screenOrientation);

        JSONObject json3 = new JSONObject();
        json3.put(ModelFields.GameScreenOptions.screenOrientation, " null ");
        assertNull(new ToGameScreenOptions(json3).parse().screenOrientation);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameScreenOptions parser = new ToGameScreenOptions(new JSONObject());

        assertEquals("GameScreenOptions", parser.getModelClassName());
    }
}
