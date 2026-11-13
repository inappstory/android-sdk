package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.game.reader.GameAcceleratorOptions;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameAcceleratorOptionsTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedOptions_whenValidFrequencyProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameAcceleratorOptions.frequency, 60.0);

        GameAcceleratorOptions result = new ToGameAcceleratorOptions(json.toString()).parse();

        assertNotNull(result);
        assertEquals(60.0, result.frequency, 0.001);
    }

    @Test
    public void parse_returnsNaNFrequency_whenJsonIsEmpty() {
        GameAcceleratorOptions result = new ToGameAcceleratorOptions("{}").parse();

        assertNotNull(result);
        assertTrue(Double.isNaN(result.frequency));
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToGameAcceleratorOptions(null).parse());
        assertNull(new ToGameAcceleratorOptions("").parse());
        assertNull(new ToGameAcceleratorOptions("{broken_json").parse());
        assertNull(new ToGameAcceleratorOptions("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNaNFrequency_whenFrequencyHasInvalidDataType() throws JSONException {
        JSONObject stringFreq = new JSONObject()
                .put(ModelFields.GameAcceleratorOptions.frequency, "not_a_double");
        JSONObject objectFreq = new JSONObject()
                .put(ModelFields.GameAcceleratorOptions.frequency, new JSONObject());
        JSONObject arrayFreq = new JSONObject()
                .put(ModelFields.GameAcceleratorOptions.frequency, new JSONArray());
        JSONObject boolFreq = new JSONObject()
                .put(ModelFields.GameAcceleratorOptions.frequency, true);

        GameAcceleratorOptions resFromStr = new ToGameAcceleratorOptions(stringFreq.toString()).parse();
        GameAcceleratorOptions resFromObj = new ToGameAcceleratorOptions(objectFreq.toString()).parse();
        GameAcceleratorOptions resFromArr = new ToGameAcceleratorOptions(arrayFreq.toString()).parse();
        GameAcceleratorOptions resFromBool = new ToGameAcceleratorOptions(boolFreq.toString()).parse();

        assertNotNull(resFromStr);
        assertTrue(Double.isNaN(resFromStr.frequency));

        assertNotNull(resFromObj);
        assertTrue(Double.isNaN(resFromObj.frequency));

        assertNotNull(resFromArr);
        assertTrue(Double.isNaN(resFromArr.frequency));

        assertNotNull(resFromBool);
        assertTrue(Double.isNaN(resFromBool.frequency));
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameAcceleratorOptions parser = new ToGameAcceleratorOptions("{}");

        assertEquals("GameAcceleratorOptions", parser.getModelClassName());
    }
}
