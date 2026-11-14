package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToInAppMessageListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsMessageList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject msg1 = new JSONObject();
        msg1.put(ModelFields.InAppMessage.id, 101);
        msg1.put(ModelFields.InAppMessage.campaignName, "Campaign 1");

        JSONObject msg2 = new JSONObject();
        msg2.put(ModelFields.InAppMessage.id, 102);
        msg2.put(ModelFields.InAppMessage.campaignName, "Campaign 2");

        JSONArray array = new JSONArray();
        array.put(msg1);
        array.put(msg2);

        List<InAppMessage> result = new ToInAppMessageList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101, result.get(0).id);
        assertEquals("Campaign 1", result.get(0).campaignName);
        assertEquals(102, result.get(1).id);
        assertEquals("Campaign 2", result.get(1).campaignName);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<InAppMessage> result = new ToInAppMessageList(new JSONArray()).parse();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToInAppMessageList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.InAppMessage.id, 101));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToInAppMessageList(arrayWithString).parse());
        assertNull(new ToInAppMessageList(arrayWithNumber).parse());
        assertNull(new ToInAppMessageList(arrayWithNestedArray).parse());
        assertNull(new ToInAppMessageList(arrayWithJsonNull).parse());
    }

    @Test
    public void parse_returnsNull_whenAnyMessageMissingRequiredId() throws JSONException {
        JSONObject validMessage = new JSONObject().put(ModelFields.InAppMessage.id, 101);
        JSONObject invalidMessageMissingId = new JSONObject().put(ModelFields.InAppMessage.campaignName, "No ID");

        JSONArray array = new JSONArray();
        array.put(validMessage);
        array.put(invalidMessageMissingId);

        // ToInAppMessage вернёт null для второго элемента (нет id) 
        // -> ToListParser вернёт null для всего списка
        assertNull(new ToInAppMessageList(array).parse());
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToInAppMessageParser() throws JSONException {
        ToInAppMessageList listParser = new ToInAppMessageList(new JSONArray());
        JSONObject msgJson = new JSONObject()
                .put(ModelFields.InAppMessage.id, 999)
                .put(ModelFields.InAppMessage.campaignName, "Test Campaign");

        ToInAppMessage itemParser = listParser.createParserInstance(msgJson);

        assertNotNull(itemParser);
        InAppMessage parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals(999, parsed.id);
        assertEquals("Test Campaign", parsed.campaignName);
    }
}