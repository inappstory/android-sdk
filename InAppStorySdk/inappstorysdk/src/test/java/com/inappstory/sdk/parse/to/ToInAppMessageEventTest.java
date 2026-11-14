package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageEvent;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToInAppMessageEventTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedEvent_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageEvent.name, "test_event_name");
        json.put(ModelFields.InAppMessageEvent.id, 42);
        json.put(ModelFields.InAppMessageEvent.priority, 10);

        ToInAppMessageEvent parser = new ToInAppMessageEvent(json);
        InAppMessageEvent result = parser.parse();

        assertNotNull(result);
        assertEquals("test_event_name", result.name);
        assertEquals(42, result.id);
        assertEquals(10, result.priority);
    }

    @Test
    public void parse_returnsEventWithDefaultValues_whenOnlyRequiredNameProvided() throws JSONException {
        JSONObject json = new JSONObject();
        // name - обязательное поле (getString)
        json.put(ModelFields.InAppMessageEvent.name, "test_event");
        // id и priority опущены

        ToInAppMessageEvent parser = new ToInAppMessageEvent(json);
        InAppMessageEvent result = parser.parse();

        assertNotNull(result);
        assertEquals("test_event", result.name);
        assertEquals(0, result.id); // optInt возвращает 0 по умолчанию
        assertEquals(0, result.priority); // optInt возвращает 0 по умолчанию
    }

    // --- Проверка обязательных полей и null ---

    @Test
    public void parse_returnsNull_whenRequiredNameIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageEvent.id, 42);
        // name отсутствует -> getString выбросит исключение -> catch вернет null

        assertNull(new ToInAppMessageEvent(json).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToInAppMessageEvent(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToInAppMessageEvent parser = new ToInAppMessageEvent(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InAppMessageEvent.name, "valid_name");

        // Передаем невалидные типы для int полей
        json.put(ModelFields.InAppMessageEvent.id, "not_an_int");
        json.put(ModelFields.InAppMessageEvent.priority, new JSONObject());

        ToInAppMessageEvent parser = new ToInAppMessageEvent(json);
        InAppMessageEvent result = parser.parse();

        assertNotNull(result);
        assertEquals("valid_name", result.name);
        assertEquals(0, result.id);
        assertEquals(0, result.priority);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInAppMessageEvent parser = new ToInAppMessageEvent(new JSONObject());
        assertEquals("InAppMessageEvent", parser.getModelClassName());
    }
}