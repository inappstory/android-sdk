package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.BackgroundStructure;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToBackgroundStructureTest {

    @Test
    public void parse_returnsBackgroundStructureWithColor_whenValidColorProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.BackgroundStructure.color, "#FF00AA");

        ToBackgroundStructure parser = new ToBackgroundStructure(json);
        BackgroundStructure result = parser.parse();

        assertNotNull(result);
        assertEquals("#FF00AA", result.color);
    }

    @Test
    public void parse_returnsStructureWithNullColor_whenColorKeyIsMissing() {
        JSONObject emptyJson = new JSONObject();

        ToBackgroundStructure parser = new ToBackgroundStructure(emptyJson);
        BackgroundStructure result = parser.parse();

        assertNotNull(result);
        assertNull(result.color);
    }

    @Test
    public void parse_returnsStructureWithNullColor_whenColorIsLiteralNullOrJsonNull() throws JSONException {
        JSONObject literalNullJson = new JSONObject();
        literalNullJson.put(ModelFields.BackgroundStructure.color, " null ");

        JSONObject jsonNullObj = new JSONObject();
        jsonNullObj.put(ModelFields.BackgroundStructure.color, JSONObject.NULL);

        BackgroundStructure resFromLiteralNull = new ToBackgroundStructure(literalNullJson).parse();
        BackgroundStructure resFromJsonNull = new ToBackgroundStructure(jsonNullObj).parse();

        assertNotNull(resFromLiteralNull);
        assertNull(resFromLiteralNull.color);

        assertNotNull(resFromJsonNull);
        assertNull(resFromJsonNull.color);
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        ToBackgroundStructure parser = new ToBackgroundStructure(null);

        assertNull(parser.parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToBackgroundStructure parser = new ToBackgroundStructure(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypeForColor() throws JSONException {
        JSONObject intInsteadOfString = new JSONObject();
        intInsteadOfString.put(ModelFields.BackgroundStructure.color, 12345);

        JSONObject objectInsteadOfString = new JSONObject();
        objectInsteadOfString.put(ModelFields.BackgroundStructure.color, new JSONObject().put("nested", 1));

        BackgroundStructure resFromInt = new ToBackgroundStructure(intInsteadOfString).parse();
        BackgroundStructure resFromObj = new ToBackgroundStructure(objectInsteadOfString).parse();

        assertNotNull(resFromInt);
        assertNull(resFromInt.color);

        assertNotNull(resFromObj);
        assertNull(resFromObj.color);
    }

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToBackgroundStructure parser = new ToBackgroundStructure(new JSONObject());

        assertEquals("BackgroundStructure", parser.getModelClassName());
    }
}