package com.inappstory.sdk.core.banners;

public interface IBannerPlaceDotNavigation {
    int dotSize(); // in pt, default = 6pt, gap is equal to size
    String color();
    String activeColor();
    int position(); //1 - inside bottom, 2 - outside bottom
}
