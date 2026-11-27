package com.inappstory.sdk.core.banners;

public enum BannerGridAlign {
    TOP(0), BOTTOM(1), CENTER(2);

    private final int value;

    public int value() {
        return value;
    }

    BannerGridAlign(int value) {
        this.value = value;
    }

    public static BannerGridAlign fromValue(int value) {
        for (BannerGridAlign alignment : values()) {
            if (alignment.value == value) {
                return alignment;
            }
        }
        return TOP;
    }
}
