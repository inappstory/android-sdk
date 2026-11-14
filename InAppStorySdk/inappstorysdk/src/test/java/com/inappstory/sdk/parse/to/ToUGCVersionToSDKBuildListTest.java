package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.UGCVersionToSDKBuild;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToUGCVersionToSDKBuildListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsBuildList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject build1 = new JSONObject();
        build1.put(ModelFields.UGCVersionToSDKBuild.minBuild, 10000);
        build1.put(ModelFields.UGCVersionToSDKBuild.editor, "v1.0.0");

        JSONObject build2 = new JSONObject();
        build2.put(ModelFields.UGCVersionToSDKBuild.minBuild, 10500);
        build2.put(ModelFields.UGCVersionToSDKBuild.editor, "v1.1.0");

        JSONArray array = new JSONArray();
        array.put(build1);
        array.put(build2);

        List<UGCVersionToSDKBuild> result = new ToUGCVersionToSDKBuildList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(10000, result.get(0).minBuild);
        assertEquals("v1.0.0", result.get(0).editor);

        assertEquals(10500, result.get(1).minBuild);
        assertEquals("v1.1.0", result.get(1).editor);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<UGCVersionToSDKBuild> result = new ToUGCVersionToSDKBuildList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToUGCVersionToSDKBuildList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.UGCVersionToSDKBuild.minBuild, 10000));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        // Наличие не-JSONObject элемента в массиве приводит к возврату null для всего списка
        assertNull(new ToUGCVersionToSDKBuildList(arrayWithString).parse());
        assertNull(new ToUGCVersionToSDKBuildList(arrayWithNumber).parse());
        assertNull(new ToUGCVersionToSDKBuildList(arrayWithNestedArray).parse());
        assertNull(new ToUGCVersionToSDKBuildList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных данных внутри элементов (без краша) ---

    @Test
    public void parse_ignoresInvalidTypesInOptionalFields_andUsesDefaults() throws JSONException {
        JSONObject buildWithBadFields = new JSONObject();
        buildWithBadFields.put(ModelFields.UGCVersionToSDKBuild.minBuild, "not_an_int");
        buildWithBadFields.put(ModelFields.UGCVersionToSDKBuild.editor, new JSONObject());

        JSONArray array = new JSONArray().put(buildWithBadFields);

        List<UGCVersionToSDKBuild> result = new ToUGCVersionToSDKBuildList(array).parse();

        // Поскольку поля не являются строго обязательными (используются opt-методы), 
        // элемент успешно создается, а невалидные поля получают значения по умолчанию.
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).minBuild);
        assertNull(result.get(0).editor);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToUGCVersionToSDKBuildParser() throws JSONException {
        ToUGCVersionToSDKBuildList listParser = new ToUGCVersionToSDKBuildList(new JSONArray());
        JSONObject buildJson = new JSONObject()
                .put(ModelFields.UGCVersionToSDKBuild.minBuild, 9999)
                .put(ModelFields.UGCVersionToSDKBuild.editor, "v3.0.0");

        ToUGCVersionToSDKBuild itemParser = listParser.createParserInstance(buildJson);

        assertNotNull(itemParser);
        UGCVersionToSDKBuild parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals(9999, parsed.minBuild);
        assertEquals("v3.0.0", parsed.editor);
    }
}