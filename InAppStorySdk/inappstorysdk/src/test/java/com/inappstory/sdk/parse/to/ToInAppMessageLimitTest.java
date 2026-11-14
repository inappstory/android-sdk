package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToInAppMessageLimitTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedLimit_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageLimit.messageId, 5001);
        json.put(ModelFields.InAppMessageLimit.canOpen, true);
        json.put(ModelFields.InAppMessageLimit.expire, 1700000000L);

        ToInAppMessageLimit parser = new ToInAppMessageLimit(json);
        InAppMessageLimit result = parser.parse();

        assertNotNull(result);
        assertEquals(5001, result.messageId);
        assertTrue(result.canOpen);
        assertEquals(1700000000L, result.expire);
    }

    @Test
    public void parse_returnsLimitWithDefaultValues_whenOnlyRequiredIdProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageLimit.messageId, 5002);

        ToInAppMessageLimit parser = new ToInAppMessageLimit(json);
        InAppMessageLimit result = parser.parse();

        assertNotNull(result);
        assertEquals(5002, result.messageId);
        assertFalse(result.canOpen); // optBoolean по умолчанию возвращает false
        assertEquals(0L, result.expire); // optLong по умолчанию возвращает 0
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredMessageIdIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageLimit.canOpen, true);

        assertNull(new ToInAppMessageLimit(json).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredMessageIdHasInvalidDataType() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageLimit.messageId, "not_a_number");

        assertNull(new ToInAppMessageLimit(json).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToInAppMessageLimit(null).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageLimit.messageId, 5003);
        json.put(ModelFields.InAppMessageLimit.canOpen, "not_a_boolean");
        json.put(ModelFields.InAppMessageLimit.expire, "not_a_long");

        ToInAppMessageLimit parser = new ToInAppMessageLimit(json);
        InAppMessageLimit result = parser.parse();

        assertNotNull(result);
        assertEquals(5003, result.messageId);
        assertFalse(result.canOpen);
        assertEquals(0L, result.expire);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInAppMessageLimit parser = new ToInAppMessageLimit(new JSONObject());
        assertEquals("InAppMessageLimit", parser.getModelClassName());
    }
}