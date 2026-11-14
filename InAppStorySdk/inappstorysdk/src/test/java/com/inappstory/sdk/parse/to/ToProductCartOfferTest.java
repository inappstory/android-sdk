package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToProductCartOfferTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedOffer_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ProductCartOffer.offerId, "offer_123");
        json.put(ModelFields.ProductCartOffer.groupId, "group_1");
        json.put(ModelFields.ProductCartOffer.name, "Test Product");
        json.put(ModelFields.ProductCartOffer.description, "Test Description");
        json.put(ModelFields.ProductCartOffer.url, "https://example.com/product");
        json.put(ModelFields.ProductCartOffer.coverUrl, "https://example.com/cover.jpg");
        json.put(ModelFields.ProductCartOffer.currency, "USD");
        json.put(ModelFields.ProductCartOffer.price, "99.99");
        json.put(ModelFields.ProductCartOffer.oldPrice, "120.00");
        json.put(ModelFields.ProductCartOffer.size, "M");
        json.put(ModelFields.ProductCartOffer.color, "Red");
        json.put(ModelFields.ProductCartOffer.adult, true);
        json.put(ModelFields.ProductCartOffer.quantity, 5);
        json.put(ModelFields.ProductCartOffer.availability, 1);
        json.put(ModelFields.ProductCartOffer.imageUrls, new JSONArray().put("img1.jpg").put("img2.jpg"));

        ToProductCartOffer parser = new ToProductCartOffer(json);
        ProductCartOffer result = parser.parse();

        assertNotNull(result);
        assertEquals("offer_123", result.offerId);
        assertEquals("group_1", result.groupId);
        assertEquals("Test Product", result.name);
        assertEquals("Test Description", result.description);
        assertEquals("https://example.com/product", result.url);
        assertEquals("https://example.com/cover.jpg", result.coverUrl);
        assertEquals("USD", result.currency);
        assertEquals("99.99", result.price);
        assertEquals("120.00", result.oldPrice);
        assertEquals("M", result.size);
        assertEquals("Red", result.color);
        assertEquals(Boolean.TRUE, result.adult);
        assertEquals(5, result.quantity);
        assertEquals(1, result.availability);
        assertNotNull(result.imageUrls);
        assertEquals(2, result.imageUrls.size());
        assertEquals("img1.jpg", result.imageUrls.get(0));
    }

    @Test
    public void parse_returnsOfferWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToProductCartOffer parser = new ToProductCartOffer(json);
        ProductCartOffer result = parser.parse();

        assertNotNull(result);
        assertNull(result.offerId);
        assertNull(result.groupId);
        assertNull(result.name);
        assertNull(result.description);
        assertNull(result.url);
        assertNull(result.coverUrl);
        assertNull(result.currency);
        assertNull(result.price);
        assertNull(result.oldPrice);
        assertNull(result.size);
        assertNull(result.color);
        assertNull(result.adult); // optBooleanNullable возвращает null
        assertEquals(0, result.quantity); // optInt возвращает 0
        assertEquals(0, result.availability);
        assertNull(result.imageUrls);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToProductCartOffer(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToProductCartOffer parser = new ToProductCartOffer(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ProductCartOffer.offerId, 12345); // Ожидается строка
        json.put(ModelFields.ProductCartOffer.adult, "not_a_boolean");
        json.put(ModelFields.ProductCartOffer.quantity, "not_an_int");
        json.put(ModelFields.ProductCartOffer.availability, new JSONObject());
        json.put(ModelFields.ProductCartOffer.imageUrls, "not_an_array");

        ToProductCartOffer parser = new ToProductCartOffer(json);
        ProductCartOffer result = parser.parse();

        assertNotNull(result);
        assertNull(result.offerId);
        assertNull(result.adult);
        assertEquals(0, result.quantity);
        assertEquals(0, result.availability);
        assertNull(result.imageUrls);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToProductCartOffer parser = new ToProductCartOffer(new JSONObject());
        assertEquals("ProductCartOffer", parser.getModelClassName());
    }
}