package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToImageListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsImageList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject img1 = new JSONObject();
        img1.put(ModelFields.Image.url, "https://example.com");
        img1.put(ModelFields.Image.width, 800);
        img1.put(ModelFields.Image.height, 600);

        JSONObject img2 = new JSONObject();
        img2.put(ModelFields.Image.url, "https://example.com");
        img2.put(ModelFields.Image.type, "png");

        JSONArray array = new JSONArray();
        array.put(img1);
        array.put(img2);

        List<Image> result = new ToImageList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("https://example.com", result.get(0).url);
        assertEquals(800, result.get(0).width);
        assertEquals(600, result.get(0).height);

        assertEquals("https://example.com", result.get(1).url);
        assertEquals("png", result.get(1).type);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<Image> result = new ToImageList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных типов элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToImageList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.Image.url, "https://example.com"));
        arrayWithString.put("invalid_string_element");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToImageList(arrayWithString).parse());
        assertNull(new ToImageList(arrayWithNumber).parse());
        assertNull(new ToImageList(arrayWithNestedArray).parse());
        assertNull(new ToImageList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных данных внутри элементов Image ---

    @Test
    public void parse_returnsNull_whenAnyImageMissingRequiredUrlOrHasInvalidUrlType() throws JSONException {
        JSONObject validImage = new JSONObject()
                .put(ModelFields.Image.url, "https://example.com");

        JSONObject missingUrlImage = new JSONObject()
                .put(ModelFields.Image.width, 100);

        JSONObject intUrlImage = new JSONObject()
                .put(ModelFields.Image.url, 12345);

        JSONObject objectUrlImage = new JSONObject()
                .put(ModelFields.Image.url, new JSONObject());

        assertNull(new ToImageList(new JSONArray().put(validImage).put(missingUrlImage)).parse());
        assertNull(new ToImageList(new JSONArray().put(validImage).put(intUrlImage)).parse());
        assertNull(new ToImageList(new JSONArray().put(validImage).put(objectUrlImage)).parse());
    }

    @Test
    public void parse_ignoresInvalidTypesInOptionalImageFields() throws JSONException {
        JSONObject imageWithBadOptionalFields = new JSONObject();
        imageWithBadOptionalFields.put(ModelFields.Image.url, "https://example.com");
        imageWithBadOptionalFields.put(ModelFields.Image.width, "bad_width");
        imageWithBadOptionalFields.put(ModelFields.Image.height, true);
        imageWithBadOptionalFields.put(ModelFields.Image.type, new JSONArray());
        imageWithBadOptionalFields.put(ModelFields.Image.expire, "bad_expire");

        List<Image> result = new ToImageList(new JSONArray().put(imageWithBadOptionalFields)).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://example.com", result.get(0).url);
        assertEquals(0, result.get(0).width);
        assertEquals(0, result.get(0).height);
        assertNull(result.get(0).type);
        assertEquals(0, result.get(0).expire);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToImageParser() throws JSONException {
        ToImageList listParser = new ToImageList(new JSONArray());
        JSONObject imageJson = new JSONObject()
                .put(ModelFields.Image.url, "https://example.com");

        ToImage itemParser = listParser.createParserInstance(imageJson);

        assertNotNull(itemParser);
        Image parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals("https://example.com", parsed.url);
    }
}
