package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FromProductCartOfferTest {

    @Test
    public void parseToJSONObject_returnsValidJsonObject_whenModelIsPopulated() throws Exception {
        ProductCartOffer model = new ProductCartOffer();
        model.offerId = "offer_123";
        model.name = "Test Offer";
        model.price = "50.00";
        model.adult = true;
        model.quantity = 5;
        
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("http://example.com/img1.jpg");
        model.imageUrls = imageUrls;

        FromProductCartOffer parser = new FromProductCartOffer(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals("offer_123", result.getString(ModelFields.ProductCartOffer.offerId));
        assertEquals("Test Offer", result.getString(ModelFields.ProductCartOffer.name));
        assertEquals("50.00", result.getString(ModelFields.ProductCartOffer.price));
        assertTrue(result.getBoolean(ModelFields.ProductCartOffer.adult));
        assertEquals(5, result.getInt(ModelFields.ProductCartOffer.quantity));
        
        JSONArray urlsJson = result.getJSONArray(ModelFields.ProductCartOffer.imageUrls);
        assertEquals(1, urlsJson.length());
        assertEquals("http://example.com/img1.jpg", urlsJson.getString(0));
    }

    @Test(expected = NullPointerException.class)
    public void parseToJSONObject_throwsNPE_whenModelIsNull() throws Exception {
        FromProductCartOffer parser = new FromProductCartOffer(null);
        parser.parseToJSONObject();
    }

    @Test
    public void parseToJSONObject_handlesNullFields() throws Exception {
        ProductCartOffer model = new ProductCartOffer();
        model.offerId = null;
        model.name = null;
        model.imageUrls = null;

        FromProductCartOffer parser = new FromProductCartOffer(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        // JSONObject.put с null значением не добавляет ключ
        assertFalse(result.has(ModelFields.ProductCartOffer.offerId));
        assertFalse(result.has(ModelFields.ProductCartOffer.name));
        assertFalse(result.has(ModelFields.ProductCartOffer.imageUrls));
    }

    @Test
    public void parseToJSONObject_handlesEmptyImageUrlsList() throws Exception {
        ProductCartOffer model = new ProductCartOffer();
        model.offerId = "offer_1";
        model.imageUrls = new ArrayList<>(); // Пустой список

        FromProductCartOffer parser = new FromProductCartOffer(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        JSONArray urlsJson = result.getJSONArray(ModelFields.ProductCartOffer.imageUrls);
        assertEquals(0, urlsJson.length());
    }
}