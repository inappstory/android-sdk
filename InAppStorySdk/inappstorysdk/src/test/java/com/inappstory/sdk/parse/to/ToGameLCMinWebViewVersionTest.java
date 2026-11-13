package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameLCMinWebViewVersion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameLCMinWebViewVersionTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedModel_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameLCMinWebViewVersion.minVersion, 110);
        json.put(ModelFields.GameLCMinWebViewVersion.message, "Please update WebView");
        json.put(ModelFields.GameLCMinWebViewVersion.loggerMessage, "WebView version is too old");

        GameLCMinWebViewVersion result = new ToGameLCMinWebViewVersion(json).parse();

        assertNotNull(result);
        assertEquals(110, result.minVersion);
        assertEquals("Please update WebView", result.message);
        assertEquals("WebView version is too old", result.loggerMessage);
    }

    @Test
    public void parse_returnsEmptyStringsForOptionalFields_whenOnlyMinVersionProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameLCMinWebViewVersion.minVersion, 95);

        GameLCMinWebViewVersion result = new ToGameLCMinWebViewVersion(json).parse();

        assertNotNull(result);
        assertEquals(95, result.minVersion);
        assertEquals("", result.message);
        assertEquals("", result.loggerMessage);
    }

    // --- Обработка null и отсутствия обязательного minVersion ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToGameLCMinWebViewVersion(null).parse());
        assertNull(new ToGameLCMinWebViewVersion(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredMinVersionIsMissing() throws JSONException {
        JSONObject withoutMinVersion = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.message, "Update required");

        assertNull(new ToGameLCMinWebViewVersion(new JSONObject()).parse());
        assertNull(new ToGameLCMinWebViewVersion(withoutMinVersion).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenMinVersionHasInvalidDataType() throws JSONException {
        JSONObject stringVersion = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.minVersion, "not_an_int");
        JSONObject objectVersion = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.minVersion, new JSONObject());
        JSONObject arrayVersion = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.minVersion, new JSONArray());
        JSONObject nullVersion = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.minVersion, JSONObject.NULL);

        assertNull(new ToGameLCMinWebViewVersion(stringVersion).parse());
        assertNull(new ToGameLCMinWebViewVersion(objectVersion).parse());
        assertNull(new ToGameLCMinWebViewVersion(arrayVersion).parse());
        assertNull(new ToGameLCMinWebViewVersion(nullVersion).parse());
    }

    @Test
    public void parse_handlesNonStringTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameLCMinWebViewVersion.minVersion, 100);
        // В org.json метод optString(key) примитивы преобразует в строку
        json.put(ModelFields.GameLCMinWebViewVersion.message, 404);
        json.put(ModelFields.GameLCMinWebViewVersion.loggerMessage, false);

        GameLCMinWebViewVersion result = new ToGameLCMinWebViewVersion(json).parse();

        assertNotNull(result);
        assertEquals(100, result.minVersion);
        assertEquals("404", result.message);
        assertEquals("false", result.loggerMessage);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameLCMinWebViewVersion parser = new ToGameLCMinWebViewVersion(new JSONObject());

        assertEquals("GameLCMinWebViewVersion", parser.getModelClassName());
    }
}
