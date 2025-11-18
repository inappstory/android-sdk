package com.inappstory.sdk.goods.outercallbacks;

public interface GoodsAddToCartProcessCallback {
    void onSuccess();

    void onError(String reason);
}
