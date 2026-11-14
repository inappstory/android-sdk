package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameDataPlaceholder;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromGameDataPlaceholderTest {

    @Test
    public void parseToJSONObject_returnsValidJsonObject_whenModelIsPopulated() throws Exception {
        GameDataPlaceholder model = new GameDataPlaceholder();
        model.name = "test_placeholder";
        model.type = "string";
        model.value = "test_value";

        FromGameDataPlaceholder parser = new FromGameDataPlaceholder(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals("test_placeholder", result.getString(ModelFields.GameDataPlaceholder.name));
        assertEquals("string", result.getString(ModelFields.GameDataPlaceholder.type));
        assertEquals("test_value", result.getString(ModelFields.GameDataPlaceholder.value));
    }

    // --- Негативные сценарии ---

    @Test(expected = NullPointerException.class)
    public void parseToJSONObject_throwsNPE_whenModelIsNull() throws Exception {
        FromGameDataPlaceholder parser = new FromGameDataPlaceholder(null);
        // ModelToJSONObjectParser не имеет try-catch в parseToJSONObject(), 
        // поэтому NPE пробросится наружу
        parser.parseToJSONObject();
    }

    @Test
    public void parseToJSONObject_handlesNullFields_whenModelHasNullValues() throws Exception {
        GameDataPlaceholder model = new GameDataPlaceholder();
        model.name = null;
        model.type = null;
        model.value = null;

        FromGameDataPlaceholder parser = new FromGameDataPlaceholder(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        // JSONObject.put() с null значением не добавляет ключ
        assertFalse(result.has(ModelFields.GameDataPlaceholder.name));
        assertFalse(result.has(ModelFields.GameDataPlaceholder.type));
        assertFalse(result.has(ModelFields.GameDataPlaceholder.value));
    }

    @Test
    public void parseToJSONObject_handlesEmptyStringFields() throws Exception {
        GameDataPlaceholder model = new GameDataPlaceholder();
        model.name = "";
        model.type = "";
        model.value = "";

        FromGameDataPlaceholder parser = new FromGameDataPlaceholder(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals("", result.getString(ModelFields.GameDataPlaceholder.name));
        assertEquals("", result.getString(ModelFields.GameDataPlaceholder.type));
        assertEquals("", result.getString(ModelFields.GameDataPlaceholder.value));
    }
}