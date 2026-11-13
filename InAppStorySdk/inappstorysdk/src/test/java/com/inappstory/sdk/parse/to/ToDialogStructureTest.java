package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToDialogStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedDialogStructure_whenValidJsonProvided() throws JSONException {
        JSONObject configV2Json = new JSONObject()
                .put(ModelFields.ConfigV2.factor, 1.5);

        JSONObject json = new JSONObject();
        json.put(ModelFields.DialogStructure.configV2, configV2Json);
        json.put(ModelFields.DialogStructure.size, new JSONObject());

        DialogStructure result = new ToDialogStructure(json.toString()).parse();

        assertNotNull(result);
        assertNotNull(result.configV2);
        assertEquals(1.5f, result.configV2.factor, 0.001f);
        assertNotNull(result.size);
    }

    @Test
    public void parse_returnsStructureWithNullFields_whenJsonIsEmpty() {
        DialogStructure result = new ToDialogStructure("{}").parse();

        assertNotNull(result);
        assertNull(result.configV2);
        assertNull(result.size);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToDialogStructure(null).parse());
        assertNull(new ToDialogStructure("").parse());
        assertNull(new ToDialogStructure("{broken_json").parse());
        // Массив на верхнем уровне вместо JSONObject
        assertNull(new ToDialogStructure("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenNestedFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        // Строка, число и массив вместо JSONObject
        json.put(ModelFields.DialogStructure.configV2, "not_an_object");
        json.put(ModelFields.DialogStructure.size, 12345);

        DialogStructure result = new ToDialogStructure(json.toString()).parse();

        assertNotNull(result);
        assertNull(result.configV2);
        assertNull(result.size);

        // Проверка JSONArray и JSONObject.NULL
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.DialogStructure.configV2, new JSONArray());
        json2.put(ModelFields.DialogStructure.size, JSONObject.NULL);

        DialogStructure result2 = new ToDialogStructure(json2.toString()).parse();

        assertNotNull(result2);
        assertNull(result2.configV2);
        assertNull(result2.size);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToDialogStructure parser = new ToDialogStructure("{}");

        assertEquals("DialogStructure", parser.getModelClassName());
    }
}
