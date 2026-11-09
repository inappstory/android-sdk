package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.goods.outercallbacks.ProductCart;
import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONObject;

public class FromProductCart extends ModelToStringParser<ProductCart> {
    public FromProductCart(ProductCart model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put(ModelFields.ProductCart.price, model.price);
        object.put(ModelFields.ProductCart.priceCurrency, model.priceCurrency);
        object.put(ModelFields.ProductCart.oldPrice, model.oldPrice);
        if (model.offers != null) {
            JSONArray offers = new JSONArray();
            for (ProductCartOffer offer : model.offers) {
                offers.put(new FromProductCartOffer(offer).parseToJSONObject());
            }
            object.put(ModelFields.ProductCart.offers, offers);
        }
        return object;
    }
}
