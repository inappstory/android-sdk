package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.LayoutResponse;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToLayoutResponseTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedResponse_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.LayoutResponse.layout, "layout_v2");
        json.put(ModelFields.LayoutResponse.lastUpdatedAt, "2023-10-25T10:00:00Z");

        ToLayoutResponse parser = new ToLayoutResponse(json.toString());
        LayoutResponse result = parser.parse();

        assertNotNull(result);
        assertEquals("layout_v2", result.layout);
        assertEquals("2023-10-25T10:00:00Z", result.lastUpdatedAt);
    }

    @Test
    public void parse_returnsResponseWithNullLastUpdatedAt_whenKeyIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.LayoutResponse.layout, "layout_v2");

        ToLayoutResponse parser = new ToLayoutResponse(json.toString());
        LayoutResponse result = parser.parse();

        assertNotNull(result);
        assertEquals("layout_v2", result.layout);
        assertNull(result.lastUpdatedAt);
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredLayoutIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.LayoutResponse.lastUpdatedAt, "2023-10-25T10:00:00Z");

        assertNull(new ToLayoutResponse(json.toString()).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredLayoutHasInvalidDataType() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.LayoutResponse.layout, 12345); // Ожидается строка

        assertNull(new ToLayoutResponse(json.toString()).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToLayoutResponse((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToLayoutResponse("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToLayoutResponse("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypeInOptionalField_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.LayoutResponse.layout, "layout_v2");
        json.put(ModelFields.LayoutResponse.lastUpdatedAt, 12345); // Ожидается строка или null

        ToLayoutResponse parser = new ToLayoutResponse(json.toString());
        LayoutResponse result = parser.parse();

        assertNotNull(result);
        assertEquals("layout_v2", result.layout);
        assertNull(result.lastUpdatedAt);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToLayoutResponse parser = new ToLayoutResponse("{}");
        assertEquals("LayoutResponse", parser.getModelClassName());
    }
}