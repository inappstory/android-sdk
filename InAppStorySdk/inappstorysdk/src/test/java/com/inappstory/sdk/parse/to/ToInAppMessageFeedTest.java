package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToInAppMessageFeedTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedFeed_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject msg1 = new JSONObject();
        msg1.put(ModelFields.InAppMessage.id, 101);
        
        JSONObject msg2 = new JSONObject();
        msg2.put(ModelFields.InAppMessage.id, 102);

        JSONArray messagesArray = new JSONArray();
        messagesArray.put(msg1);
        messagesArray.put(msg2);

        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageFeed.messages, messagesArray);

        ToInAppMessageFeed parser = new ToInAppMessageFeed(json.toString());
        InAppMessageFeed result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.messages);
        assertEquals(2, result.messages.size());
        assertEquals(101, result.messages.get(0).id);
        assertEquals(102, result.messages.get(1).id);
    }

    @Test
    public void parse_returnsFeedWithNullMessages_whenKeyIsMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToInAppMessageFeed parser = new ToInAppMessageFeed(json.toString());
        InAppMessageFeed result = parser.parse();

        assertNotNull(result);
        assertNull(result.messages);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToInAppMessageFeed((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToInAppMessageFeed("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToInAppMessageFeed("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypeForMessages_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем строку вместо массива
        json.put(ModelFields.InAppMessageFeed.messages, "not_an_array");

        ToInAppMessageFeed parser = new ToInAppMessageFeed(json.toString());
        InAppMessageFeed result = parser.parse();

        // optJSONArray вернет null для строки, и ToInAppMessageList корректно обработает null
        assertNotNull(result);
        assertNull(result.messages);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInAppMessageFeed parser = new ToInAppMessageFeed("{}");
        assertEquals("InAppMessageFeed", parser.getModelClassName());
    }
}