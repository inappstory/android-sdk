package com.inappstory.sdk.goods.outercallbacks;

import java.util.ArrayList;
import java.util.List;

public class ProductCart {
    public List<ProductCartOffer> offers;
    public String price;
    public String oldPrice;
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
