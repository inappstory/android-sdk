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

public class ToGameLogTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedGameLog_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameLog.gameInstanceId, "inst_1");
        json.put(ModelFields.GameLog.message, "Game started");
        json.put(ModelFields.GameLog.logSession, "sess_99");
        json.put(ModelFields.GameLog.webViewVersion, "120.0.6099.144");
        json.put(ModelFields.GameLog.stacktrace, "at line 1");
        json.put(ModelFields.GameLog.webViewPackage, "com.google.android.webview");
        json.put(ModelFields.GameLog.type, "info");
        json.put(ModelFields.GameLog.gameLoaded, true);
        json.put(ModelFields.GameLog.timestamp, 1700000000000L);
        json.put(ModelFields.GameLog.launchTryNumber, 2);

        GameLog result = new ToGameLog(json).parse();

        assertNotNull(result);
        assertEquals("inst_1", result.gameInstanceId);
        assertEquals("Game started", result.message);
        assertEquals("sess_99", result.logSession);
        assertEquals("120.0.6099.144", result.webViewVersion);
        assertEquals("at line 1", result.stacktrace);
        assertEquals("com.google.android.webview", result.webViewPackage);
        assertEquals("info", result.type);
        assertTrue(result.gameLoaded);
        assertEquals(Long.valueOf(1700000000000L), result.timestamp);
        assertEquals(2, result.launchTryNumber);
    }

    @Test
    public void parse_returnsDefaultValues_whenJsonIsEmpty() {
        GameLog result = new ToGameLog(new JSONObject()).parse();

        assertNotNull(result);
        assertNull(result.gameInstanceId);
        assertNull(result.message);
        assertNull(result.logSession);
        assertNull(result.webViewVersion);
        assertNull(result.stacktrace);
        assertNull(result.webViewPackage);
        assertNull(result.type);
        assertFalse(result.gameLoaded);
        assertNull(result.timestamp);
        assertEquals(0, result.launchTryNumber);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToGameLog(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToGameLog parser = new ToGameLog(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_handlesInvalidDataTypes_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Нестроковые типы и строковый "null" для optStringNullable -> должны стать null
        json.put(ModelFields.GameLog.gameInstanceId, 123);
        json.put(ModelFields.GameLog.message, new JSONObject());
        json.put(ModelFields.GameLog.logSession, new JSONArray());
        json.put(ModelFields.GameLog.webViewVersion, true);
        json.put(ModelFields.GameLog.stacktrace, " null ");
        json.put(ModelFields.GameLog.webViewPackage, JSONObject.NULL);
        json.put(ModelFields.GameLog.type, 45.6);

        // Невалидные типы для boolean, Long и int
        json.put(ModelFields.GameLog.gameLoaded, "not_a_boolean");
        json.put(ModelFields.GameLog.timestamp, "not_a_long");
        json.put(ModelFields.GameLog.launchTryNumber, new JSONObject());

        GameLog result = new ToGameLog(json).parse();

        assertNotNull(result);
        assertNull(result.gameInstanceId);
        assertNull(result.message);
        assertNull(result.logSession);
        assertNull(result.webViewVersion);
        assertNull(result.stacktrace);
        assertNull(result.webViewPackage);
        assertNull(result.type);
        assertFalse(result.gameLoaded);
        assertNull(result.timestamp);
        assertEquals(0, result.launchTryNumber);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameLog parser = new ToGameLog(new JSONObject());

        assertEquals("GameLog", parser.getModelClassName());
    }
}
