package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromGameLogTest {

    @Test
    public void parseToJSONObject_returnsValidJsonObject_whenModelIsPopulated() throws Exception {
        GameLog model = new GameLog();
        model.message = "Test log message";
        model.logSession = "log_sess_123";
        model.webViewVersion = "100.0.0.0";
        model.stacktrace = "stacktrace here";
        model.gameInstanceId = "game_inst_456";
        model.type = "error";
        model.webViewPackage = "com.android.webview";
        model.gameLoaded = true;
        model.timestamp = 1690000000L;
        model.launchTryNumber = 3;

        FromGameLog parser = new FromGameLog(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals("Test log message", result.getString(ModelFields.GameLog.message));
        assertEquals("log_sess_123", result.getString(ModelFields.GameLog.logSession));
        assertEquals("100.0.0.0", result.getString(ModelFields.GameLog.webViewVersion));
        assertEquals("stacktrace here", result.getString(ModelFields.GameLog.stacktrace));
        assertEquals("game_inst_456", result.getString(ModelFields.GameLog.gameInstanceId));
        assertEquals("error", result.getString(ModelFields.GameLog.type));
        assertEquals("com.android.webview", result.getString(ModelFields.GameLog.webViewPackage));
        assertTrue(result.getBoolean(ModelFields.GameLog.gameLoaded));
        assertEquals(1690000000L, result.getLong(ModelFields.GameLog.timestamp));
        assertEquals(3, result.getInt(ModelFields.GameLog.launchTryNumber));
    }

    // --- Негативные сценарии ---

    @Test(expected = NullPointerException.class)
    public void parseToJSONObject_throwsNPE_whenModelIsNull() throws Exception {
        FromGameLog parser = new FromGameLog(null);
        // ModelToJSONObjectParser не имеет try-catch, NPE пробросится наружу
        parser.parseToJSONObject();
    }

    @Test
    public void parseToJSONObject_handlesNullStringFields() throws Exception {
        GameLog model = new GameLog();
        // Все строковые поля null
        model.message = null;
        model.logSession = null;
        model.webViewVersion = null;
        model.stacktrace = null;
        model.gameInstanceId = null;
        model.type = null;
        model.webViewPackage = null;
        // Примитивные поля имеют значения по умолчанию
        model.gameLoaded = false;
        model.timestamp = 0L;
        model.launchTryNumber = 0;

        FromGameLog parser = new FromGameLog(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        // Строковые поля с null не добавляются в JSON
        assertFalse(result.has(ModelFields.GameLog.message));
        assertFalse(result.has(ModelFields.GameLog.logSession));
        assertFalse(result.has(ModelFields.GameLog.webViewVersion));
        assertFalse(result.has(ModelFields.GameLog.stacktrace));
        assertFalse(result.has(ModelFields.GameLog.gameInstanceId));
        assertFalse(result.has(ModelFields.GameLog.type));
        assertFalse(result.has(ModelFields.GameLog.webViewPackage));
        // Примитивные поля добавляются с дефолтными значениями
        assertEquals(false, result.getBoolean(ModelFields.GameLog.gameLoaded));
        assertEquals(0L, result.getLong(ModelFields.GameLog.timestamp));
        assertEquals(0, result.getInt(ModelFields.GameLog.launchTryNumber));
    }

    @Test
    public void parseToJSONObject_handlesEmptyStringFields() throws Exception {
        GameLog model = new GameLog();
        model.message = "";
        model.logSession = "";
        model.webViewVersion = "";
        model.stacktrace = "";
        model.gameInstanceId = "";
        model.type = "";
        model.webViewPackage = "";
        model.gameLoaded = false;
        model.timestamp = 0L;
        model.launchTryNumber = 0;

        FromGameLog parser = new FromGameLog(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals("", result.getString(ModelFields.GameLog.message));
        assertEquals("", result.getString(ModelFields.GameLog.logSession));
        assertEquals("", result.getString(ModelFields.GameLog.webViewVersion));
        assertEquals("", result.getString(ModelFields.GameLog.stacktrace));
        assertEquals("", result.getString(ModelFields.GameLog.gameInstanceId));
        assertEquals("", result.getString(ModelFields.GameLog.type));
        assertEquals("", result.getString(ModelFields.GameLog.webViewPackage));
    }
}