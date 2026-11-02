package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;

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
        object.put("offerId", model.offerId);
        object.put("groupId", model.groupId);
        object.put("name", model.name);
        object.put("description", model.description);
        object.put("url", model.url);
        object.put("coverUrl", model.coverUrl);
        object.put("currency", model.currency);
        object.put("price", model.price);
        object.put("oldPrice", model.oldPrice);
        object.put("adult", model.adult);
        object.put("availability", model.availability);
        object.put("size", model.size);
        object.put("color", model.color);
        object.put("quantity", model.quantity);
        if (model.imageUrls != null) {
            JSONArray imageUrls = new JSONArray();
            for (String imageUrl : model.imageUrls) {
                imageUrls.put(imageUrl);
            }
            object.put("imageUrls", imageUrls);
        }
        return object;
    }
}
