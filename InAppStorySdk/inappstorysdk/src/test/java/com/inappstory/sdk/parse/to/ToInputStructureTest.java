package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.InputStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToInputStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedStructure_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InputStructure.type, "text");
        json.put(ModelFields.InputStructure.text, new JSONObject().put("value", "Enter name"));
        json.put(ModelFields.InputStructure.border, new JSONObject().put("width", 1));
        json.put(ModelFields.InputStructure.background, new JSONObject().put("color", "#FFF"));
        json.put(ModelFields.InputStructure.padding, new JSONObject().put("top", 10));

        ToInputStructure parser = new ToInputStructure(json);
        InputStructure result = parser.parse();

        assertNotNull(result);
        assertEquals("text", result.type);
        assertNotNull(result.text);
        assertNotNull(result.border);
        assertNotNull(result.background);
        assertNotNull(result.padding);
    }

    @Test
    public void parse_returnsStructureWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToInputStructure parser = new ToInputStructure(json);
        InputStructure result = parser.parse();

        assertNotNull(result);
        assertNull(result.type);
        assertNull(result.text);
        assertNull(result.border);
        assertNull(result.background);
        assertNull(result.padding);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToInputStructure(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToInputStructure parser = new ToInputStructure(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesForNestedObjects_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InputStructure.type, 123); // Ожидается строка
        // Передаем примитивы вместо объектов для вложенных структур
        json.put(ModelFields.InputStructure.text, "not_an_object");
        json.put(ModelFields.InputStructure.border, 456);
        json.put(ModelFields.InputStructure.background, true);
        json.put(ModelFields.InputStructure.padding, new JSONArray());

        ToInputStructure parser = new ToInputStructure(json);
        InputStructure result = parser.parse();

        // optJSONObject вернет null для не-объектов, и вложенные парсеры корректно обработают null
        assertNotNull(result);
        assertNull(result.type);
        assertNull(result.text);
        assertNull(result.border);
        assertNull(result.background);
        assertNull(result.padding);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInputStructure parser = new ToInputStructure(new JSONObject());
        assertEquals("InputStructure", parser.getModelClassName());
    }
}