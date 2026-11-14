package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToInAppMessageLimitListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsLimitList_whenValidJsonArrayStringProvided() throws JSONException {
        JSONObject limit1 = new JSONObject();
        limit1.put(ModelFields.InAppMessageLimit.messageId, 1001);
        limit1.put(ModelFields.InAppMessageLimit.canOpen, true);

        JSONObject limit2 = new JSONObject();
        limit2.put(ModelFields.InAppMessageLimit.messageId, 1002);
        limit2.put(ModelFields.InAppMessageLimit.expire, 5000L);

        JSONArray array = new JSONArray();
        array.put(limit1);
        array.put(limit2);

        ToInAppMessageLimitList parser = new ToInAppMessageLimitList(array.toString());
        List<InAppMessageLimit> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1001, result.get(0).messageId);
        assertTrue(result.get(0).canOpen);
        assertEquals(1002, result.get(1).messageId);
        assertEquals(5000L, result.get(1).expire);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayStringProvided() {
        ToInAppMessageLimitList parser = new ToInAppMessageLimitList("[]");
        List<InAppMessageLimit> result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToInAppMessageLimitList((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToInAppMessageLimitList("{invalid_json").parse());
    }

    @Test
    public void parse_returnsNull_whenAnyLimitIsInvalid_andFailsWholeList() throws JSONException {
        JSONArray array = new JSONArray();
        
        // 1. Валидный элемент
        JSONObject validLimit = new JSONObject();
        validLimit.put(ModelFields.InAppMessageLimit.messageId, 1001);
        array.put(validLimit);
        
        // 2. Невалидный элемент: отсутствует обязательное поле 'messageId'
        JSONObject invalidLimitMissingField = new JSONObject();
        invalidLimitMissingField.put(ModelFields.InAppMessageLimit.canOpen, true);
        array.put(invalidLimitMissingField);

        ToInAppMessageLimitList parser = new ToInAppMessageLimitList(array.toString());
        
        // Из-за конструкции `if (limit == null) return null;`, весь список должен вернуться как null
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.InAppMessageLimit.messageId, 1001));
        arrayWithString.put("not_an_object");

        // optJSONObject(i) вернет null для строки, парсинг вернет null, и весь метод вернет null
        assertNull(new ToInAppMessageLimitList(arrayWithString.toString()).parse());
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInAppMessageLimitList parser = new ToInAppMessageLimitList("[]");
        assertEquals("List of InAppMessageLimit", parser.getModelClassName());
    }
}