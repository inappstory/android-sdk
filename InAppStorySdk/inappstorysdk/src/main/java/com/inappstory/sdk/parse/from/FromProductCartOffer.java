package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONObject;

public class FromProductCartOffer extends
        ModelToJSONObjectParser<ProductCartOffer> {
    public FromProductCartOffer(
            ProductCartOffer model
    ) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put(ModelFields.ProductCartOffer.offerId, model.offerId);
        object.put(ModelFields.ProductCartOffer.groupId, model.groupId);
        object.put(ModelFields.ProductCartOffer.name, model.name);
        object.put(ModelFields.ProductCartOffer.description, model.description);
        object.put(ModelFields.ProductCartOffer.url, model.url);
        object.put(ModelFields.ProductCartOffer.coverUrl, model.coverUrl);
        object.put(ModelFields.ProductCartOffer.currency, model.currency);
        object.put(ModelFields.ProductCartOffer.price, model.price);
        object.put(ModelFields.ProductCartOffer.oldPrice, model.oldPrice);
        object.put(ModelFields.ProductCartOffer.adult, model.adult);
        object.put(ModelFields.ProductCartOffer.availability, model.availability);
        object.put(ModelFields.ProductCartOffer.size, model.size);
        object.put(ModelFields.ProductCartOffer.color, model.color);
        object.put(ModelFields.ProductCartOffer.quantity, model.quantity);
        if (model.imageUrls != null) {
            JSONArray imageUrls = new JSONArray();
            for (String imageUrl : model.imageUrls) {
                imageUrls.put(imageUrl);
            }
            object.put(ModelFields.ProductCartOffer.imageUrls, imageUrls);
        }
        return object;
    }
}
