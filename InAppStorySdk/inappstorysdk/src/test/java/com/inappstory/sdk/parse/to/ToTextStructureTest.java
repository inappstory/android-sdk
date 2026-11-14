package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.TextStructure;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToTextStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedTextStructure_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.TextStructure.align, "center");
        json.put(ModelFields.TextStructure.color, "#FF0000");
        json.put(ModelFields.TextStructure.value, "Hello World");
        json.put(ModelFields.TextStructure.placeholder, "Enter text");
        json.put(ModelFields.TextStructure.family, "Roboto");
        json.put(ModelFields.TextStructure.weight, "bold");
        json.put(ModelFields.TextStructure.style, "italic");
        json.put(ModelFields.TextStructure.maxLines, 3);
        json.put(ModelFields.TextStructure.limit, 100);
        json.put(ModelFields.TextStructure.lineHeight, 1.5);
        json.put(ModelFields.TextStructure.size, 16.0);

        ToTextStructure parser = new ToTextStructure(json);
        TextStructure result = parser.parse();

        assertNotNull(result);
        assertEquals("center", result.align);
        assertEquals("#FF0000", result.color);
        assertEquals("Hello World", result.value);
        assertEquals("Enter text", result.placeholder);
        assertEquals("Roboto", result.family);
        assertEquals("bold", result.weight);
        assertEquals("italic", result.style);
        assertEquals(3, result.maxLines);
        assertEquals(100, result.limit);
        assertEquals(1.5f, result.lineHeight, 0.001f);
        assertEquals(16.0f, result.size, 0.001f);
    }

    @Test
    public void parse_returnsTextStructureWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToTextStructure parser = new ToTextStructure(json);
        TextStructure result = parser.parse();

        assertNotNull(result);
        assertNull(result.align);
        assertNull(result.color);
        assertNull(result.value);
        assertNull(result.placeholder);
        assertNull(result.family);
        assertNull(result.weight);
        assertNull(result.style);
        assertEquals(0, result.maxLines);
        assertEquals(0, result.limit);
        // optDouble возвращает NaN, если ключ отсутствует или значение не число
        assertTrue(Float.isNaN(result.lineHeight));
        assertTrue(Float.isNaN(result.size));
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToTextStructure(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToTextStructure parser = new ToTextStructure(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.TextStructure.align, 123);
        json.put(ModelFields.TextStructure.color, new JSONObject());
        json.put(ModelFields.TextStructure.maxLines, "not_an_int");
        json.put(ModelFields.TextStructure.limit, new JSONObject());
        json.put(ModelFields.TextStructure.lineHeight, "not_a_double");
        json.put(ModelFields.TextStructure.size, new JSONObject());

        ToTextStructure parser = new ToTextStructure(json);
        TextStructure result = parser.parse();

        assertNotNull(result);
        assertNull(result.align);
        assertNull(result.color);
        assertEquals(0, result.maxLines);
        assertEquals(0, result.limit);
        assertTrue(Float.isNaN(result.lineHeight));
        assertTrue(Float.isNaN(result.size));
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToTextStructure parser = new ToTextStructure(new JSONObject());
        assertEquals("TextStructure", parser.getModelClassName());
    }
}