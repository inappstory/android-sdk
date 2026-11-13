package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.BorderStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToBorderStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedBorderStructure_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.BorderStructure.radius, 12.5);
        json.put(ModelFields.BorderStructure.width, 2.0);
        json.put(ModelFields.BorderStructure.color, "#00FF00");

        BorderStructure result = new ToBorderStructure(json).parse();

        assertNotNull(result);
        assertEquals(12.5f, result.radius, 0.001f);
        assertEquals(2.0f, result.width, 0.001f);
        assertEquals("#00FF00", result.color);
    }

    @Test
    public void parse_returnsDefaultValues_whenJsonIsEmpty() {
        BorderStructure result = new ToBorderStructure(new JSONObject()).parse();

        assertNotNull(result);
        assertTrue(Float.isNaN(result.radius));
        assertTrue(Float.isNaN(result.width));
        assertNull(result.color);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToBorderStructure(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToBorderStructure parser = new ToBorderStructure(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_handlesInvalidDataTypes_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Нечисловые строки и объекты вместо double для radius и width
        json.put(ModelFields.BorderStructure.radius, "not_a_float");
        json.put(ModelFields.BorderStructure.width, new JSONObject());
        // Число и массив вместо String для color
        json.put(ModelFields.BorderStructure.color, 987654);

        BorderStructure result = new ToBorderStructure(json).parse();

        assertNotNull(result);
        assertTrue(Float.isNaN(result.radius));
        assertTrue(Float.isNaN(result.width));
        assertNull(result.color);

        // Дополнительная проверка массива и строкового "null" для color
        JSONObject jsonWithArrayColor = new JSONObject();
        jsonWithArrayColor.put(ModelFields.BorderStructure.color, new JSONArray());
        assertNull(new ToBorderStructure(jsonWithArrayColor).parse().color);

        JSONObject jsonWithLiteralNull = new JSONObject();
        jsonWithLiteralNull.put(ModelFields.BorderStructure.color, " null ");
        assertNull(new ToBorderStructure(jsonWithLiteralNull).parse().color);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToBorderStructure parser = new ToBorderStructure(new JSONObject());

        assertEquals("BorderStructure", parser.getModelClassName());
    }
}