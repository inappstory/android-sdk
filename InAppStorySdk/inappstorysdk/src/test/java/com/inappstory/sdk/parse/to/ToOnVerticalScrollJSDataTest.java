package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.OnVerticalScrollJSData;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToOnVerticalScrollJSDataTest {

    @Test
    public void parse_returnsPopulatedData_whenValidJsonStringProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.OnVerticalScrollJSData.scrollY, 125.5);
        json.put(ModelFields.OnVerticalScrollJSData.scrollPercent, 45.25);
        json.put(ModelFields.OnVerticalScrollJSData.isScrollableLayer, true);
        json.put(ModelFields.OnVerticalScrollJSData.cardId, 10);
        json.put(ModelFields.OnVerticalScrollJSData.slideIndex, 2);
        json.put(ModelFields.OnVerticalScrollJSData.layerIndex, 3);
        OnVerticalScrollJSData result = new ToOnVerticalScrollJSData(json.toString()).parse();
        assertNotNull(result);
        assertEquals(125.5f, result.scrollY, 0.001f);
        assertEquals(45.25f, result.scrollPercent, 0.001f);
        assertTrue(result.isScrollableLayer);
        assertEquals(10, result.cardId);
        assertEquals(2, result.slideIndex);
        assertEquals(3, result.layerIndex);
    }

    @Test
    public void parse_usesDefaultValues_whenEmptyJsonObjectProvided() {
        OnVerticalScrollJSData result = new ToOnVerticalScrollJSData("{}").parse();
        assertNotNull(result);
        assertTrue(Float.isNaN(result.scrollY));
        assertTrue(Float.isNaN(result.scrollPercent));
        assertFalse(result.isScrollableLayer);
        assertEquals(0, result.cardId);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.layerIndex);
    }

    @Test
    public void parse_returnsNull_whenStringIsMalformedEmptyOrNull() {
        assertNull(new ToOnVerticalScrollJSData("{invalid_json").parse());
        assertNull(new ToOnVerticalScrollJSData("").parse());
        assertNull(new ToOnVerticalScrollJSData(null).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypes_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.OnVerticalScrollJSData.scrollY, "not_a_float");
        json.put(ModelFields.OnVerticalScrollJSData.scrollPercent, new JSONObject());
        json.put(ModelFields.OnVerticalScrollJSData.isScrollableLayer, "not_a_boolean");
        json.put(ModelFields.OnVerticalScrollJSData.cardId, "not_an_int");
        json.put(ModelFields.OnVerticalScrollJSData.slideIndex, JSONObject.NULL);
        json.put(ModelFields.OnVerticalScrollJSData.layerIndex, true);
        OnVerticalScrollJSData result = new ToOnVerticalScrollJSData(json.toString()).parse();
        assertNotNull(result);
        assertTrue(Float.isNaN(result.scrollY));
        assertTrue(Float.isNaN(result.scrollPercent));
        assertFalse(result.isScrollableLayer);
        assertEquals(0, result.cardId);
        assertEquals(0, result.slideIndex);
        assertEquals(0, result.layerIndex);
    }

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToOnVerticalScrollJSData parser = new ToOnVerticalScrollJSData("{}");
        assertEquals("OnVerticalScrollJSData", parser.getModelClassName());
    }
}