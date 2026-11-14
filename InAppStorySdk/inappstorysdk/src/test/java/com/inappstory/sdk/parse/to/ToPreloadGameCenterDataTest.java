package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToPreloadGameCenterDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedData_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.PreloadGameCenterData.id, "game_123");
        json.put(ModelFields.PreloadGameCenterData.url, "https://example.com/game.zip");
        json.put(ModelFields.PreloadGameCenterData.archiveSize, 1024L);
        json.put(ModelFields.PreloadGameCenterData.archiveUncompressedSize, 2048L);
        json.put(ModelFields.PreloadGameCenterData.archiveSha1, "sha1_hash");
        json.put(ModelFields.PreloadGameCenterData.archiveItems, new JSONArray());
        json.put(ModelFields.PreloadGameCenterData.resources, new JSONArray());
        json.put(ModelFields.PreloadGameCenterData.splashScreen, new JSONObject());
        json.put(ModelFields.PreloadGameCenterData.splashLandscapeScreen, new JSONObject());
        json.put(ModelFields.PreloadGameCenterData.splashAnimation, new JSONObject());

        ToPreloadGameCenterData parser = new ToPreloadGameCenterData(json);
        PreloadGameCenterData result = parser.parse();

        assertNotNull(result);
        assertEquals("game_123", result.id);
        assertEquals("https://example.com/game.zip", result.url);
        assertEquals(Long.valueOf(1024L), result.archiveSize);
        assertEquals(Long.valueOf(2048L), result.archiveUncompressedSize);
        assertEquals("sha1_hash", result.archiveSha1);
        assertNotNull(result.archiveItems);
        assertNotNull(result.resources);
        assertNull(result.splashScreen);
        assertNull(result.splashLandscapeScreen);
        assertNull(result.splashAnimation);
    }

    @Test
    public void parse_returnsDataWithDefaultValues_whenOnlyRequiredFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.PreloadGameCenterData.id, "game_123");
        json.put(ModelFields.PreloadGameCenterData.url, "https://example.com/game.zip");

        ToPreloadGameCenterData parser = new ToPreloadGameCenterData(json);
        PreloadGameCenterData result = parser.parse();

        assertNotNull(result);
        assertEquals("game_123", result.id);
        assertEquals("https://example.com/game.zip", result.url);
        assertNull(result.archiveSize);
        assertNull(result.archiveUncompressedSize);
        assertNull(result.archiveSha1);
        assertNull(result.archiveItems);
        assertNull(result.resources);
        assertNull(result.splashScreen);
        assertNull(result.splashLandscapeScreen);
        assertNull(result.splashAnimation);
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.PreloadGameCenterData.url, "https://example.com/game.zip");
        assertNull(new ToPreloadGameCenterData(json).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.PreloadGameCenterData.id, "game_123");
        assertNull(new ToPreloadGameCenterData(json).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToPreloadGameCenterData(null).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.PreloadGameCenterData.id, "game_123");
        json.put(ModelFields.PreloadGameCenterData.url, "https://example.com/game.zip");
        
        json.put(ModelFields.PreloadGameCenterData.archiveSize, "not_a_long");
        json.put(ModelFields.PreloadGameCenterData.archiveItems, "not_an_array");
        json.put(ModelFields.PreloadGameCenterData.splashScreen, "not_an_object");

        ToPreloadGameCenterData parser = new ToPreloadGameCenterData(json);
        PreloadGameCenterData result = parser.parse();

        assertNotNull(result);
        assertEquals("game_123", result.id);
        assertEquals("https://example.com/game.zip", result.url);
        assertNull(result.archiveSize);
        assertNull(result.archiveItems);
        assertNull(result.splashScreen);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToPreloadGameCenterData parser = new ToPreloadGameCenterData(new JSONObject());
        assertEquals("PreloadGameCenterData", parser.getModelClassName());
    }
}