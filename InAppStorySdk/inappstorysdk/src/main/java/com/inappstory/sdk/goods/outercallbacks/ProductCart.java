package com.inappstory.sdk.goods.outercallbacks;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.ArrayList;
import java.util.List;

public class ProductCart {
    @SerializedName(ModelFields.ProductCart.offers)
    public List<ProductCartOffer> offers;
    @SerializedName(ModelFields.ProductCart.price)
    public String price;
    @SerializedName(ModelFields.ProductCart.oldPrice)
    public String oldPrice;
    @SerializedName(ModelFields.ProductCart.priceCurrency)
    public String priceCurrency;

    public ProductCart offers(List<ProductCartOffer> offers) {
        if (offers == null)
            this.offers = new ArrayList<>();
        else
            this.offers = new ArrayList<>(offers);
        return this;
    }

    public ProductCart price(String price) {
        this.price = price;
        return this;
    }

    public ProductCart oldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
        return this;
    }

    public ProductCart priceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
        return this;
    }
}
