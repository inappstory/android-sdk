package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.CenterStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToCenterStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedCenterStructure_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.CenterStructure.x, 120.5);
        json.put(ModelFields.CenterStructure.y, 240.25);

        CenterStructure result = new ToCenterStructure(json).parse();

        assertNotNull(result);
        assertEquals(120.5f, result.x, 0.001f);
        assertEquals(240.25f, result.y, 0.001f);
    }

    @Test
    public void parse_returnsNaNCoordinates_whenJsonIsEmpty() {
        CenterStructure result = new ToCenterStructure(new JSONObject()).parse();

        assertNotNull(result);
        assertTrue(Float.isNaN(result.x));
        assertTrue(Float.isNaN(result.y));
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToCenterStructure(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToCenterStructure parser = new ToCenterStructure(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNaNCoordinates_whenFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.CenterStructure.x, "not_a_float");
        json.put(ModelFields.CenterStructure.y, new JSONObject().put("nested", 1));

        CenterStructure result = new ToCenterStructure(json).parse();

        assertNotNull(result);
        assertTrue(Float.isNaN(result.x));
        assertTrue(Float.isNaN(result.y));

        // Проверка массива и boolean вместо числа
        JSONObject jsonWithArrayAndBool = new JSONObject();
        jsonWithArrayAndBool.put(ModelFields.CenterStructure.x, new JSONArray());
        jsonWithArrayAndBool.put(ModelFields.CenterStructure.y, true);

        CenterStructure res2 = new ToCenterStructure(jsonWithArrayAndBool).parse();
        assertNotNull(res2);
        assertTrue(Float.isNaN(res2.x));
        assertTrue(Float.isNaN(res2.y));
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToCenterStructure parser = new ToCenterStructure(new JSONObject());

        assertEquals("CenterStructure", parser.getModelClassName());
    }
}