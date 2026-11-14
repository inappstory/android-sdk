package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.InAppMessageEvent;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToInAppMessageEventListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsEventList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject event1 = new JSONObject();
        event1.put(ModelFields.InAppMessageEvent.name, "event_start");
        event1.put(ModelFields.InAppMessageEvent.id, 10);
        event1.put(ModelFields.InAppMessageEvent.priority, 1);

        JSONObject event2 = new JSONObject();
        event2.put(ModelFields.InAppMessageEvent.name, "event_end");
        event2.put(ModelFields.InAppMessageEvent.id, 20);

        JSONArray array = new JSONArray();
        array.put(event1);
        array.put(event2);

        List<InAppMessageEvent> result = new ToInAppMessageEventList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("event_start", result.get(0).name);
        assertEquals(10, result.get(0).id);
        assertEquals("event_end", result.get(1).name);
        assertEquals(20, result.get(1).id);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<InAppMessageEvent> result = new ToInAppMessageEventList(new JSONArray()).parse();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToInAppMessageEventList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.InAppMessageEvent.name, "valid"));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);

        assertNull(new ToInAppMessageEventList(arrayWithString).parse());
        assertNull(new ToInAppMessageEventList(arrayWithNumber).parse());
    }

    // --- Проверка невалидных данных внутри элементов (строгая проверка обязательных полей) ---

    @Test
    public void parse_returnsNull_whenAnyEventMissingRequiredName() throws JSONException {
        JSONObject validEvent = new JSONObject().put(ModelFields.InAppMessageEvent.name, "valid_name");
        JSONObject invalidEventMissingName = new JSONObject().put(ModelFields.InAppMessageEvent.id, 99);

        JSONArray array = new JSONArray();
        array.put(validEvent);
        array.put(invalidEventMissingName);

        // ToInAppMessageEvent вернёт null для второго элемента (нет name) 
        // -> ToListParser вернёт null для всего списка
        assertNull(new ToInAppMessageEventList(array).parse());
    }

    @Test
    public void parse_ignoresInvalidTypesInOptionalEventFields() throws JSONException {
        JSONObject eventWithBadOptionalFields = new JSONObject();
        eventWithBadOptionalFields.put(ModelFields.InAppMessageEvent.name, "valid_name");
        eventWithBadOptionalFields.put(ModelFields.InAppMessageEvent.id, "not_an_int");
        eventWithBadOptionalFields.put(ModelFields.InAppMessageEvent.priority, new JSONObject());

        JSONArray array = new JSONArray().put(eventWithBadOptionalFields);

        List<InAppMessageEvent> result = new ToInAppMessageEventList(array).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("valid_name", result.get(0).name);
        assertEquals(0, result.get(0).id);
        assertEquals(0, result.get(0).priority);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToInAppMessageEventParser() throws JSONException {
        ToInAppMessageEventList listParser = new ToInAppMessageEventList(new JSONArray());
        JSONObject eventJson = new JSONObject()
                .put(ModelFields.InAppMessageEvent.name, "test_event")
                .put(ModelFields.InAppMessageEvent.id, 42);

        ToInAppMessageEvent itemParser = listParser.createParserInstance(eventJson);

        assertNotNull(itemParser);
        InAppMessageEvent parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals("test_event", parsed.name);
        assertEquals(42, parsed.id);
    }
}