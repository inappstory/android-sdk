package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToGameArchiveItemListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsGameArchiveItemList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject item1 = new JSONObject();
        item1.put(ModelFields.GameArchiveItem.item, "index.html");
        item1.put(ModelFields.GameArchiveItem.sha1, "hash1");

        JSONObject item2 = new JSONObject();
        item2.put(ModelFields.GameArchiveItem.item, "game.js");
        item2.put(ModelFields.GameArchiveItem.sha1, "hash2");

        JSONArray array = new JSONArray();
        array.put(item1);
        array.put(item2);

        List<GameArchiveItem> result = new ToGameArchiveItemList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("index.html", result.get(0).item);
        assertEquals("hash1", result.get(0).sha1);

        assertEquals("game.js", result.get(1).item);
        assertEquals("hash2", result.get(1).sha1);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<GameArchiveItem> result = new ToGameArchiveItemList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных типов элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToGameArchiveItemList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.GameArchiveItem.item, "index.html"));
        arrayWithString.put("invalid_string_element");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToGameArchiveItemList(arrayWithString).parse());
        assertNull(new ToGameArchiveItemList(arrayWithNumber).parse());
        assertNull(new ToGameArchiveItemList(arrayWithNestedArray).parse());
        assertNull(new ToGameArchiveItemList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных данных внутри элементов GameArchiveItem ---

    @Test
    public void parse_returnsNull_whenAnyItemMissingRequiredItemFieldOrHasInvalidType() throws JSONException {
        JSONObject validItem = new JSONObject()
                .put(ModelFields.GameArchiveItem.item, "index.html");

        JSONObject missingItemField = new JSONObject()
                .put(ModelFields.GameArchiveItem.sha1, "only_hash");

        JSONObject invalidTypeItemField = new JSONObject()
                .put(ModelFields.GameArchiveItem.item, 12345);

        assertNull(new ToGameArchiveItemList(new JSONArray().put(validItem).put(missingItemField)).parse());
        assertNull(new ToGameArchiveItemList(new JSONArray().put(validItem).put(invalidTypeItemField)).parse());
    }

    @Test
    public void parse_ignoresInvalidTypeInOptionalSha1Field() throws JSONException {
        JSONObject itemWithBadSha1 = new JSONObject()
                .put(ModelFields.GameArchiveItem.item, "bundle.js")
                .put(ModelFields.GameArchiveItem.sha1, new JSONArray());

        List<GameArchiveItem> result = new ToGameArchiveItemList(new JSONArray().put(itemWithBadSha1)).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("bundle.js", result.get(0).item);
        assertNull(result.get(0).sha1);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToGameArchiveItemParser() throws JSONException {
        ToGameArchiveItemList listParser = new ToGameArchiveItemList(new JSONArray());
        JSONObject itemJson = new JSONObject()
                .put(ModelFields.GameArchiveItem.item, "sprite.png");

        ToGameArchiveItem itemParser = listParser.createParserInstance(itemJson);

        assertNotNull(itemParser);
        GameArchiveItem parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals("sprite.png", parsed.item);
    }
}
