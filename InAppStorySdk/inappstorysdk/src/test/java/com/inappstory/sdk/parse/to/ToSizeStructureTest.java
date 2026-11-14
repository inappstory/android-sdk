package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.SizeStructure;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSizeStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedSizeStructure_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SizeStructure.width, 100.5);
        json.put(ModelFields.SizeStructure.height, 200.0);
        json.put(ModelFields.SizeStructure.left, 10.0);
        
        JSONObject centerJson = new JSONObject();
        // Предполагаем, что ToCenterStructure парсит какие-то поля, передаем валидный объект
        centerJson.put("x", 50); 
        json.put(ModelFields.SizeStructure.center, centerJson);

        ToSizeStructure parser = new ToSizeStructure(json);
        SizeStructure result = parser.parse();

        assertNotNull(result);
        assertEquals(100.5f, result.width, 0.001f);
        assertEquals(200.0f, result.height, 0.001f);
        assertEquals(10.0f, result.left, 0.001f);
        assertNotNull(result.center); // Делегирование в ToCenterStructure сработало
    }

    @Test
    public void parse_returnsSizeStructureWithNaNValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToSizeStructure parser = new ToSizeStructure(json);
        SizeStructure result = parser.parse();

        assertNotNull(result);
        // optDouble возвращает NaN, если ключ отсутствует, приведение к float дает Float.NaN
        assertTrue(Float.isNaN(result.width));
        assertTrue(Float.isNaN(result.height));
        assertTrue(Float.isNaN(result.left));
        assertNull(result.center);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToSizeStructure(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToSizeStructure parser = new ToSizeStructure(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных для числовых полей
        json.put(ModelFields.SizeStructure.width, "not_a_double");
        json.put(ModelFields.SizeStructure.height, new JSONObject());
        json.put(ModelFields.SizeStructure.left, "not_a_double");
        // Передаем невалидный тип для вложенного объекта (строку вместо объекта)
        json.put(ModelFields.SizeStructure.center, "not_an_object");

        ToSizeStructure parser = new ToSizeStructure(json);
        SizeStructure result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertTrue(Float.isNaN(result.width));
        assertTrue(Float.isNaN(result.height));
        assertTrue(Float.isNaN(result.left));
        assertNull(result.center); // optJSONObject вернет null для строки, ToCenterStructure обработает null
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSizeStructure parser = new ToSizeStructure(new JSONObject());
        assertEquals("SizeStructure", parser.getModelClassName());
    }
}