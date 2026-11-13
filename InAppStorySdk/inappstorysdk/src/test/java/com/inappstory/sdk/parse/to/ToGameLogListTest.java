package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToGameLogListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsGameLogList_whenValidJsonArrayStringProvided() throws JSONException {
        JSONObject log1 = new JSONObject();
        log1.put(ModelFields.GameLog.gameInstanceId, "inst_1");
        log1.put(ModelFields.GameLog.message, "First log");
        log1.put(ModelFields.GameLog.gameLoaded, true);

        JSONObject log2 = new JSONObject();
        log2.put(ModelFields.GameLog.gameInstanceId, "inst_2");
        log2.put(ModelFields.GameLog.launchTryNumber, 3);

        JSONArray array = new JSONArray();
        array.put(log1);
        array.put(log2);

        List<GameLog> result = new ToGameLogList(array.toString()).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("inst_1", result.get(0).gameInstanceId);
        assertEquals("First log", result.get(0).message);
        assertTrue(result.get(0).gameLoaded);

        assertEquals("inst_2", result.get(1).gameInstanceId);
        assertEquals(3, result.get(1).launchTryNumber);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayStringProvided() {
        List<GameLog> result = new ToGameLogList("[]").parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Обработка null и невалидного JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToGameLogList(null).parse());
        assertNull(new ToGameLogList("").parse());
        assertNull(new ToGameLogList("{broken_json").parse());
        // На верхнем уровне передан JSONObject вместо JSONArray
        assertNull(new ToGameLogList("{\"message\": \"not_an_array\"}").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.GameLog.message, "ok"));
        arrayWithString.put("invalid_string_element");

        JSONArray arrayWithNumber = new JSONArray().put(123);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToGameLogList(arrayWithString.toString()).parse());
        assertNull(new ToGameLogList(arrayWithNumber.toString()).parse());
        assertNull(new ToGameLogList(arrayWithNestedArray.toString()).parse());
        assertNull(new ToGameLogList(arrayWithJsonNull.toString()).parse());
    }

    @Test
    public void parse_keepsItemsWithDefaultValues_whenLogFieldsHaveInvalidTypes() throws JSONException {
        JSONObject badLog = new JSONObject();
        badLog.put(ModelFields.GameLog.gameInstanceId, 999);
        badLog.put(ModelFields.GameLog.gameLoaded, "not_boolean");
        badLog.put(ModelFields.GameLog.launchTryNumber, "not_int");

        JSONArray array = new JSONArray().put(badLog);

        List<GameLog> result = new ToGameLogList(array.toString()).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).gameInstanceId);
        assertFalse(result.get(0).gameLoaded);
        assertEquals(0, result.get(0).launchTryNumber);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameLogList parser = new ToGameLogList("[]");

        assertEquals("List of GameLog", parser.getModelClassName());
    }
}
