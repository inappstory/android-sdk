package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.goods.outercallbacks.ProductCart;
import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FromProductCartTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsFullyPopulated() throws Exception {
        ProductCart model = new ProductCart();
        model.price = "100.00";
        model.priceCurrency = "USD";
        model.oldPrice = "150.00";
        
        List<ProductCartOffer> offers = new ArrayList<>();
        ProductCartOffer offer = new ProductCartOffer();
        offer.offerId = "offer_1";
        offers.add(offer);
        model.offers = offers;

        FromProductCart parser = new FromProductCart(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("100.00", json.getString(ModelFields.ProductCart.price));
        assertEquals("USD", json.getString(ModelFields.ProductCart.priceCurrency));
        assertEquals("150.00", json.getString(ModelFields.ProductCart.oldPrice));
        
        JSONArray offersJson = json.getJSONArray(ModelFields.ProductCart.offers);
        assertEquals(1, offersJson.length());
        assertEquals("offer_1", offersJson.getJSONObject(0).getString(ModelFields.ProductCartOffer.offerId));
    }

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromProductCart parser = new FromProductCart(null);
        // ModelToStringParser перехватывает NPE и возвращает null
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesNullOffersGracefully() throws Exception {
        ProductCart model = new ProductCart();
        model.price = "100.00";
        model.offers = null; // Явный null

        FromProductCart parser = new FromProductCart(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("100.00", json.getString(ModelFields.ProductCart.price));
        // Ключ offers не должен быть добавлен в JSON
        assertFalse(json.has(ModelFields.ProductCart.offers));
    }

    @Test
    public void parse_handlesEmptyOffersList() throws Exception {
        ProductCart model = new ProductCart();
        model.price = "100.00";
        model.offers = new ArrayList<>(); // Пустой список

        FromProductCart parser = new FromProductCart(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        JSONArray offersJson = json.getJSONArray(ModelFields.ProductCart.offers);
        assertEquals(0, offersJson.length());
    }
}