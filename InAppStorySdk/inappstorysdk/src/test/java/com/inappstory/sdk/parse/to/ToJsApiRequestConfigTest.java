package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.network.jsapiclient.JsApiRequestConfig;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToJsApiRequestConfigTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedConfig_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.JsApiRequestConfig.cb, "callback_func");
        json.put(ModelFields.JsApiRequestConfig.id, "req_123");
        json.put(ModelFields.JsApiRequestConfig.data, "{\"key\":\"value\"}");
        json.put(ModelFields.JsApiRequestConfig.headers, "{\"Auth\":\"token\"}");
        json.put(ModelFields.JsApiRequestConfig.profilingKey, "prof_key");
        json.put(ModelFields.JsApiRequestConfig.url, "https://api.example.com");
        json.put(ModelFields.JsApiRequestConfig.method, "POST");
        json.put(ModelFields.JsApiRequestConfig.params, "param1=1");

        ToJsApiRequestConfig parser = new ToJsApiRequestConfig(json.toString());
        JsApiRequestConfig result = parser.parse();

        assertNotNull(result);
        assertEquals("callback_func", result.cb);
        assertEquals("req_123", result.id);
        assertEquals("{\"key\":\"value\"}", result.data);
        assertEquals("{\"Auth\":\"token\"}", result.headers);
        assertEquals("prof_key", result.profilingKey);
        assertEquals("https://api.example.com", result.url);
        assertEquals("POST", result.method);
        assertEquals("param1=1", result.params);
    }

    @Test
    public void parse_returnsConfigWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToJsApiRequestConfig parser = new ToJsApiRequestConfig(json.toString());
        JsApiRequestConfig result = parser.parse();

        assertNotNull(result);
        assertNull(result.cb);
        assertNull(result.id);
        assertNull(result.data);
        assertNull(result.headers);
        assertNull(result.profilingKey);
        assertNull(result.url);
        assertNull(result.method);
        assertNull(result.params);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToJsApiRequestConfig((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToJsApiRequestConfig("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToJsApiRequestConfig("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.JsApiRequestConfig.cb, 123);
        json.put(ModelFields.JsApiRequestConfig.id, new JSONObject());
        json.put(ModelFields.JsApiRequestConfig.data, new JSONArray());

        ToJsApiRequestConfig parser = new ToJsApiRequestConfig(json.toString());
        JsApiRequestConfig result = parser.parse();

        assertNotNull(result);
        assertNull(result.cb);
        assertNull(result.id);
        assertNull(result.data);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToJsApiRequestConfig parser = new ToJsApiRequestConfig("{}");
        assertEquals("JsApiRequestConfig", parser.getModelClassName());
    }
}