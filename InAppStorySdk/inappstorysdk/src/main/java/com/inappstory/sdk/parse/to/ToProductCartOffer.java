package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToProductCartOffer extends ToModelParser<ProductCartOffer> {
    public ToProductCartOffer(JSONObject value) {
        super(value);
    }

    @Override
    public ProductCartOffer parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            ProductCartOffer offer = new ProductCartOffer();

            offer.offerId = jsonObj.optStringNullable("offerId");
            offer.groupId = jsonObj.optStringNullable("groupId");
            offer.name = jsonObj.optStringNullable("name");
            offer.description = jsonObj.optStringNullable("description");
            offer.url = jsonObj.optStringNullable("url");
            offer.coverUrl = jsonObj.optStringNullable("coverUrl");
            offer.currency = jsonObj.optStringNullable("currency");
            offer.price = jsonObj.optStringNullable("price");
            offer.oldPrice = jsonObj.optStringNullable("oldPrice");
            offer.size = jsonObj.optStringNullable("size");
            offer.color = jsonObj.optStringNullable("color");
            offer.adult = jsonObj.optBooleanNullable("adult");
            offer.quantity = jsonObj.optInt("quantity");
            offer.availability = jsonObj.optInt("availability");
            offer.imageUrls = new ToStringList(jsonObj.optJSONArray("imageUrls")).parse();
            return offer;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ProductCartOffer";
    }
}
