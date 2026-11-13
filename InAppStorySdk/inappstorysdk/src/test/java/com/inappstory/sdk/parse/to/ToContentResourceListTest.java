package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.ContentResource;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToContentResourceListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsContentResourceList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject res1 = new JSONObject();
        res1.put(ModelFields.ContentResource.url, "https://example.com");
        res1.put(ModelFields.ContentResource.type, "video");

        JSONObject res2 = new JSONObject();
        res2.put(ModelFields.ContentResource.url, "https://example.com");
        res2.put(ModelFields.ContentResource.index, 1);

        JSONArray array = new JSONArray();
        array.put(res1);
        array.put(res2);

        List<ContentResource> result = new ToContentResourceList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("https://example.com", result.get(0).url);
        assertEquals("video", result.get(0).type);

        assertEquals("https://example.com", result.get(1).url);
        assertEquals(Integer.valueOf(1), result.get(1).index);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<ContentResource> result = new ToContentResourceList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных типов элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToContentResourceList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.ContentResource.url, "https://example.com"));
        arrayWithString.put("invalid_string_element");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToContentResourceList(arrayWithString).parse());
        assertNull(new ToContentResourceList(arrayWithNumber).parse());
        assertNull(new ToContentResourceList(arrayWithNestedArray).parse());
        assertNull(new ToContentResourceList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных данных внутри элементов ContentResource ---

    @Test
    public void parse_returnsNull_whenAnyItemMissingRequiredUrlOrHasInvalidUrlType() throws JSONException {
        JSONObject validItem = new JSONObject()
                .put(ModelFields.ContentResource.url, "https://example.com");

        JSONObject missingUrlItem = new JSONObject()
                .put(ModelFields.ContentResource.type, "image");

        JSONObject intUrlItem = new JSONObject()
                .put(ModelFields.ContentResource.url, 12345);

        JSONObject objectUrlItem = new JSONObject()
                .put(ModelFields.ContentResource.url, new JSONObject());

        assertNull(new ToContentResourceList(new JSONArray().put(validItem).put(missingUrlItem)).parse());
        assertNull(new ToContentResourceList(new JSONArray().put(validItem).put(intUrlItem)).parse());
        assertNull(new ToContentResourceList(new JSONArray().put(validItem).put(objectUrlItem)).parse());
    }

    @Test
    public void parse_ignoresInvalidTypesInOptionalResourceFields() throws JSONException {
        JSONObject itemWithBadOptionalFields = new JSONObject();
        itemWithBadOptionalFields.put(ModelFields.ContentResource.url, "https://example.com");
        itemWithBadOptionalFields.put(ModelFields.ContentResource.index, "not_an_int");
        itemWithBadOptionalFields.put(ModelFields.ContentResource.rangeStart, "not_a_long");
        itemWithBadOptionalFields.put(ModelFields.ContentResource.rangeEnd, new JSONArray());

        List<ContentResource> result = new ToContentResourceList(
                new JSONArray().put(itemWithBadOptionalFields)
        ).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://example.com", result.get(0).url);
        assertNull(result.get(0).index);
        assertNull(result.get(0).rangeStart);
        assertNull(result.get(0).rangeEnd);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToContentResourceParser() throws JSONException {
        ToContentResourceList listParser = new ToContentResourceList(new JSONArray());
        JSONObject itemJson = new JSONObject()
                .put(ModelFields.ContentResource.url, "https://example.com");

        ToContentResource itemParser = listParser.createParserInstance(itemJson);

        assertNotNull(itemParser);
        ContentResource parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals("https://example.com", parsed.url);
    }
}
