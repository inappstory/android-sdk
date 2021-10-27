package com.inappstory.sdk.stories.ui.views.goodswidget;

public class GoodsItemData {
    public String sku;
    public String title;
    public String description;
    public String image;

    public String price;
    public String oldPrice;
    public Object raw;

    public GoodsItemData(String sku,
                         String title,
                         String description,
                         String image,
                         String price,
                         String oldPrice,
                         Object raw) {
        this.sku = sku;
        this.title = title;
        this.description = description;
        this.image = image;
        this.price = price;
        this.oldPrice = oldPrice;
        this.raw = raw;
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
