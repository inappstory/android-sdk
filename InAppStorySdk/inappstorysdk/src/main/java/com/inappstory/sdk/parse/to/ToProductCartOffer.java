package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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

            offer.offerId = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.offerId
            );
            offer.groupId = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.groupId
            );
            offer.name = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.name
            );
            offer.description = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.description
            );
            offer.url = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.url
            );
            offer.coverUrl = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.coverUrl
            );
            offer.currency = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.currency
            );
            offer.price = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.price
            );
            offer.oldPrice = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.oldPrice
            );
            offer.size = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.size
            );
            offer.color = jsonObj.optStringNullable(
                    ModelFields.ProductCartOffer.color
            );
            offer.adult = jsonObj.optBooleanNullable(
                    ModelFields.ProductCartOffer.adult
            );
            offer.quantity = jsonObj.optInt(
                    ModelFields.ProductCartOffer.quantity
            );
            offer.availability = jsonObj.optInt(
                    ModelFields.ProductCartOffer.availability
            );
            offer.imageUrls = new ToStringList(jsonObj.optJSONArray(
                    ModelFields.ProductCartOffer.imageUrls
            )).parse();
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
