package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameLaunchConditions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameLaunchConditionsTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedConditions_whenValidMinWebViewVersionProvided() throws JSONException {
        JSONObject webViewJson = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.minVersion, 120)
                .put(ModelFields.GameLCMinWebViewVersion.message, "Upgrade WebView");

        JSONObject json = new JSONObject();
        json.put(ModelFields.GameLaunchConditions.minWebViewVersion, webViewJson);

        GameLaunchConditions result = new ToGameLaunchConditions(json).parse();

        assertNotNull(result);
        assertNotNull(result.minWebViewVersion);
        assertEquals(120, result.minWebViewVersion.minVersion);
        assertEquals("Upgrade WebView", result.minWebViewVersion.message);
    }

    @Test
    public void parse_returnsConditionsWithNullField_whenJsonIsEmpty() {
        GameLaunchConditions result = new ToGameLaunchConditions(new JSONObject()).parse();

        assertNotNull(result);
        assertNull(result.minWebViewVersion);
    }

    // --- Обработка null ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToGameLaunchConditions(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToGameLaunchConditions parser = new ToGameLaunchConditions(new JSONObject());

        assertNull(parser.parseObject(null));
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsMinWebViewVersionToNull_whenFieldHasInvalidDataType() throws JSONException {
        JSONObject stringField = new JSONObject()
                .put(ModelFields.GameLaunchConditions.minWebViewVersion, "not_an_object");
        JSONObject intField = new JSONObject()
                .put(ModelFields.GameLaunchConditions.minWebViewVersion, 123);
        JSONObject arrayField = new JSONObject()
                .put(ModelFields.GameLaunchConditions.minWebViewVersion, new JSONArray());
        JSONObject nullField = new JSONObject()
                .put(ModelFields.GameLaunchConditions.minWebViewVersion, JSONObject.NULL);

        GameLaunchConditions res1 = new ToGameLaunchConditions(stringField).parse();
        GameLaunchConditions res2 = new ToGameLaunchConditions(intField).parse();
        GameLaunchConditions res3 = new ToGameLaunchConditions(arrayField).parse();
        GameLaunchConditions res4 = new ToGameLaunchConditions(nullField).parse();

        assertNotNull(res1);
        assertNull(res1.minWebViewVersion);

        assertNotNull(res2);
        assertNull(res2.minWebViewVersion);

        assertNotNull(res3);
        assertNull(res3.minWebViewVersion);

        assertNotNull(res4);
        assertNull(res4.minWebViewVersion);
    }

    @Test
    public void parse_setsMinWebViewVersionToNull_whenNestedObjectMissesMinVersionOrHasInvalidType() throws JSONException {
        JSONObject missingMinVersion = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.message, "Only message");

        JSONObject invalidMinVersion = new JSONObject()
                .put(ModelFields.GameLCMinWebViewVersion.minVersion, "not_an_int");

        JSONObject json1 = new JSONObject()
                .put(ModelFields.GameLaunchConditions.minWebViewVersion, missingMinVersion);
        JSONObject json2 = new JSONObject()
                .put(ModelFields.GameLaunchConditions.minWebViewVersion, invalidMinVersion);

        GameLaunchConditions res1 = new ToGameLaunchConditions(json1).parse();
        GameLaunchConditions res2 = new ToGameLaunchConditions(json2).parse();

        assertNotNull(res1);
        assertNull(res1.minWebViewVersion);

        assertNotNull(res2);
        assertNull(res2.minWebViewVersion);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameLaunchConditions parser = new ToGameLaunchConditions(new JSONObject());

        assertEquals("GameLaunchConditions", parser.getModelClassName());
    }
}
