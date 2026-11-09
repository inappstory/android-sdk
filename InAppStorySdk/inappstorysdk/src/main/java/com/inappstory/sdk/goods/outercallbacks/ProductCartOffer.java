package com.inappstory.sdk.goods.outercallbacks;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.List;
import java.util.Map;

public class ProductCartOffer {
    @SerializedName(ModelFields.ProductCartOffer.offerId)
    public String offerId;
    @SerializedName(ModelFields.ProductCartOffer.groupId)
    public String groupId;
    @SerializedName(ModelFields.ProductCartOffer.name)
    public String name;
    @SerializedName(ModelFields.ProductCartOffer.description)
    public String description;
    @SerializedName(ModelFields.ProductCartOffer.url)
    public String url;
    @SerializedName(ModelFields.ProductCartOffer.coverUrl)
    public String coverUrl;
    @SerializedName(ModelFields.ProductCartOffer.imageUrls)
    public List<String> imageUrls;
    @SerializedName(ModelFields.ProductCartOffer.currency)
    public String currency;
    @SerializedName(ModelFields.ProductCartOffer.price)
    public String price;
    @SerializedName(ModelFields.ProductCartOffer.oldPrice)
    public String oldPrice;
    @SerializedName(ModelFields.ProductCartOffer.adult)
    public Boolean adult;
    @SerializedName(ModelFields.ProductCartOffer.availability)
    public int availability;
    @SerializedName(ModelFields.ProductCartOffer.size)
    public String size;
    @SerializedName(ModelFields.ProductCartOffer.color)
    public String color;
    @SerializedName(ModelFields.ProductCartOffer.quantity)
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
