package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameCenterData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameCenterDataTest {

    private JSONObject createMinimalValidJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameCenterData.url, "https://example.com");
        json.put(ModelFields.GameCenterData.initCode, "window.initGame();");
        return json;
    }

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedGameCenterData_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = createMinimalValidJson();
        json.put(ModelFields.GameCenterData.id, "game_42");
        json.put(ModelFields.GameCenterData.layoutDirection, "ltr");
        json.put(ModelFields.GameCenterData.loggerLevel, 2);
        json.put(ModelFields.GameCenterData.canTryReloadCount, 3);
        json.put(ModelFields.GameCenterData.archiveSize, 204800L);
        json.put(ModelFields.GameCenterData.archiveUncompressedSize, 512000L);
        json.put(ModelFields.GameCenterData.archiveSha1, "abcdef123456");

        JSONArray archiveItems = new JSONArray()
                .put(new JSONObject().put(ModelFields.GameArchiveItem.item, "index.html"));
        json.put(ModelFields.GameCenterData.archiveItems, archiveItems);
        json.put(ModelFields.GameCenterData.resources, new JSONArray());

        JSONObject splashScreenJson = new JSONObject()
                .put(ModelFields.GameSplashScreen.url, "https://example.com");
        JSONObject splashLandscapeJson = new JSONObject()
                .put(ModelFields.GameSplashScreen.url, "https://example.com");
        JSONObject splashAnimJson = new JSONObject()
                .put(ModelFields.GameSplashAnimation.url, "https://example.com");

        json.put(ModelFields.GameCenterData.splashScreen, splashScreenJson);
        json.put(ModelFields.GameCenterData.splashLandscapeScreen, splashLandscapeJson);
        json.put(ModelFields.GameCenterData.options, new JSONObject());
        json.put(ModelFields.GameCenterData.gameLaunchConditions, new JSONObject());
        json.put(ModelFields.GameCenterData.splashAnimation, splashAnimJson);
        json.put(ModelFields.GameCenterData.instanceUserData, new JSONObject().put("level", 5));

        GameCenterData result = new ToGameCenterData(json.toString()).parse();

        assertNotNull(result);
        assertEquals("game_42", result.id);
        assertEquals("ltr", result.layoutDirection);
        assertEquals(Integer.valueOf(2), result.loggerLevel);
        assertEquals("https://example.com", result.url);
        assertEquals("window.initGame();", result.initCode);
        assertEquals(Integer.valueOf(3), result.canTryReloadCount);
        assertEquals(Long.valueOf(204800L), result.archiveSize);
        assertEquals(Long.valueOf(512000L), result.archiveUncompressedSize);
        assertEquals("abcdef123456", result.archiveSha1);

        assertNotNull(result.archiveItems);
        assertEquals(1, result.archiveItems.size());
        assertEquals("index.html", result.archiveItems.get(0).item);

        assertNotNull(result.resources);
        assertTrue(result.resources.isEmpty());
        assertNotNull(result.splashScreen);
        assertNotNull(result.splashLandscapeScreen);
        assertNotNull(result.options);
        assertNotNull(result.gameLaunchConditions);
        assertNotNull(result.splashAnimation);
        assertNotNull(result.instanceUserData);
        assertEquals(5, result.instanceUserData.get("level"));
    }

    @Test
    public void parse_returnsModelWithNullOptionalFields_whenOnlyRequiredFieldsProvided() throws JSONException {
        JSONObject json = createMinimalValidJson();

        GameCenterData result = new ToGameCenterData(json.toString()).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals("window.initGame();", result.initCode);

        assertNull(result.id);
        assertNull(result.layoutDirection);
        assertNull(result.loggerLevel);
        assertNull(result.canTryReloadCount);
        assertNull(result.archiveSize);
        assertNull(result.archiveUncompressedSize);
        assertNull(result.archiveSha1);
        assertNull(result.archiveItems);
        assertNull(result.resources);
        assertNull(result.splashScreen);
        assertNull(result.splashLandscapeScreen);
        assertNull(result.options);
        assertNull(result.gameLaunchConditions);
        assertNull(result.splashAnimation);
    }

    // --- Проверка null, битого JSON и отсутствия обязательных полей ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToGameCenterData(null).parse());
        assertNull(new ToGameCenterData("").parse());
        assertNull(new ToGameCenterData("{broken_json").parse());
        assertNull(new ToGameCenterData("[]").parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlOrInitCodeIsMissing() throws JSONException {
        JSONObject onlyUrl = new JSONObject()
                .put(ModelFields.GameCenterData.url, "https://example.com");
        JSONObject onlyInitCode = new JSONObject()
                .put(ModelFields.GameCenterData.initCode, "init();");

        assertNull(new ToGameCenterData("{}").parse());
        assertNull(new ToGameCenterData(onlyUrl.toString()).parse());
        assertNull(new ToGameCenterData(onlyInitCode.toString()).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenRequiredFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject intUrl = createMinimalValidJson()
                .put(ModelFields.GameCenterData.url, 12345);
        JSONObject objUrl = createMinimalValidJson()
                .put(ModelFields.GameCenterData.url, new JSONObject());
        JSONObject nullUrl = createMinimalValidJson()
                .put(ModelFields.GameCenterData.url, JSONObject.NULL);

        JSONObject intInitCode = createMinimalValidJson()
                .put(ModelFields.GameCenterData.initCode, 67890);
        JSONObject arrInitCode = createMinimalValidJson()
                .put(ModelFields.GameCenterData.initCode, new JSONArray());
        JSONObject nullInitCode = createMinimalValidJson()
                .put(ModelFields.GameCenterData.initCode, JSONObject.NULL);

        assertNull(new ToGameCenterData(intUrl.toString()).parse());
        assertNull(new ToGameCenterData(objUrl.toString()).parse());
        assertNull(new ToGameCenterData(nullUrl.toString()).parse());

        assertNull(new ToGameCenterData(intInitCode.toString()).parse());
        assertNull(new ToGameCenterData(arrInitCode.toString()).parse());
        assertNull(new ToGameCenterData(nullInitCode.toString()).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = createMinimalValidJson();

        // Невалидные типы для optStringNullable, optIntNullable, optLongNullable
        json.put(ModelFields.GameCenterData.id, 100);
        json.put(ModelFields.GameCenterData.layoutDirection, new JSONArray());
        json.put(ModelFields.GameCenterData.loggerLevel, "not_an_int");
        json.put(ModelFields.GameCenterData.canTryReloadCount, false);
        json.put(ModelFields.GameCenterData.archiveSize, "not_a_long");
        json.put(ModelFields.GameCenterData.archiveUncompressedSize, new JSONObject());
        json.put(ModelFields.GameCenterData.archiveSha1, " null ");

        // Строки и числа вместо JSONArray и JSONObject для вложенных структур
        json.put(ModelFields.GameCenterData.archiveItems, "not_an_array");
        json.put(ModelFields.GameCenterData.resources, 555);
        json.put(ModelFields.GameCenterData.splashScreen, "not_an_object");
        json.put(ModelFields.GameCenterData.splashLandscapeScreen, 111);
        json.put(ModelFields.GameCenterData.options, new JSONArray());
        json.put(ModelFields.GameCenterData.gameLaunchConditions, true);
        json.put(ModelFields.GameCenterData.splashAnimation, "not_an_object");
        json.put(ModelFields.GameCenterData.instanceUserData, "not_an_object");

        GameCenterData result = new ToGameCenterData(json.toString()).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals("window.initGame();", result.initCode);

        assertNull(result.id);
        assertNull(result.layoutDirection);
        assertNull(result.loggerLevel);
        assertNull(result.canTryReloadCount);
        assertNull(result.archiveSize);
        assertNull(result.archiveUncompressedSize);
        assertNull(result.archiveSha1);
        assertNull(result.archiveItems);
        assertNull(result.resources);
        assertNull(result.splashScreen);
        assertNull(result.splashLandscapeScreen);
        assertNull(result.options);
        assertNull(result.gameLaunchConditions);
        assertNull(result.splashAnimation);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameCenterData parser = new ToGameCenterData("{}");

        assertEquals("GameCenterData", parser.getModelClassName());
    }
}
