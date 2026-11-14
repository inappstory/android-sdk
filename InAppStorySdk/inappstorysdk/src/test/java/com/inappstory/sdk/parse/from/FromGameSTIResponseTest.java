package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameSTIResponse;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromGameSTIResponseTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsPopulated() throws Exception {
        GameSTIResponse model = new GameSTIResponse();
        model.id = "sti_id_789";
        model.response = "success_response_data";

        FromGameSTIResponse parser = new FromGameSTIResponse(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("sti_id_789", json.getString(ModelFields.GameSTIResponse.id));
        assertEquals("success_response_data", json.getString(ModelFields.GameSTIResponse.response));
    }

    // --- Негативные сценарии ---

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromGameSTIResponse parser = new FromGameSTIResponse(null);
        // Базовый класс ModelToStringParser перехватит NPE и вернет null
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesNullFields_whenModelHasNullValues() throws Exception {
        GameSTIResponse model = new GameSTIResponse();
        model.id = null;
        model.response = null;

        FromGameSTIResponse parser = new FromGameSTIResponse(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        // Оба поля null, поэтому ключи не добавляются в JSON
        assertFalse(json.has(ModelFields.GameSTIResponse.id));
        assertFalse(json.has(ModelFields.GameSTIResponse.response));
    }

    @Test
    public void parse_handlesEmptyStringFields() throws Exception {
        GameSTIResponse model = new GameSTIResponse();
        model.id = "";
        model.response = "";

        FromGameSTIResponse parser = new FromGameSTIResponse(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("", json.getString(ModelFields.GameSTIResponse.id));
        assertEquals("", json.getString(ModelFields.GameSTIResponse.response));
    }
}