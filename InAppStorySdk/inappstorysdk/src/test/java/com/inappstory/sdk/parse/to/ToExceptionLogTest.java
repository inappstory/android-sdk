package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToExceptionLogTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedExceptionLog_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ExceptionLog.id, "err_123");
        json.put(ModelFields.ExceptionLog.session, "sess_456");
        json.put(ModelFields.ExceptionLog.stacktrace, "java.lang.NullPointerException\n\tat Main.java:10");
        json.put(ModelFields.ExceptionLog.cause, "Null reference");
        json.put(ModelFields.ExceptionLog.file, "Main.java");
        json.put(ModelFields.ExceptionLog.message, "Object is null");
        json.put(ModelFields.ExceptionLog.line, 10);
        json.put(ModelFields.ExceptionLog.timestamp, 1700000000000L);

        ExceptionLog result = new ToExceptionLog(json.toString()).parse();

        assertNotNull(result);
        assertEquals("err_123", result.id);
        assertEquals("sess_456", result.session);
        assertEquals("java.lang.NullPointerException\n\tat Main.java:10", result.stacktrace);
        assertEquals("Null reference", result.cause);
        assertEquals("Main.java", result.file);
        assertEquals("Object is null", result.message);
        assertEquals(10, result.line);
        assertEquals(1700000000000L, result.timestamp);
    }

    @Test
    public void parse_returnsDefaultValues_whenJsonIsEmpty() {
        ExceptionLog result = new ToExceptionLog("{}").parse();

        assertNotNull(result);
        assertNull(result.id);
        assertNull(result.session);
        assertNull(result.stacktrace);
        assertNull(result.cause);
        assertNull(result.file);
        assertNull(result.message);
        assertEquals(0, result.line);
        assertEquals(0L, result.timestamp);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToExceptionLog(null).parse());
        assertNull(new ToExceptionLog("").parse());
        assertNull(new ToExceptionLog("{broken_json").parse());
        assertNull(new ToExceptionLog("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_handlesInvalidDataTypes_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Нестроковые типы и строковый "null" для optStringNullable полей -> должны стать null
        json.put(ModelFields.ExceptionLog.id, 12345);
        json.put(ModelFields.ExceptionLog.session, new JSONObject());
        json.put(ModelFields.ExceptionLog.stacktrace, new JSONArray());
        json.put(ModelFields.ExceptionLog.cause, true);
        json.put(ModelFields.ExceptionLog.file, " null ");
        json.put(ModelFields.ExceptionLog.message, JSONObject.NULL);

        // Нечисловые типы для optInt и optLong -> должны стать 0 и 0L
        json.put(ModelFields.ExceptionLog.line, "not_an_int");
        json.put(ModelFields.ExceptionLog.timestamp, new JSONObject());

        ExceptionLog result = new ToExceptionLog(json.toString()).parse();

        assertNotNull(result);
        assertNull(result.id);
        assertNull(result.session);
        assertNull(result.stacktrace);
        assertNull(result.cause);
        assertNull(result.file);
        assertNull(result.message);
        assertEquals(0, result.line);
        assertEquals(0L, result.timestamp);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToExceptionLog parser = new ToExceptionLog("{}");

        assertEquals("ExceptionLog", parser.getModelClassName());
    }
}
