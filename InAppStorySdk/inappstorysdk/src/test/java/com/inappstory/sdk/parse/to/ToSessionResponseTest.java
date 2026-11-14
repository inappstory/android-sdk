package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.SessionResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSessionResponseTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedResponse_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("is_allow_ugc", true);
        json.put("server_timestamp", 1700000000L);
        json.put("preloadGame", true);
        json.put("preview_aspect_ratio", 1.77);
        json.put("layout_updated_at", 9999);
        json.put("is_game_reader_default_fullscreen", true);
        json.put("is_allow_profiling", true);
        json.put("is_allow_statistic_v1", true);
        json.put("is_allow_statistic_v2", false);
        json.put("is_allow_crash", true);
        json.put("debug_session_key", "debug_key_123");
        
        json.put("placeholders", new JSONArray());
        json.put("image_placeholders", new JSONArray());
        json.put("cache", new JSONArray());
        
        JSONObject sessionJson = new JSONObject().put("id", "sess_1");
        json.put("session", sessionJson);

        ToSessionResponse parser = new ToSessionResponse(json.toString());
        SessionResponse result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isAllowUgc);
        assertEquals(Long.valueOf(1700000000L), result.serverTimestamp);
        assertTrue(result.preloadGame);
        assertEquals(1.77f, result.previewAspectRatio, 0.001f);
        assertEquals(9999, result.layoutUpdatedAt);
        assertTrue(result.isGameReaderDefaultFullscreen);
        assertTrue(result.isAllowProfiling);
        assertEquals(Boolean.TRUE, result.isAllowStatV1);
        assertEquals(Boolean.FALSE, result.isAllowStatV2);
        assertEquals(Boolean.TRUE, result.isAllowCrash);
        assertEquals("debug_key_123", result.debugSessionKey);
        
        assertNotNull(result.placeholders);
        assertNotNull(result.imagePlaceholders);
        assertNotNull(result.sessionAssets);
        assertNotNull(result.session);
        assertEquals("sess_1", result.session.id);
    }

    @Test
    public void parse_returnsResponseWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToSessionResponse parser = new ToSessionResponse(json.toString());
        SessionResponse result = parser.parse();

        assertNotNull(result);
        assertFalse(result.isAllowUgc); // optBoolean -> false
        assertNull(result.serverTimestamp); // optLongNullable -> null
        assertFalse(result.preloadGame);
        assertTrue(Float.isNaN(result.previewAspectRatio)); // optDouble -> NaN -> Float.NaN
        assertEquals(0, result.layoutUpdatedAt); // optInt -> 0
        assertFalse(result.isGameReaderDefaultFullscreen);
        assertFalse(result.isAllowProfiling);
        assertNull(result.isAllowStatV1); // optBooleanNullable -> null
        assertNull(result.isAllowStatV2);
        assertNull(result.isAllowCrash);
        assertNull(result.debugSessionKey);
        
        assertNull(result.placeholders);
        assertNull(result.imagePlaceholders);
        assertNull(result.sessionAssets);
        assertNull(result.session);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToSessionResponse((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToSessionResponse("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToSessionResponse("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put("is_allow_ugc", "not_a_boolean");
        json.put("server_timestamp", "not_a_long");
        json.put("preview_aspect_ratio", "not_a_double");
        json.put("layout_updated_at", "not_an_int");
        json.put("is_allow_statistic_v1", "not_a_boolean");
        json.put("debug_session_key", 123);
        
        // Передаем невалидные типы для вложенных списков и объектов
        json.put("placeholders", "not_an_array");
        json.put("cache", 123);
        json.put("session", "not_an_object");

        ToSessionResponse parser = new ToSessionResponse(json.toString());
        SessionResponse result = parser.parse();

        assertNotNull(result);
        assertFalse(result.isAllowUgc);
        assertNull(result.serverTimestamp);
        assertTrue(Float.isNaN(result.previewAspectRatio));
        assertEquals(0, result.layoutUpdatedAt);
        assertNull(result.isAllowStatV1);
        assertNull(result.debugSessionKey);
        
        assertNull(result.placeholders);
        assertNull(result.sessionAssets);
        assertNull(result.session);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSessionResponse parser = new ToSessionResponse("{}");
        assertEquals("SessionResponse", parser.getModelClassName());
    }
}