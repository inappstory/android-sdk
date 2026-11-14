package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class FromExceptionLogTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsPopulated() throws JSONException {
        ExceptionLog model = new ExceptionLog();
        model.timestamp = 1690000000L;
        model.id = "log_id_123";
        model.session = "session_abc";
        model.file = "MainActivity.java";
        model.message = "Test exception message";
        model.stacktrace = "java.lang.NullPointerException\n\tat com.example...";
        model.line = 42;
        model.cause = "Unknown cause";

        FromExceptionLog parser = new FromExceptionLog(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals(1690000000L, json.getLong(ModelFields.ExceptionLog.timestamp));
        assertEquals("log_id_123", json.getString(ModelFields.ExceptionLog.id));
        assertEquals("session_abc", json.getString(ModelFields.ExceptionLog.session));
        assertEquals("MainActivity.java", json.getString(ModelFields.ExceptionLog.file));
        assertEquals("Test exception message", json.getString(ModelFields.ExceptionLog.message));
        assertEquals("java.lang.NullPointerException\n\tat com.example...", json.getString(ModelFields.ExceptionLog.stacktrace));
        assertEquals(42, json.getInt(ModelFields.ExceptionLog.line));
        assertEquals("Unknown cause", json.getString(ModelFields.ExceptionLog.cause));
    }

    // --- Негативные сценарии ---

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromExceptionLog parser = new FromExceptionLog(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesNullStringFields_whenModelHasNullValues() throws Exception {
        ExceptionLog model = new ExceptionLog();
        model.timestamp = 1690000000L;
        model.line = 42;
        // Все строковые поля остаются null
        model.id = null;
        model.session = null;
        model.file = null;
        model.message = null;
        model.stacktrace = null;
        model.cause = null;

        FromExceptionLog parser = new FromExceptionLog(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals(1690000000L, json.getLong(ModelFields.ExceptionLog.timestamp));
        assertEquals(42, json.getInt(ModelFields.ExceptionLog.line));
        // JSONObject.put() с null значением не добавляет ключ
        assertFalse(json.has(ModelFields.ExceptionLog.id));
        assertFalse(json.has(ModelFields.ExceptionLog.session));
        assertFalse(json.has(ModelFields.ExceptionLog.file));
        assertFalse(json.has(ModelFields.ExceptionLog.message));
        assertFalse(json.has(ModelFields.ExceptionLog.stacktrace));
        assertFalse(json.has(ModelFields.ExceptionLog.cause));
    }

    @Test
    public void parse_handlesEmptyStringFields_whenModelHasEmptyStrings() throws Exception {
        ExceptionLog model = new ExceptionLog();
        model.timestamp = 0L;
        model.line = 0;
        model.id = "";
        model.session = "";
        model.file = "";
        model.message = "";
        model.stacktrace = "";
        model.cause = "";

        FromExceptionLog parser = new FromExceptionLog(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals(0L, json.getLong(ModelFields.ExceptionLog.timestamp));
        assertEquals(0, json.getInt(ModelFields.ExceptionLog.line));
        assertEquals("", json.getString(ModelFields.ExceptionLog.id));
        assertEquals("", json.getString(ModelFields.ExceptionLog.session));
        assertEquals("", json.getString(ModelFields.ExceptionLog.file));
        assertEquals("", json.getString(ModelFields.ExceptionLog.message));
        assertEquals("", json.getString(ModelFields.ExceptionLog.stacktrace));
        assertEquals("", json.getString(ModelFields.ExceptionLog.cause));
    }
}