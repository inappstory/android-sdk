package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.ButtonStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToButtonStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedButtonStructure_whenValidNestedObjectsProvided() throws JSONException {
        JSONObject borderJson = new JSONObject()
                .put(ModelFields.BorderStructure.color, "#112233")
                .put(ModelFields.BorderStructure.width, 1.5);

        JSONObject backgroundJson = new JSONObject()
                .put(ModelFields.BackgroundStructure.color, "#AABBCC");

        JSONObject json = new JSONObject();
        json.put(ModelFields.ButtonStructure.text, new JSONObject());
        json.put(ModelFields.ButtonStructure.border, borderJson);
        json.put(ModelFields.ButtonStructure.background, backgroundJson);
        json.put(ModelFields.ButtonStructure.padding, new JSONObject());

        ButtonStructure result = new ToButtonStructure(json).parse();

        assertNotNull(result);
        assertNotNull(result.text);
        assertNotNull(result.border);
        assertEquals("#112233", result.border.color);
        assertEquals(1.5f, result.border.width, 0.001f);
        assertNotNull(result.background);
        assertEquals("#AABBCC", result.background.color);
        assertNotNull(result.padding);
    }

    @Test
    public void parse_returnsStructureWithNullFields_whenJsonIsEmpty() {
        ButtonStructure result = new ToButtonStructure(new JSONObject()).parse();

        assertNotNull(result);
        assertNull(result.text);
        assertNull(result.border);
        assertNull(result.background);
        assertNull(result.padding);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToButtonStructure(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToButtonStructure parser = new ToButtonStructure(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsNestedStructuresToNull_whenFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаём строку, число, массив и строковый "null" вместо JSONObject
        json.put(ModelFields.ButtonStructure.text, "not_an_object");
        json.put(ModelFields.ButtonStructure.border, 12345);
        json.put(ModelFields.ButtonStructure.background, new JSONArray());
        json.put(ModelFields.ButtonStructure.padding, " null ");

        ButtonStructure result = new ToButtonStructure(json).parse();

        assertNotNull(result);
        assertNull(result.text);
        assertNull(result.border);
        assertNull(result.background);
        assertNull(result.padding);
    }

    @Test
    public void parse_handlesInvalidDataTypesInsideNestedObjects() throws JSONException {
        JSONObject badBorder = new JSONObject()
                .put(ModelFields.BorderStructure.color, 999)
                .put(ModelFields.BorderStructure.width, "bad_float");

        JSONObject badBackground = new JSONObject()
                .put(ModelFields.BackgroundStructure.color, new JSONArray());

        JSONObject json = new JSONObject();
        json.put(ModelFields.ButtonStructure.border, badBorder);
        json.put(ModelFields.ButtonStructure.background, badBackground);

        ButtonStructure result = new ToButtonStructure(json).parse();

        assertNotNull(result);
        assertNotNull(result.border);
        assertNull(result.border.color);
        assertNotNull(result.background);
        assertNull(result.background.color);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToButtonStructure parser = new ToButtonStructure(new JSONObject());

        assertEquals("ButtonStructure", parser.getModelClassName());
    }
}