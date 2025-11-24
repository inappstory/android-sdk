package com.inappstory.sdk.goods.outercallbacks;

import java.util.List;
import java.util.Map;

public class ProductCartOffer {
    public String offerId;
    public String groupId;
    public String name;
    public String description;
    public String url;
    public String coverUrl;
    public List<String> imageUrls;
    public String currency;
    public String price;
    public String oldPrice;
    public Boolean adult;
    public int availability;
    public String size;
    public String color;
    public int quantity;

    public ProductCartOffer offerId(String offerId) {
        this.offerId = offerId;
        return this;
    }

    public ProductCartOffer groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public ProductCartOffer name(String name) {
        this.name = name;
        return this;
    }

    public ProductCartOffer description(String description) {
        this.description = description;
        return this;
    }

    public ProductCartOffer url(String url) {
        this.url = url;
        return this;
    }

    public ProductCartOffer coverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    public ProductCartOffer imageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public ProductCartOffer currency(String currency) {
        this.currency = currency;
        return this;
    }

    public ProductCartOffer price(String price) {
        this.price = price;
        return this;
    }

    public ProductCartOffer oldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
        return this;
    }

    public ProductCartOffer adult(Boolean adult) {
        this.adult = adult;
        return this;
    }

    public ProductCartOffer availability(int availability) {
        this.availability = availability;
        return this;
    }

    public ProductCartOffer size(String size) {
        this.size = size;
        return this;
    }

    public ProductCartOffer color(String color) {
        this.color = color;
        return this;
    }

    public ProductCartOffer quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
}
