package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.ConfigV2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToConfigV2Test {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedConfigV2_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ConfigV2.main, new JSONObject());
        json.put(ModelFields.ConfigV2.factor, 1.75);

        ConfigV2 result = new ToConfigV2(json).parse();

        assertNotNull(result);
        assertNotNull(result.main);
        assertEquals(1.75f, result.factor, 0.001f);
    }

    @Test
    public void parse_returnsDefaultValues_whenJsonIsEmpty() {
        ConfigV2 result = new ToConfigV2(new JSONObject()).parse();

        assertNotNull(result);
        assertNull(result.main);
        assertTrue(Float.isNaN(result.factor));
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToConfigV2(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToConfigV2 parser = new ToConfigV2(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_handlesInvalidDataTypes_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Строка вместо JSONObject для main, массив вместо числа для factor
        json.put(ModelFields.ConfigV2.main, "not_a_json_object");
        json.put(ModelFields.ConfigV2.factor, new JSONArray());

        ConfigV2 result = new ToConfigV2(json).parse();

        assertNotNull(result);
        assertNull(result.main);
        assertTrue(Float.isNaN(result.factor));

        // Число вместо JSONObject для main, нечисловая строка для factor
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.ConfigV2.main, 42);
        json2.put(ModelFields.ConfigV2.factor, "invalid_float");

        ConfigV2 result2 = new ToConfigV2(json2).parse();

        assertNotNull(result2);
        assertNull(result2.main);
        assertTrue(Float.isNaN(result2.factor));
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToConfigV2 parser = new ToConfigV2(new JSONObject());

        assertEquals("ConfigV2", parser.getModelClassName());
    }
}