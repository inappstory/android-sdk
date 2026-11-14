package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToUGCVersionToSDKBuildTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedBuild_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.UGCVersionToSDKBuild.minBuild, 10500);
        json.put(ModelFields.UGCVersionToSDKBuild.editor, "v2.5.0");

        ToUGCVersionToSDKBuild parser = new ToUGCVersionToSDKBuild(json);
        UGCVersionToSDKBuild result = parser.parse();

        assertNotNull(result);
        assertEquals(10500, result.minBuild);
        assertEquals("v2.5.0", result.editor);
    }

    @Test
    public void parse_returnsBuildWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToUGCVersionToSDKBuild parser = new ToUGCVersionToSDKBuild(json);
        UGCVersionToSDKBuild result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.minBuild); // optInt по умолчанию возвращает 0
        assertNull(result.editor);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        ToUGCVersionToSDKBuild parser = new ToUGCVersionToSDKBuild(null);
        assertNull(parser.parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToUGCVersionToSDKBuild parser = new ToUGCVersionToSDKBuild(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put(ModelFields.UGCVersionToSDKBuild.minBuild, "not_an_int");
        json.put(ModelFields.UGCVersionToSDKBuild.editor, 12345);

        ToUGCVersionToSDKBuild parser = new ToUGCVersionToSDKBuild(json);
        UGCVersionToSDKBuild result = parser.parse();

        // Благодаря opt-методам и блоку try-catch, парсинг не должен приводить к крашу
        assertNotNull(result);
        assertEquals(0, result.minBuild);
        assertNull(result.editor);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToUGCVersionToSDKBuild parser = new ToUGCVersionToSDKBuild(new JSONObject());
        assertEquals("UGCVersionToSDKBuild", parser.getModelClassName());
    }
}