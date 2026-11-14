package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToSessionAssetListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsAssetList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject asset1 = new JSONObject();
        asset1.put(ModelFields.SessionAsset.url, "https://example.com/1.zip");
        asset1.put(ModelFields.SessionAsset.size, 100L);

        JSONObject asset2 = new JSONObject();
        asset2.put(ModelFields.SessionAsset.url, "https://example.com/2.zip");
        asset2.put(ModelFields.SessionAsset.type, "video");

        JSONArray array = new JSONArray();
        array.put(asset1);
        array.put(asset2);

        List<SessionAsset> result = new ToSessionAssetList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("https://example.com/1.zip", result.get(0).url);
        assertEquals(100L, result.get(0).size);
        assertEquals("https://example.com/2.zip", result.get(1).url);
        assertEquals("video", result.get(1).type);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<SessionAsset> result = new ToSessionAssetList(new JSONArray()).parse();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToSessionAssetList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.SessionAsset.url, "valid"));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);

        assertNull(new ToSessionAssetList(arrayWithString).parse());
        assertNull(new ToSessionAssetList(arrayWithNumber).parse());
    }

    @Test
    public void parse_returnsNull_whenAnyAssetMissingRequiredUrl() throws JSONException {
        JSONObject validAsset = new JSONObject().put(ModelFields.SessionAsset.url, "valid_url");
        JSONObject invalidAsset = new JSONObject().put(ModelFields.SessionAsset.type, "image"); // Нет url

        JSONArray array = new JSONArray();
        array.put(validAsset);
        array.put(invalidAsset);

        assertNull(new ToSessionAssetList(array).parse());
    }

    @Test
    public void parse_ignoresInvalidTypesInOptionalAssetFields() throws JSONException {
        JSONObject assetWithBadOptionalFields = new JSONObject();
        assetWithBadOptionalFields.put(ModelFields.SessionAsset.url, "valid_url");
        assetWithBadOptionalFields.put(ModelFields.SessionAsset.size, "not_a_long");
        assetWithBadOptionalFields.put(ModelFields.SessionAsset.type, 123);

        JSONArray array = new JSONArray().put(assetWithBadOptionalFields);

        List<SessionAsset> result = new ToSessionAssetList(array).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("valid_url", result.get(0).url);
        assertEquals(0L, result.get(0).size);
        assertNull(result.get(0).type);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToSessionAssetParser() throws JSONException {
        ToSessionAssetList listParser = new ToSessionAssetList(new JSONArray());
        JSONObject assetJson = new JSONObject().put(ModelFields.SessionAsset.url, "test_url");

        ToSessionAsset itemParser = listParser.createParserInstance(assetJson);

        assertNotNull(itemParser);
        SessionAsset parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals("test_url", parsed.url);
    }
}