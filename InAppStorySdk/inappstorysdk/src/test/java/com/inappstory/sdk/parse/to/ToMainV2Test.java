package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.MainV2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToMainV2Test {
    @Test
    public void parse_returnsPopulatedMainV2_whenAllNestedObjectsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.MainV2.border, new JSONObject());
        json.put(ModelFields.MainV2.background, new JSONObject().put(ModelFields.BackgroundStructure.color, "#FFFFFF"));
        json.put(ModelFields.MainV2.padding, new JSONObject().put(ModelFields.PaddingStructure.top, 10.0));
        json.put(ModelFields.MainV2.input, new JSONObject());
        json.put(ModelFields.MainV2.button, new JSONObject());
        json.put(ModelFields.MainV2.question, new JSONObject());
        MainV2 result = new ToMainV2(json).parse();
        assertNotNull(result);
        assertNull(result.border);
        assertNotNull(result.background);
        assertEquals("#FFFFFF", result.background.color);
        assertNotNull(result.padding);
        assertEquals(10.0f, result.padding.top, 0.001f);
        assertNull(result.input);
        assertNull(result.button);
        assertNull(result.question);
    }

    @Test
    public void parse_returnsMainV2WithNullFields_whenEmptyJsonObjectProvided() {
        MainV2 result = new ToMainV2(new JSONObject()).parse();
        assertNotNull(result);
        assertNull(result.border);
        assertNull(result.background);
        assertNull(result.padding);
        assertNull(result.input);
        assertNull(result.button);
        assertNull(result.question);
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.MainV2.border, "not_an_object");
        json.put(ModelFields.MainV2.background, 12345);
        json.put(ModelFields.MainV2.padding, new JSONArray());
        json.put(ModelFields.MainV2.input, true);
        json.put(ModelFields.MainV2.button, JSONObject.NULL);
        json.put(ModelFields.MainV2.question, "invalid");
        MainV2 result = new ToMainV2(json).parse();
        assertNotNull(result);
        assertNull(result.border);
        assertNull(result.background);
        assertNull(result.padding);
        assertNull(result.input);
        assertNull(result.button);
        assertNull(result.question);
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        ToMainV2 parser = new ToMainV2(null);
        assertNull(parser.parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToMainV2 parser = new ToMainV2(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToMainV2 parser = new ToMainV2(new JSONObject());
        assertEquals("MainV2", parser.getModelClassName());
    }
}