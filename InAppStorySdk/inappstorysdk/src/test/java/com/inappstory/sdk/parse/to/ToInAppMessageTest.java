package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToInAppMessageTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedMessage_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessage.id, 1001);
        json.put(ModelFields.InAppMessage.hasPlaceholders, true);
        json.put(ModelFields.InAppMessage.hasLimit, true);
        json.put(ModelFields.InAppMessage.displayTo, 1700000000L);
        json.put(ModelFields.InAppMessage.displayFrom, 1600000000L);
        json.put(ModelFields.InAppMessage.frequencyLimit, 3600L);
        json.put(ModelFields.InAppMessage.campaignName, "Summer Sale");
        json.put(ModelFields.InAppMessage.hasSwipeUp, true);
        json.put(ModelFields.InAppMessage.disableClose, false);
        json.put(ModelFields.InAppMessage.screenType, 2);
        json.put(ModelFields.InAppMessage.customAttributes, new JSONObject().put("key", "value"));
        json.put(ModelFields.InAppMessage.slides, new JSONArray());
        json.put(ModelFields.InAppMessage.events, new JSONArray());

        ToInAppMessage parser = new ToInAppMessage(json);
        InAppMessage result = parser.parse();

        assertNotNull(result);
        assertEquals(1001, result.id);
        assertTrue(result.hasPlaceholders);
        assertTrue(result.hasLimit);
        assertEquals(Long.valueOf(1700000000L), result.displayTo);
        assertEquals(Long.valueOf(1600000000L), result.displayFrom);
        assertEquals(Long.valueOf(3600L), result.frequencyLimit);
        assertEquals("Summer Sale", result.campaignName);
        assertEquals(Boolean.TRUE, result.hasSwipeUp);
        assertFalse(result.disableClose);
        assertEquals(2, result.screenType);
        assertNotNull(result.customAttributes);
        assertNotNull(result.slides);
        assertNotNull(result.events);
    }

    @Test
    public void parse_returnsPopulatedMessage_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessage.id, 2002);
        json.put(ModelFields.InAppMessage.campaignName, "String Test");

        ToInAppMessage parser = new ToInAppMessage(json.toString());
        InAppMessage result = parser.parse();

        assertNotNull(result);
        assertEquals(2002, result.id);
        assertEquals("String Test", result.campaignName);
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() throws JSONException {
        JSONObject jsonWithoutId = new JSONObject();
        jsonWithoutId.put(ModelFields.InAppMessage.campaignName, "No ID");

        assertNull(new ToInAppMessage(jsonWithoutId).parse());
        assertNull(new ToInAppMessage("{}").parse());
    }

    @Test
    public void parse_returnsNull_whenIdHasInvalidDataType() throws JSONException {
        JSONObject stringId = new JSONObject();
        stringId.put(ModelFields.InAppMessage.id, "not_a_number");

        assertNull(new ToInAppMessage(stringId).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToInAppMessage((JSONObject) null).parse());
        assertNull(new ToInAppMessage((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToInAppMessage("{invalid_json").parse());
        assertNull(new ToInAppMessage("").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessage.id, 3003);
        
        // Передаем невалидные типы для опциональных полей
        json.put(ModelFields.InAppMessage.hasPlaceholders, "not_a_boolean");
        json.put(ModelFields.InAppMessage.displayTo, "not_a_long");
        json.put(ModelFields.InAppMessage.campaignName, 123);
        json.put(ModelFields.InAppMessage.hasSwipeUp, 999);
        json.put(ModelFields.InAppMessage.screenType, "not_an_int");
        json.put(ModelFields.InAppMessage.customAttributes, "not_an_object");
        json.put(ModelFields.InAppMessage.slides, "not_an_array");

        ToInAppMessage parser = new ToInAppMessage(json);
        InAppMessage result = parser.parse();

        assertNotNull(result);
        assertEquals(3003, result.id);
        assertFalse(result.hasPlaceholders);
        assertNull(result.displayTo);
        assertNull(result.campaignName);
        assertNull(result.hasSwipeUp);
        assertEquals(0, result.screenType);
        assertNull(result.customAttributes);
        assertNull(result.slides);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInAppMessage parser = new ToInAppMessage(new JSONObject());
        assertEquals("InAppMessage", parser.getModelClassName());
    }
}