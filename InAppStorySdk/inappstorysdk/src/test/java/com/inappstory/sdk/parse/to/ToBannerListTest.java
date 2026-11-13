package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToBannerListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsBannerList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject banner1 = new JSONObject();
        banner1.put(ModelFields.Banner.id, 10);
        banner1.put(ModelFields.Banner.hasLimit, true);

        JSONObject banner2 = new JSONObject();
        banner2.put(ModelFields.Banner.id, 20);
        banner2.put(ModelFields.Banner.disableClose, true);

        JSONArray array = new JSONArray();
        array.put(banner1);
        array.put(banner2);

        List<Banner> result = new ToBannerList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(10, result.get(0).id);
        assertTrue(result.get(0).hasLimit);

        assertEquals(20, result.get(1).id);
        assertTrue(result.get(1).disableClose);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<Banner> result = new ToBannerList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToBannerList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.Banner.id, 1));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToBannerList(arrayWithString).parse());
        assertNull(new ToBannerList(arrayWithNumber).parse());
        assertNull(new ToBannerList(arrayWithNestedArray).parse());
        assertNull(new ToBannerList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных данных внутри элементов Banner ---

    @Test
    public void parse_returnsNull_whenAnyBannerMissingRequiredId() throws JSONException {
        JSONObject validBanner = new JSONObject().put(ModelFields.Banner.id, 1);
        JSONObject invalidBanner = new JSONObject().put(ModelFields.Banner.hasLimit, true);

        JSONArray array = new JSONArray();
        array.put(validBanner);
        array.put(invalidBanner);

        // ToBanner вернёт null для второго элемента -> ToListParser вернёт null для всего списка
        assertNull(new ToBannerList(array).parse());
    }

    @Test
    public void parse_returnsNull_whenAnyBannerHasInvalidIdType() throws JSONException {
        JSONObject validBanner = new JSONObject().put(ModelFields.Banner.id, 1);

        JSONObject stringIdBanner = new JSONObject().put(ModelFields.Banner.id, "invalid_id");
        JSONObject objectIdBanner = new JSONObject().put(ModelFields.Banner.id, new JSONObject());
        JSONObject nullIdBanner = new JSONObject().put(ModelFields.Banner.id, JSONObject.NULL);

        assertNull(new ToBannerList(new JSONArray().put(validBanner).put(stringIdBanner)).parse());
        assertNull(new ToBannerList(new JSONArray().put(validBanner).put(objectIdBanner)).parse());
        assertNull(new ToBannerList(new JSONArray().put(validBanner).put(nullIdBanner)).parse());
    }

    @Test
    public void parse_ignoresInvalidTypesInOptionalBannerFields() throws JSONException {
        JSONObject bannerWithBadOptionalFields = new JSONObject();
        bannerWithBadOptionalFields.put(ModelFields.Banner.id, 99);
        bannerWithBadOptionalFields.put(ModelFields.Banner.hasLimit, "not_boolean");
        bannerWithBadOptionalFields.put(ModelFields.Banner.frequencyLimit, "not_long");
        bannerWithBadOptionalFields.put(ModelFields.Banner.slides, "not_array");

        JSONArray array = new JSONArray().put(bannerWithBadOptionalFields);

        List<Banner> result = new ToBannerList(array).parse();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(99, result.get(0).id);
        assertFalse(result.get(0).hasLimit);
        assertNull(result.get(0).frequencyLimit);
        assertNull(result.get(0).slides);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToBannerParser() throws JSONException {
        ToBannerList listParser = new ToBannerList(new JSONArray());
        JSONObject bannerJson = new JSONObject().put(ModelFields.Banner.id, 5);

        ToBanner itemParser = listParser.createParserInstance(bannerJson);

        assertNotNull(itemParser);
        Banner parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals(5, parsed.id);
    }
}