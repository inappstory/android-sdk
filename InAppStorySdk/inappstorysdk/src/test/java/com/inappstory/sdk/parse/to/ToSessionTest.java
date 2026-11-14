package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.Session;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSessionTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedSession_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Session.id, "session_abc123");

        ToSession parser = new ToSession(json);
        Session result = parser.parse();

        assertNotNull(result);
        assertEquals("session_abc123", result.id);
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() throws JSONException {
        JSONObject json = new JSONObject();

        // getString выбросит исключение -> catch вернет null
        assertNull(new ToSession(json).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredIdHasInvalidDataType() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Session.id, 12345); // Ожидается строка

        assertNull(new ToSession(json).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToSession(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToSession parser = new ToSession(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSession parser = new ToSession(new JSONObject());
        assertEquals("Session", parser.getModelClassName());
    }
}