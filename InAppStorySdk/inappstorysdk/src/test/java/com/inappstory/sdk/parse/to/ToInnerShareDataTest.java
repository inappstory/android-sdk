package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToInnerShareDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedShareData_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InnerShareData.text, "Share this!");
        json.put(ModelFields.InnerShareData.url, "https://example.com");
        json.put(ModelFields.InnerShareData.title, "My Title");
        json.put(ModelFields.InnerShareData.payload, "{\"key\":\"value\"}");

        ToInnerShareData parser = new ToInnerShareData(json.toString());
        InnerShareData result = parser.parse();

        assertNotNull(result);
        assertEquals("Share this!", result.text);
        assertEquals("https://example.com", result.url);
        assertEquals("My Title", result.title);
        assertEquals("{\"key\":\"value\"}", result.payload);
    }

    @Test
    public void parse_returnsShareDataWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToInnerShareData parser = new ToInnerShareData(json.toString());
        InnerShareData result = parser.parse();

        assertNotNull(result);
        assertNull(result.text);
        assertNull(result.url);
        assertNull(result.title);
        assertNull(result.payload);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToInnerShareData((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToInnerShareData("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToInnerShareData("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.InnerShareData.text, 123);
        json.put(ModelFields.InnerShareData.url, new JSONObject());
        json.put(ModelFields.InnerShareData.title, new JSONArray());
        json.put(ModelFields.InnerShareData.payload, true);

        ToInnerShareData parser = new ToInnerShareData(json.toString());
        InnerShareData result = parser.parse();

        assertNotNull(result);
        assertNull(result.text);
        assertNull(result.url);
        assertNull(result.title);
        assertNull(result.payload);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToInnerShareData parser = new ToInnerShareData("{}");
        assertEquals("InnerShareData", parser.getModelClassName());
    }
}