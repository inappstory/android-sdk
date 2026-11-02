package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.goods.outercallbacks.ProductCart;
import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;

import org.json.JSONArray;
import org.json.JSONObject;

public class FromProductCart extends ModelToStringParser<ProductCart> {
    public FromProductCart(ProductCart model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("price", model.price);
        object.put("priceCurrency", model.priceCurrency);
        object.put("oldPrice", model.oldPrice);
        if (model.offers != null) {
            JSONArray offers = new JSONArray();
            for (ProductCartOffer offer : model.offers) {
                offers.put(new FromProductCartOffer(offer).parseToJSONObject());
            }
            object.put("offers", offers);
        }
        return object;
    }
}
