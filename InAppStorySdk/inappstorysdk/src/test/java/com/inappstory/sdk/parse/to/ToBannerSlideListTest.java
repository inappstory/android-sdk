package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.BannerSlide;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToBannerSlideListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsBannerSlideList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject slide1 = new JSONObject();
        slide1.put(ModelFields.BannerSlide.slideIndex, 0);
        slide1.put(ModelFields.BannerSlide.duration, 3000);
        slide1.put(ModelFields.BannerSlide.html, "<p>Slide 1</p>");

        JSONObject slide2 = new JSONObject();
        slide2.put(ModelFields.BannerSlide.slideIndex, 1);
        slide2.put(ModelFields.BannerSlide.duration, 4500);
        slide2.put(ModelFields.BannerSlide.html, "<p>Slide 2</p>");

        JSONArray array = new JSONArray();
        array.put(slide1);
        array.put(slide2);

        List<BannerSlide> result = new ToBannerSlideList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(0, result.get(0).slideIndex);
        assertEquals(3000, result.get(0).duration);
        assertEquals("<p>Slide 1</p>", result.get(0).html);

        assertEquals(1, result.get(1).slideIndex);
        assertEquals(4500, result.get(1).duration);
        assertEquals("<p>Slide 2</p>", result.get(1).html);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<BannerSlide> result = new ToBannerSlideList(new JSONArray()).parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных типов элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToBannerSlideList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.BannerSlide.slideIndex, 0));
        arrayWithString.put("invalid_string_element");

        JSONArray arrayWithNumber = new JSONArray().put(123);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToBannerSlideList(arrayWithString).parse());
        assertNull(new ToBannerSlideList(arrayWithNumber).parse());
        assertNull(new ToBannerSlideList(arrayWithNestedArray).parse());
        assertNull(new ToBannerSlideList(arrayWithJsonNull).parse());
    }

    // --- Проверка невалидных типов полей внутри элементов BannerSlide ---

    @Test
    public void parse_keepsSlideWithDefaultValues_whenSlideFieldsHaveInvalidTypes() throws JSONException {
        JSONObject slideWithInvalidFieldTypes = new JSONObject();
        slideWithInvalidFieldTypes.put(ModelFields.BannerSlide.slideIndex, "wrong_int");
        slideWithInvalidFieldTypes.put(ModelFields.BannerSlide.duration, false);
        slideWithInvalidFieldTypes.put(ModelFields.BannerSlide.slidePayload, "wrong_object");
        slideWithInvalidFieldTypes.put(ModelFields.BannerSlide.resources, 777);

        JSONArray array = new JSONArray().put(slideWithInvalidFieldTypes);

        List<BannerSlide> result = new ToBannerSlideList(array).parse();

        // ToBannerSlide использует opt-методы, поэтому слайд создаётся со значениями по умолчанию
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).slideIndex);
        assertEquals(0, result.get(0).duration);
        assertNull(result.get(0).slidePayload);
        assertNull(result.get(0).resources);
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToBannerSlideParser() throws JSONException {
        ToBannerSlideList listParser = new ToBannerSlideList(new JSONArray());
        JSONObject slideJson = new JSONObject().put(ModelFields.BannerSlide.slideIndex, 4);

        ToBannerSlide itemParser = listParser.createParserInstance(slideJson);

        assertNotNull(itemParser);
        BannerSlide parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals(4, parsed.slideIndex);
    }
}