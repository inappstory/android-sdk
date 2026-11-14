package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.goods.models.ProductCartUpdateJSData;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToProductCartUpdateJSDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedData_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject offerJson = new JSONObject();
        offerJson.put(ModelFields.ProductCartOffer.offerId, "offer_123");
        offerJson.put(ModelFields.ProductCartOffer.name, "Test Product");
        json.put(ModelFields.ProductCartUpdateJSData.offer, offerJson);

        ToProductCartUpdateJSData parser = new ToProductCartUpdateJSData(json.toString());
        ProductCartUpdateJSData result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.offer);
        assertEquals("offer_123", result.offer.offerId);
        assertEquals("Test Product", result.offer.name);
    }

    @Test
    public void parse_returnsDataWithNullOffer_whenKeyIsMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToProductCartUpdateJSData parser = new ToProductCartUpdateJSData(json.toString());
        ProductCartUpdateJSData result = parser.parse();

        assertNotNull(result);
        assertNull(result.offer);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToProductCartUpdateJSData((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToProductCartUpdateJSData("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToProductCartUpdateJSData("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypeForOffer_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем строку вместо объекта
        json.put(ModelFields.ProductCartUpdateJSData.offer, "not_an_object");

        ToProductCartUpdateJSData parser = new ToProductCartUpdateJSData(json.toString());
        ProductCartUpdateJSData result = parser.parse();

        // optJSONObject вернет null для строки, и ToProductCartOffer корректно обработает null
        assertNotNull(result);
        assertNull(result.offer);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToProductCartUpdateJSData parser = new ToProductCartUpdateJSData("{}");
        assertEquals("ProductCartUpdateJSData", parser.getModelClassName());
    }
}