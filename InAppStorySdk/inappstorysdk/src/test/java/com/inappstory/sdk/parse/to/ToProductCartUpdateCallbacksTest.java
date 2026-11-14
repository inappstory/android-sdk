package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.goods.models.ProductCartUpdateCallbacks;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToProductCartUpdateCallbacksTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedCallbacks_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ProductCartUpdateCallbacks.errorCb, "onError");
        json.put(ModelFields.ProductCartUpdateCallbacks.requestId, "req_999");
        json.put(ModelFields.ProductCartUpdateCallbacks.successCb, "onSuccess");

        ToProductCartUpdateCallbacks parser = new ToProductCartUpdateCallbacks(json.toString());
        ProductCartUpdateCallbacks result = parser.parse();

        assertNotNull(result);
        assertEquals("onError", result.errorCb);
        assertEquals("req_999", result.requestId);
        assertEquals("onSuccess", result.successCb);
    }

    @Test
    public void parse_returnsCallbacksWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToProductCartUpdateCallbacks parser = new ToProductCartUpdateCallbacks(json.toString());
        ProductCartUpdateCallbacks result = parser.parse();

        assertNotNull(result);
        assertNull(result.errorCb);
        assertNull(result.requestId);
        assertNull(result.successCb);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToProductCartUpdateCallbacks((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToProductCartUpdateCallbacks("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToProductCartUpdateCallbacks("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ProductCartUpdateCallbacks.errorCb, 123);
        json.put(ModelFields.ProductCartUpdateCallbacks.requestId, new JSONObject());
        json.put(ModelFields.ProductCartUpdateCallbacks.successCb, new JSONArray());

        ToProductCartUpdateCallbacks parser = new ToProductCartUpdateCallbacks(json.toString());
        ProductCartUpdateCallbacks result = parser.parse();

        assertNotNull(result);
        assertNull(result.errorCb);
        assertNull(result.requestId);
        assertNull(result.successCb);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToProductCartUpdateCallbacks parser = new ToProductCartUpdateCallbacks("{}");
        assertEquals("ProductCartUpdateCallbacks", parser.getModelClassName());
    }
}