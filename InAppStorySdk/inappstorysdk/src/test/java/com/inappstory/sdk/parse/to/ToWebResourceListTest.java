package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.WebResource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToWebResourceListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsWebResourceList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject resource1 = new JSONObject();
        resource1.put(ModelFields.WebResource.url, "https://example.com/1.zip");
        resource1.put(ModelFields.WebResource.key, "key_1");
        resource1.put(ModelFields.WebResource.size, 100L);

        JSONObject resource2 = new JSONObject();
        resource2.put(ModelFields.WebResource.url, "https://example.com/2.zip");
        resource2.put(ModelFields.WebResource.key, "key_2");
        resource2.put(ModelFields.WebResource.sha1, "abc123");

        JSONArray array = new JSONArray();
        array.put(resource1);
        array.put(resource2);

        List<WebResource> result = new ToWebResourceList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("https://example.com/1.zip", result.get(0).url);
        assertEquals("key_1", result.get(0).key);
        assertEquals(100L, result.get(0).size);

        assertEquals("https://example.com/2.zip", result.get(1).url);
        assertEquals("key_2", result.get(1).key);
        assertEquals("abc123", result.get(1).sha1);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<WebResource> result = new ToWebResourceList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToWebResourceList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.WebResource.url, "url").put(ModelFields.WebResource.key, "key"));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToWebResourceList(arrayWithString).parse());
        assertNull(new ToWebResourceList(arrayWithNumber).parse());
        assertNull(new ToWebResourceList(arrayWithNestedArray).parse());
        assertNull(new ToWebResourceList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных данных внутри элементов WebResource ---

    @Test
    public void parse_returnsNull_whenAnyWebResourceMissingRequiredUrlOrKey() throws JSONException {
        JSONObject validResource = new JSONObject()
                .put(ModelFields.WebResource.url, "https://example.com/1.zip")
                .put(ModelFields.WebResource.key, "key_1");
                
        JSONObject missingUrl = new JSONObject().put(ModelFields.WebResource.key, "key_2");
        JSONObject missingKey = new JSONObject().put(ModelFields.WebResource.url, "https://example.com/2.zip");

        JSONArray arrayMissingUrl = new JSONArray().put(validResource).put(missingUrl);
        JSONArray arrayMissingKey = new JSONArray().put(validResource).put(missingKey);

        // ToWebResource вернёт null для элемента с отсутствующим обязательным полем 
        // -> ToListParser вернёт null для всего списка
        assertNull(new ToWebResourceList(arrayMissingUrl).parse());
        assertNull(new ToWebResourceList(arrayMissingKey).parse());
    }

    @Test
    public void parse_returnsNull_whenAnyWebResourceHasInvalidRequiredFieldType() throws JSONException {
        JSONObject validResource = new JSONObject()
                .put(ModelFields.WebResource.url, "https://example.com/1.zip")
                .put(ModelFields.WebResource.key, "key_1");

        JSONObject stringUrlResource = new JSONObject()
                .put(ModelFields.WebResource.url, 123)
                .put(ModelFields.WebResource.key, "key_2");
                
        JSONObject objectKeyResource = new JSONObject()
                .put(ModelFields.WebResource.url, "https://example.com/2.zip")
                .put(ModelFields.WebResource.key, new JSONObject());

        assertNull(new ToWebResourceList(new JSONArray().put(validResource).put(stringUrlResource)).parse());
        assertNull(new ToWebResourceList(new JSONArray().put(validResource).put(objectKeyResource)).parse());
    }

    @Test
    public void parse_ignoresInvalidTypesInOptionalWebResourceFields() throws JSONException {
        JSONObject resourceWithBadOptionalFields = new JSONObject();
        resourceWithBadOptionalFields.put(ModelFields.WebResource.url, "https://example.com/valid.zip");
        resourceWithBadOptionalFields.put(ModelFields.WebResource.key, "valid_key");
        resourceWithBadOptionalFields.put(ModelFields.WebResource.sha1, 999); // должен быть строкой
        resourceWithBadOptionalFields.put(ModelFields.WebResource.size, "not_a_long"); // должен быть числом

        JSONArray array = new JSONArray().put(resourceWithBadOptionalFields);

        List<WebResource> result = new ToWebResourceList(array).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://example.com/valid.zip", result.get(0).url);
        assertEquals("valid_key", result.get(0).key);
        assertNull(result.get(0).sha1);
        assertEquals(0L, result.get(0).size);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToWebResourceParser() throws JSONException {
        ToWebResourceList listParser = new ToWebResourceList(new JSONArray());
        JSONObject resourceJson = new JSONObject()
                .put(ModelFields.WebResource.url, "https://example.com/test.zip")
                .put(ModelFields.WebResource.key, "test_key");

        ToWebResource itemParser = listParser.createParserInstance(resourceJson);

        assertNotNull(itemParser);
        WebResource parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals("https://example.com/test.zip", parsed.url);
        assertEquals("test_key", parsed.key);
    }
}