package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameSplashScreenTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedSplashScreen_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSplashScreen.url, "https://example.com");
        json.put(ModelFields.GameSplashScreen.size, 102400L);
        json.put(ModelFields.GameSplashScreen.sha1, "fedcba9876543210");

        GameSplashScreen result = new ToGameSplashScreen(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals(Long.valueOf(102400L), result.size);
        assertEquals("fedcba9876543210", result.sha1);
    }

    @Test
    public void parse_returnsScreenWithNullOptionalFields_whenOnlyRequiredUrlProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSplashScreen.url, "https://example.com");

        GameSplashScreen result = new ToGameSplashScreen(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertNull(result.size);
        assertNull(result.sha1);
    }

    // --- Обработка null и отсутствия обязательного url ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToGameSplashScreen(null).parse());
        assertNull(new ToGameSplashScreen(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() {
        assertNull(new ToGameSplashScreen(new JSONObject()).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenRequiredUrlHasInvalidDataType() throws JSONException {
        JSONObject intUrl = new JSONObject().put(ModelFields.GameSplashScreen.url, 404);
        JSONObject objUrl = new JSONObject().put(ModelFields.GameSplashScreen.url, new JSONObject());
        JSONObject arrUrl = new JSONObject().put(ModelFields.GameSplashScreen.url, new JSONArray());
        JSONObject nullUrl = new JSONObject().put(ModelFields.GameSplashScreen.url, JSONObject.NULL);

        assertNull(new ToGameSplashScreen(intUrl).parse());
        assertNull(new ToGameSplashScreen(objUrl).parse());
        assertNull(new ToGameSplashScreen(arrUrl).parse());
        assertNull(new ToGameSplashScreen(nullUrl).parse());
    }

    @Test
    public void parse_setsOptionalFieldsToNull_whenOptionalFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSplashScreen.url, "https://example.com");
        json.put(ModelFields.GameSplashScreen.size, "not_a_long");
        json.put(ModelFields.GameSplashScreen.sha1, new JSONArray());

        GameSplashScreen result = new ToGameSplashScreen(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertNull(result.size);
        assertNull(result.sha1);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameSplashScreen parser = new ToGameSplashScreen(new JSONObject());

        assertEquals("GameSplashScreen", parser.getModelClassName());
    }
}
