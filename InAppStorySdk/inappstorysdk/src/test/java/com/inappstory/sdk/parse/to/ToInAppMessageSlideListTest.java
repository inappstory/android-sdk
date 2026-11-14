package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.InAppMessageSlide;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToInAppMessageSlideListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsSlideList_whenValidJsonArrayProvided() throws JSONException {
        JSONObject slide1 = new JSONObject();
        slide1.put(ModelFields.InAppMessageSlide.slideIndex, 1);
        slide1.put(ModelFields.InAppMessageSlide.html, "<p>Slide 1</p>");

        JSONObject slide2 = new JSONObject();
        slide2.put(ModelFields.InAppMessageSlide.slideIndex, 2);
        slide2.put(ModelFields.InAppMessageSlide.duration, 3000);

        JSONArray array = new JSONArray();
        array.put(slide1);
        array.put(slide2);

        List<InAppMessageSlide> result = new ToInAppMessageSlideList(array).parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).slideIndex);
        assertEquals("<p>Slide 1</p>", result.get(0).html);
        assertEquals(2, result.get(1).slideIndex);
        assertEquals(3000, result.get(1).duration);
    }

    @Test
    public void parse_returnsEmptyList_whenEmptyJsonArrayProvided() {
        List<InAppMessageSlide> result = new ToInAppMessageSlideList(new JSONArray()).parse();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных элементов массива ---

    @Test
    public void parse_returnsNull_whenConstructedWithNull() {
        assertNull(new ToInAppMessageSlideList(null).parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.InAppMessageSlide.slideIndex, 1));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        assertNull(new ToInAppMessageSlideList(arrayWithString).parse());
        assertNull(new ToInAppMessageSlideList(arrayWithNumber).parse());
        assertNull(new ToInAppMessageSlideList(arrayWithNestedArray).parse());
        assertNull(new ToInAppMessageSlideList(arrayWithJsonNull).parse());
    }

    // --- Проверка фабричного метода createParserInstance ---

    @Test
    public void createParserInstance_returnsValidToInAppMessageSlideParser() throws JSONException {
        ToInAppMessageSlideList listParser = new ToInAppMessageSlideList(new JSONArray());
        JSONObject slideJson = new JSONObject().put(ModelFields.InAppMessageSlide.slideIndex, 5);

        ToInAppMessageSlide itemParser = listParser.createParserInstance(slideJson);

        assertNotNull(itemParser);
        InAppMessageSlide parsed = itemParser.parse();
        assertNotNull(parsed);
        assertEquals(5, parsed.slideIndex);
    }
}