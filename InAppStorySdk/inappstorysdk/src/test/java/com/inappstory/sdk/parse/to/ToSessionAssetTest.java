package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSessionAssetTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedAsset_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SessionAsset.url, "https://example.com/asset.zip");
        json.put(ModelFields.SessionAsset.sha1, "a1b2c3");
        json.put(ModelFields.SessionAsset.type, "image");
        json.put(ModelFields.SessionAsset.replaceKey, "key_1");
        json.put(ModelFields.SessionAsset.filename, "asset.png");
        json.put(ModelFields.SessionAsset.format, "png");
        json.put(ModelFields.SessionAsset.mimeType, "image/png");
        json.put(ModelFields.SessionAsset.purpose, "story");
        json.put(ModelFields.SessionAsset.size, 2048L);

        ToSessionAsset parser = new ToSessionAsset(json);
        SessionAsset result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/asset.zip", result.url);
        assertEquals("a1b2c3", result.sha1);
        assertEquals("image", result.type);
        assertEquals("key_1", result.replaceKey);
        assertEquals("asset.png", result.filename);
        assertEquals("png", result.format);
        assertEquals("image/png", result.mimeType);
        assertEquals("story", result.purpose);
        assertEquals(2048L, result.size);
    }

    @Test
    public void parse_returnsAssetWithDefaultValues_whenOnlyRequiredUrlProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SessionAsset.url, "https://example.com/asset.zip");

        ToSessionAsset parser = new ToSessionAsset(json);
        SessionAsset result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/asset.zip", result.url);
        assertNull(result.sha1);
        assertNull(result.type);
        assertNull(result.replaceKey);
        assertNull(result.filename);
        assertNull(result.format);
        assertNull(result.mimeType);
        assertNull(result.purpose);
        assertEquals(0L, result.size); // optLong по умолчанию возвращает 0
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SessionAsset.type, "image");

        assertNull(new ToSessionAsset(json).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlHasInvalidDataType() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SessionAsset.url, 12345); // Ожидается строка

        assertNull(new ToSessionAsset(json).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToSessionAsset(null).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SessionAsset.url, "https://example.com/asset.zip");
        
        // Передаем невалидные типы для опциональных полей
        json.put(ModelFields.SessionAsset.sha1, 999);
        json.put(ModelFields.SessionAsset.type, new JSONObject());
        json.put(ModelFields.SessionAsset.size, "not_a_long");

        ToSessionAsset parser = new ToSessionAsset(json);
        SessionAsset result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/asset.zip", result.url);
        assertNull(result.sha1);
        assertNull(result.type);
        assertEquals(0L, result.size);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSessionAsset parser = new ToSessionAsset(new JSONObject());
        assertEquals("SessionAsset", parser.getModelClassName());
    }
}