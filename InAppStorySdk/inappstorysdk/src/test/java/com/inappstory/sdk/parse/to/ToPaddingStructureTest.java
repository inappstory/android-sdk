package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.PaddingStructure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToPaddingStructureTest {

    @Test
    public void parse_returnsPopulatedPaddingStructure_whenValidJsonObjectProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.PaddingStructure.top, 10.5);
        json.put(ModelFields.PaddingStructure.bottom, 20.0);
        json.put(ModelFields.PaddingStructure.left, 15.25);
        json.put(ModelFields.PaddingStructure.right, 5.75);
        PaddingStructure result = new ToPaddingStructure(json).parse();
        assertNotNull(result);
        assertEquals(10.5f, result.top, 0.001f);
        assertEquals(20.0f, result.bottom, 0.001f);
        assertEquals(15.25f, result.left, 0.001f);
        assertEquals(5.75f, result.right, 0.001f);
    }

    @Test
    public void parse_usesDefaultNaNValues_whenKeysAreMissing() {
        JSONObject emptyJson = new JSONObject();
        PaddingStructure result = new ToPaddingStructure(emptyJson).parse();
        assertNotNull(result);
        assertTrue(Float.isNaN(result.top));
        assertTrue(Float.isNaN(result.bottom));
        assertTrue(Float.isNaN(result.left));
        assertTrue(Float.isNaN(result.right));
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        ToPaddingStructure parser = new ToPaddingStructure(null);
        assertNull(parser.parse());
    }

    @Test
    public void parseObject_and_invalidTypes_test() throws JSONException {
        ToPaddingStructure parser = new ToPaddingStructure(new JSONObject());
        assertNull(parser.parseObject(null));
        JSONObject json = new JSONObject();
        json.put(ModelFields.PaddingStructure.top, "not_a_float");
        json.put(ModelFields.PaddingStructure.right, JSONObject.NULL);
        PaddingStructure result = new ToPaddingStructure(json).parse();
        assertNotNull(result);
        assertTrue(Float.isNaN(result.top));
        assertTrue(Float.isNaN(result.right));
        assertEquals("PaddingStructure", parser.getModelClassName());
    }
}