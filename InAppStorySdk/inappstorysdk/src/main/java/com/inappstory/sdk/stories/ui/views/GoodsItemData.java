package com.inappstory.sdk.stories.ui.views;

public class GoodsItemData {
    public String sku;
    public String title;
    public String description;
    public String image;

    public String price;
    public String oldPrice;

    public GoodsItemData(String sku, String title, String description, String image, String price, String oldPrice) {
        this.sku = sku;
        this.title = title;
        this.description = description;
        this.image = image;
        this.price = price;
        this.oldPrice = oldPrice;
    }

    @Override
    public String toString() {
        return "GoodsItemData{" +
                "sku='" + sku + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", price='" + price + '\'' +
                ", oldPrice='" + oldPrice + '\'' +
                '}';
    }
}
